#!/bin/bash

# Namesilo DNS Update Script for ECS Container
# Updates project.jcarl.net A record with container's public IP

set -euo pipefail

# Configuration
NAMESILO_API_KEY="15fc56289aa5698b5d7c41ce"
DOMAIN="jcarl.net"
SUBDOMAIN="project"
FULL_DOMAIN="${SUBDOMAIN}.${DOMAIN}"
TTL="3600"
AWS_REGION="us-west-2"
NAMESILO_API_BASE="https://www.namesilo.com/api"

# Logging functions
log_info() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [INFO] $*"
}

log_error() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR] $*" >&2
}

log_success() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [SUCCESS] $*"
}

# Function to get container's private IP from ECS metadata
get_container_private_ip() {
    log_info "Fetching container private IP from ECS metadata..."
    
    local metadata_uri="${ECS_CONTAINER_METADATA_URI_V4}/task"
    local private_ip
    
    if ! private_ip=$(curl -s "${metadata_uri}" | grep -o '"PrivateIPv4Addresses":\["[^"]*"' | cut -d'"' -f4); then
        log_error "Failed to retrieve private IP from ECS metadata"
        return 1
    fi
    
    if [[ -z "${private_ip}" ]]; then
        log_error "Private IP is empty"
        return 1
    fi
    
    log_info "Container private IP: ${private_ip}"
    echo "${private_ip}"
}

# Function to get public IP from EC2 network interface
get_public_ip_from_private() {
    local private_ip=$1
    log_info "Looking up public IP for private IP: ${private_ip}..."
    
    local public_ip
    if ! public_ip=$(aws ec2 describe-network-interfaces \
        --region "${AWS_REGION}" \
        --filters "Name=addresses.private-ip-address,Values=${private_ip}" \
        --query 'NetworkInterfaces[0].Association.PublicIp' \
        --output text 2>&1); then
        log_error "Failed to query AWS EC2 API: ${public_ip}"
        return 1
    fi
    
    if [[ -z "${public_ip}" || "${public_ip}" == "None" ]]; then
        log_error "No public IP found for private IP ${private_ip}"
        return 1
    fi
    
    log_success "Found public IP: ${public_ip}"
    echo "${public_ip}"
}

# Function to get DNS record ID from Namesilo
get_record_id() {
    log_info "Fetching DNS record ID for ${FULL_DOMAIN}..."
    
    local api_url="${NAMESILO_API_BASE}/dnsListRecords?version=1&type=xml&key=${NAMESILO_API_KEY}&domain=${DOMAIN}"
    local response
    
    if ! response=$(curl -s "${api_url}"); then
        log_error "Failed to fetch DNS records from Namesilo"
        return 1
    fi
    
    # Check for API errors
    local reply_code
    reply_code=$(echo "${response}" | grep -o '<code>[0-9]*</code>' | head -1 | sed 's/<[^>]*>//g')
    
    if [[ "${reply_code}" != "300" ]]; then
        local error_detail
        error_detail=$(echo "${response}" | grep -o '<detail>.*</detail>' | sed 's/<[^>]*>//g')
        log_error "Namesilo API error (code ${reply_code}): ${error_detail}"
        return 1
    fi
    
    # Extract record ID for the subdomain
    local record_id
    record_id=$(echo "${response}" | grep -A 5 "<host>${FULL_DOMAIN}</host>" | grep -o '<record_id>[^<]*</record_id>' | sed 's/<[^>]*>//g' | head -1)
    
    if [[ -z "${record_id}" ]]; then
        log_error "DNS record for ${FULL_DOMAIN} not found"
        return 1
    fi
    
    log_success "Found record ID: ${record_id}"
    echo "${record_id}"
}

# Function to update DNS record at Namesilo
update_dns_record() {
    local record_id=$1
    local new_ip=$2
    
    log_info "Updating DNS record ${record_id} to IP ${new_ip}..."
    
    local api_url="${NAMESILO_API_BASE}/dnsUpdateRecord"
    api_url="${api_url}?version=1&type=xml&key=${NAMESILO_API_KEY}"
    api_url="${api_url}&domain=${DOMAIN}&rrid=${record_id}"
    api_url="${api_url}&rrhost=${SUBDOMAIN}&rrvalue=${new_ip}&rrttl=${TTL}"
    
    local response
    if ! response=$(curl -s "${api_url}"); then
        log_error "Failed to update DNS record"
        return 1
    fi
    
    # Check for API errors
    local reply_code
    reply_code=$(echo "${response}" | grep -o '<code>[0-9]*</code>' | head -1 | sed 's/<[^>]*>//g')
    
    if [[ "${reply_code}" != "300" ]]; then
        local error_detail
        error_detail=$(echo "${response}" | grep -o '<detail>.*</detail>' | sed 's/<[^>]*>//g')
        log_error "Failed to update DNS record (code ${reply_code}): ${error_detail}"
        return 1
    fi
    
    log_success "DNS record updated successfully"
    return 0
}

# Function to verify DNS update
verify_dns_update() {
    local expected_ip=$1
    log_info "Verifying DNS update for ${FULL_DOMAIN}..."
    
    # Wait a moment for DNS to propagate
    sleep 5
    
    # Query Namesilo API to verify the update
    local api_url="${NAMESILO_API_BASE}/dnsListRecords?version=1&type=xml&key=${NAMESILO_API_KEY}&domain=${DOMAIN}"
    local response
    
    if ! response=$(curl -s "${api_url}"); then
        log_error "Failed to verify DNS update"
        return 1
    fi
    
    # Extract the current IP for our subdomain
    local current_ip
    current_ip=$(echo "${response}" | grep -A 5 "<host>${FULL_DOMAIN}</host>" | grep -o '<value>[^<]*</value>' | sed 's/<[^>]*>//g' | head -1)
    
    if [[ "${current_ip}" == "${expected_ip}" ]]; then
        log_success "DNS verification successful: ${FULL_DOMAIN} -> ${current_ip}"
        return 0
    else
        log_error "DNS verification failed: expected ${expected_ip}, got ${current_ip}"
        return 1
    fi
}

# Main execution
main() {
    log_info "Starting Namesilo DNS update for ${FULL_DOMAIN}"
    
    # Step 1: Get container's private IP
    local private_ip
    if ! private_ip=$(get_container_private_ip); then
        log_error "Failed to get container private IP"
        exit 1
    fi
    
    # Step 2: Get public IP from AWS
    local public_ip
    if ! public_ip=$(get_public_ip_from_private "${private_ip}"); then
        log_error "Failed to get public IP"
        exit 1
    fi
    
    # Step 3: Get DNS record ID
    local record_id
    if ! record_id=$(get_record_id); then
        log_error "Failed to get DNS record ID"
        exit 1
    fi
    
    # Step 4: Update DNS record
    if ! update_dns_record "${record_id}" "${public_ip}"; then
        log_error "Failed to update DNS record"
        exit 1
    fi
    
    # Step 5: Verify the update
    if ! verify_dns_update "${public_ip}"; then
        log_error "DNS verification failed"
        exit 1
    fi
    
    log_success "DNS update completed successfully for ${FULL_DOMAIN} -> ${public_ip}"
}

# Run main function
main "$@"

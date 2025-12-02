#!/bin/bash

################################################################################
# Namesilo DNS Update Script
# Purpose: Update Namesilo DNS record with container's public IP
################################################################################

set -euo pipefail

# Configuration
API_KEY="15fc56289aa5698b5d7c41ce"
DOMAIN="jcarl.net"
SUBDOMAIN="project"
RECORD_ID="8ad154e44ae9c94cd8f22be2bea457d2"
TTL="7207"

# Logging Functions
log_info() { echo "[$(date +'%Y-%m-%d %H:%M:%S')] [INFO] $1"; }
log_error() { echo "[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR] $1"; }
log_success() { echo "[$(date +'%Y-%m-%d %H:%M:%S')] [SUCCESS] $1"; }

# Step 1: Get Public IP
get_public_ip() {
    log_info "Step 1: Retrieving public IP..."
    
    # Method A: ECS Metadata (Preferred)
    if [[ -n "${ECS_CONTAINER_METADATA_URI_V4:-}" ]]; then
        local task_metadata_url="${ECS_CONTAINER_METADATA_URI_V4}/task"
        # We use grep/cut here to avoid needing 'jq' if you want to keep the image minimal,
        # though installing jq is safer.
        local public_ip
        public_ip=$(curl -s "$task_metadata_url" 2>/dev/null | grep -o '"PublicIPv4":"[^"]*"' | cut -d'"' -f4 | head -1)
        
        if [[ -n "$public_ip" && "$public_ip" != "null" ]]; then
            log_success "Public IP retrieved from task metadata: $public_ip"
            echo "$public_ip"
            return 0
        fi
    fi
    
    # Method B: External Service (Fallback)
    log_info "Task metadata unavailable. Using external IP service..."
    local public_ip
    public_ip=$(curl -s https://api.ipify.org 2>/dev/null || curl -s https://ifconfig.me 2>/dev/null || echo "")
    
    if [[ -z "$public_ip" ]]; then
        log_error "Failed to retrieve public IP address"
        exit 1
    fi
    
    log_success "Public IP retrieved from external service: $public_ip"
    echo "$public_ip"
}

# Step 2: Update Namesilo
update_namesilo_record() {
    local ip_address="$1"
    log_info "Step 2: Updating Namesilo DNS record for ${SUBDOMAIN}.${DOMAIN}..."
    
    local api_url="https://www.namesilo.com/api/dnsUpdateRecord?version=1&type=xml&key=${API_KEY}&domain=${DOMAIN}&rrid=${RECORD_ID}&rrhost=${SUBDOMAIN}&rrvalue=${ip_address}&rrttl=${TTL}"
    
    local response
    response=$(curl -s "$api_url")
    
    # Simple XML parsing to check for code 300 (Success)
    if echo "$response" | grep -q "<code>300</code>"; then
        log_success "Namesilo DNS record updated successfully to $ip_address"
    else
        log_error "Namesilo API Update Failed. Response: $response"
        exit 1
    fi
}

main() {
    local public_ip
    public_ip=$(get_public_ip)
    update_namesilo_record "$public_ip"
}

main "$@"
# ---- Build stage ---------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---- Runtime stage -------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

# Install curl and bash for DNS updates
RUN apk add --no-cache curl bash bind-tools

# Create the Namesilo DNS update script
RUN cat > /update-namesilo-dns.sh <<'EOF'
#!/bin/bash

################################################################################
# Namesilo DNS Update Script
# Purpose: Update Namesilo DNS record with container's public IP
# This script retrieves the container's public IP from ECS task metadata
# and updates the Namesilo DNS record via their API
################################################################################

set -euo pipefail

# Configuration
API_KEY="15fc56289aa5698b5d7c41ce"
DOMAIN="jcarl.net"
SUBDOMAIN="project"
RECORD_ID="8ad154e44ae9c94cd8f22be2bea457d2"
TTL="7207"
LOG_FILE="/var/log/namesilo-update.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

################################################################################
# Logging Functions
################################################################################

log_info() {
    local message="$1"
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [INFO] $message" | tee -a "$LOG_FILE"
}

log_error() {
    local message="$1"
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR] $message${NC}" | tee -a "$LOG_FILE"
}

log_success() {
    local message="$1"
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [SUCCESS] $message${NC}" | tee -a "$LOG_FILE"
}

log_warning() {
    local message="$1"
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [WARNING] $message${NC}" | tee -a "$LOG_FILE"
}

################################################################################
# Error Handling
################################################################################

error_exit() {
    local message="$1"
    local exit_code="${2:-1}"
    log_error "$message"
    exit "$exit_code"
}

trap 'error_exit "Script interrupted or failed unexpectedly" 1' ERR

################################################################################
# Step 1: Get Public IP from ECS Task Metadata
################################################################################

get_public_ip() {
    log_info "Step 1: Retrieving public IP from ECS task metadata..."
    
    # Try to get public IP from ECS task metadata endpoint
    if [[ -n "${ECS_CONTAINER_METADATA_URI_V4:-}" ]]; then
        local task_metadata_url="${ECS_CONTAINER_METADATA_URI_V4}/task"
        local metadata_response
        metadata_response=$(curl -s "$task_metadata_url" 2>/dev/null || echo "")
        
        if [[ -n "$metadata_response" ]]; then
            # Try to extract public IP from task metadata
            local public_ip
            public_ip=$(echo "$metadata_response" | grep -o '"PublicIPv4":"[^"]*"' | cut -d'"' -f4 | head -1)
            
            if [[ -n "$public_ip" && "$public_ip" != "null" ]]; then
                log_success "Public IP retrieved from task metadata: $public_ip"
                echo "$public_ip"
                return 0
            fi
        fi
    fi
    
    # Fallback: Use external service to get public IP
    log_info "Task metadata not available, using external IP service..."
    local public_ip
    public_ip=$(curl -s https://api.ipify.org 2>/dev/null || curl -s https://ifconfig.me 2>/dev/null || echo "")
    
    if [[ -z "$public_ip" ]]; then
        error_exit "Failed to retrieve public IP address"
    fi
    
    log_success "Public IP retrieved from external service: $public_ip"
    echo "$public_ip"
}

################################################################################
# Step 2: Update Namesilo DNS Record
################################################################################

update_namesilo_record() {
    local ip_address="$1"
    log_info "Step 2: Updating Namesilo DNS record for ${SUBDOMAIN}.${DOMAIN} with IP $ip_address..."
    
    # Build the API URL
    local api_url="https://www.namesilo.com/api/dnsUpdateRecord"
    api_url="${api_url}?version=1&type=xml&key=${API_KEY}"
    api_url="${api_url}&domain=${DOMAIN}&rrid=${RECORD_ID}"
    api_url="${api_url}&rrhost=${SUBDOMAIN}&rrvalue=${ip_address}&rrttl=${TTL}"
    
    # Make the API request
    local response
    response=$(curl -s "$api_url")
    
    if [[ -z "$response" ]]; then
        error_exit "Failed to get response from Namesilo API"
    fi
    
    # Parse the response code
    local reply_code
    reply_code=$(echo "$response" | grep -o '<code>[^<]*</code>' | sed 's/<[^>]*>//g' | head -1)
    
    local reply_detail
    reply_detail=$(echo "$response" | grep -o '<detail>[^<]*</detail>' | sed 's/<[^>]*>//g' | head -1)
    
    log_info "API Response Code: $reply_code"
    log_info "API Response Detail: $reply_detail"
    
    # Check if update was successful (code 300 = success)
    if [[ "$reply_code" == "300" ]]; then
        log_success "Namesilo DNS record updated successfully!"
        log_info "DNS record ${SUBDOMAIN}.${DOMAIN} now points to $ip_address (TTL: ${TTL}s)"
    else
        error_exit "Namesilo API returned error code $reply_code: $reply_detail"
    fi
}

################################################################################
# Step 3: Verify DNS Update
################################################################################

verify_dns_update() {
    local expected_ip="$1"
    log_info "Step 3: Verifying DNS update..."
    
    # Wait a moment for DNS propagation
    sleep 2
    
    # Query the DNS record (using nslookup since dig might not be available)
    local resolved_ip
    resolved_ip=$(nslookup "${SUBDOMAIN}.${DOMAIN}" 8.8.8.8 2>/dev/null | grep -A1 "Name:" | grep "Address:" | tail -1 | awk '{print $2}' || echo "")
    
    if [[ "$resolved_ip" == "$expected_ip" ]]; then
        log_success "DNS verification successful! ${SUBDOMAIN}.${DOMAIN} resolves to $resolved_ip"
        return 0
    else
        log_warning "DNS verification pending. Expected: $expected_ip, Got: $resolved_ip"
        log_info "DNS propagation may take a few moments (TTL: ${TTL}s). Namesilo update was successful."
        return 0
    fi
}

################################################################################
# Main Execution
################################################################################

main() {
    log_info "=========================================="
    log_info "Namesilo DNS Update Script Started"
    log_info "=========================================="
    log_info "Domain: ${SUBDOMAIN}.${DOMAIN}"
    log_info "Record ID: $RECORD_ID"
    log_info "TTL: ${TTL}s"
    log_info "=========================================="
    
    # Step 1: Get public IP
    local public_ip
    public_ip=$(get_public_ip)
    
    # Step 2: Update Namesilo DNS
    update_namesilo_record "$public_ip"
    
    # Step 3: Verify the update
    verify_dns_update "$public_ip"
    
    log_info "=========================================="
    log_success "Namesilo DNS Update Script Completed Successfully"
    log_info "=========================================="
}

# Run main function
main "$@"
EOF

RUN chmod +x /update-namesilo-dns.sh

# Create startup script that runs Namesilo DNS update then starts the app
RUN cat > /startup.sh <<EOF
#!/bin/bash
/update-namesilo-dns.sh || echo "WARNING: Namesilo DNS update failed, continuing anyway..."
exec java \
  -Xmx512m \
  -Xms256m \
  -XX:+UseContainerSupport \
  -Djava.security.egd=file:/dev/./urandom \
  -jar /app.jar
EOF

RUN chmod +x /startup.sh

ENTRYPOINT ["/startup.sh"]

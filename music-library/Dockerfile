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

# Install AWS CLI and jq for JSON parsing
RUN apk add --no-cache aws-cli jq curl

# Create the Route 53 update script
RUN cat > /update-route53.sh <<'EOF'
#!/bin/bash

################################################################################
# Route 53 DNS Update Script
# Purpose: Update Route 53 DNS record with container's public IP
# This script retrieves the container's private IP from ECS metadata,
# looks up the public IP from EC2 network interface, and updates Route 53
################################################################################

set -euo pipefail

# Configuration
DOMAIN="project.jcarl.net"
HOSTED_ZONE_ID="Z08164103KW73VKOVN6GY"
REGION="us-west-2"
TTL="300"
LOG_FILE="/var/log/route53-update.log"

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
# Step 1: Get Container Private IP from ECS Metadata
################################################################################

get_container_private_ip() {
    log_info "Step 1: Retrieving container private IP from ECS metadata..."
    
    # Check if ECS metadata endpoint is available
    if [[ -z "${ECS_CONTAINER_METADATA_URI:-}" ]]; then
        error_exit "ECS_CONTAINER_METADATA_URI environment variable not set. This script must run in an ECS container."
    fi
    
    # Retrieve container metadata
    local metadata_response
    metadata_response=$(curl -s "${ECS_CONTAINER_METADATA_URI}")
    
    if [[ -z "$metadata_response" ]]; then
        error_exit "Failed to retrieve ECS container metadata from ${ECS_CONTAINER_METADATA_URI}"
    fi
    
    # Extract private IP from metadata
    local private_ip
    private_ip=$(echo "$metadata_response" | grep -o '"IPv4Addresses":\s*\[\s*"[^"]*"' | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+' | head -1)
    
    if [[ -z "$private_ip" ]]; then
        error_exit "Failed to extract private IP from ECS metadata"
    fi
    
    log_success "Container private IP retrieved: $private_ip"
    echo "$private_ip"
}

################################################################################
# Step 2: Get Public IP from EC2 Network Interface
################################################################################

get_public_ip_from_eni() {
    local private_ip="$1"
    log_info "Step 2: Looking up public IP for private IP $private_ip using EC2 API..."
    
    # Get the network interface ID associated with the private IP
    local eni_id
    eni_id=$(aws ec2 describe-network-interfaces \
        --region "$REGION" \
        --filters "Name=private-ip-address,Values=$private_ip" \
        --query 'NetworkInterfaces[0].NetworkInterfaceId' \
        --output text 2>/dev/null)
    
    if [[ -z "$eni_id" || "$eni_id" == "None" ]]; then
        error_exit "Failed to find network interface for private IP $private_ip"
    fi
    
    log_info "Found network interface: $eni_id"
    
    # Get the public IP associated with the network interface
    local public_ip
    public_ip=$(aws ec2 describe-network-interfaces \
        --region "$REGION" \
        --network-interface-ids "$eni_id" \
        --query 'NetworkInterfaces[0].Association.PublicIp' \
        --output text 2>/dev/null)
    
    if [[ -z "$public_ip" || "$public_ip" == "None" ]]; then
        log_warning "No public IP associated with network interface $eni_id. Using private IP instead."
        public_ip="$private_ip"
    fi
    
    log_success "Public IP retrieved: $public_ip"
    echo "$public_ip"
}

################################################################################
# Step 3: Update Route 53 DNS Record
################################################################################

update_route53_record() {
    local ip_address="$1"
    log_info "Step 3: Updating Route 53 record for $DOMAIN with IP $ip_address..."
    
    # Create the change batch JSON for Route 53 update
    local change_batch
    change_batch=$(cat <<BATCH
{
    "Changes": [
        {
            "Action": "UPSERT",
            "ResourceRecordSet": {
                "Name": "$DOMAIN",
                "Type": "A",
                "TTL": $TTL,
                "ResourceRecords": [
                    {
                        "Value": "$ip_address"
                    }
                ]
            }
        }
    ]
}
BATCH
)
    
    # Update the Route 53 record
    local change_info
    change_info=$(aws route53 change-resource-record-sets \
        --hosted-zone-id "$HOSTED_ZONE_ID" \
        --change-batch "$change_batch" \
        --region "$REGION" \
        --output json 2>/dev/null)
    
    if [[ -z "$change_info" ]]; then
        error_exit "Failed to update Route 53 record"
    fi
    
    # Extract the change ID
    local change_id
    change_id=$(echo "$change_info" | grep -o '"Id":\s*"[^"]*"' | head -1 | grep -o '[^"]*$' | head -1)
    
    log_success "Route 53 record updated successfully. Change ID: $change_id"
    log_info "DNS record $DOMAIN now points to $ip_address (TTL: ${TTL}s)"
}

################################################################################
# Step 4: Verify DNS Update
################################################################################

verify_dns_update() {
    local expected_ip="$1"
    log_info "Step 4: Verifying DNS update..."
    
    # Wait a moment for DNS propagation
    sleep 2
    
    # Query the DNS record
    local resolved_ip
    resolved_ip=$(dig +short "$DOMAIN" @8.8.8.8 | tail -1)
    
    if [[ "$resolved_ip" == "$expected_ip" ]]; then
        log_success "DNS verification successful! $DOMAIN resolves to $resolved_ip"
        return 0
    else
        log_warning "DNS verification pending. Expected: $expected_ip, Got: $resolved_ip"
        log_info "DNS propagation may take a few moments. Route 53 update was successful."
        return 0
    fi
}

################################################################################
# Main Execution
################################################################################

main() {
    log_info "=========================================="
    log_info "Route 53 DNS Update Script Started"
    log_info "=========================================="
    log_info "Domain: $DOMAIN"
    log_info "Hosted Zone ID: $HOSTED_ZONE_ID"
    log_info "Region: $REGION"
    log_info "TTL: ${TTL}s"
    log_info "=========================================="
    
    # Step 1: Get container private IP
    local private_ip
    private_ip=$(get_container_private_ip)
    
    # Step 2: Get public IP from EC2 network interface
    local public_ip
    public_ip=$(get_public_ip_from_eni "$private_ip")
    
    # Step 3: Update Route 53
    update_route53_record "$public_ip"
    
    # Step 4: Verify the update
    verify_dns_update "$public_ip"
    
    log_info "=========================================="
    log_success "Route 53 DNS Update Script Completed Successfully"
    log_info "=========================================="
}

# Run main function
main "$@"
EOF

RUN chmod +x /update-route53.sh

# Create startup script that runs Route 53 update then starts the app
RUN cat > /startup.sh <<EOF
#!/bin/sh
/update-route53.sh || echo "WARNING: Route 53 update failed, continuing anyway..."
exec java \
  -Xmx512m \
  -Xms256m \
  -XX:+UseContainerSupport \
  -Djava.security.egd=file:/dev/./urandom \
  -jar /app.jar
EOF

RUN chmod +x /startup.sh

ENTRYPOINT ["/startup.sh"]

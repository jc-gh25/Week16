#!/bin/bash
set -euo pipefail

# Namesilo DNS Update Script
# Purpose: Update Namesilo DNS record with container's public IP

# Configuration
API_KEY="15fc56289aa5698b5d7c41ce"
DOMAIN="jcarl.net"
SUBDOMAIN="project"
FULL_HOST="project.jcarl.net"
TTL="3600"

log() { echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"; }

# 1. Get Public IP
log "Getting Public IP..."
PUBLIC_IP=$(curl -s https://api.ipify.org)
log "Public IP: $PUBLIC_IP"

# 2. Get the current DNS Record ID dynamically
log "Finding Record ID for $FULL_HOST..."
# Fetch all records
RECORDS=$(curl -s "https://www.namesilo.com/api/dnsListRecords?version=1&type=xml&key=${API_KEY}&domain=${DOMAIN}")

# Parse XML to find the record ID for our subdomain
# We look for the host tag, then grab the record_id associated with it
RECORD_ID=$(echo "$RECORDS" | grep -C 5 "<host>${FULL_HOST}</host>" | grep -o "<record_id>.*</record_id>" | cut -d'>' -f2 | cut -d'<' -f1)

if [[ -z "$RECORD_ID" ]]; then
    log "ERROR: Could not find existing DNS record for $FULL_HOST"
    log "API Response Dump: $RECORDS"
    exit 1
fi
log "Found Record ID: $RECORD_ID"

# 3. Update the Record
log "Updating DNS to $PUBLIC_IP..."
UPDATE_URL="https://www.namesilo.com/api/dnsUpdateRecord?version=1&type=xml&key=${API_KEY}&domain=${DOMAIN}&rrid=${RECORD_ID}&rrhost=${SUBDOMAIN}&rrvalue=${PUBLIC_IP}&rrttl=${TTL}"

RESPONSE=$(curl -s "$UPDATE_URL")

if echo "$RESPONSE" | grep -q "<code>300</code>"; then
    log "SUCCESS: DNS updated successfully!"
else
    log "ERROR: Update failed."
    log "Response: $RESPONSE"
    exit 1
fi
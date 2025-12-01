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

# Parse XML to find ALL record IDs for our subdomain (A records only)
RECORD_IDS=$(echo "$RECORDS" | grep -B 2 "<host>${FULL_HOST}</host>" | grep "<type>A</type>" -B 2 | grep -o "<record_id>.*</record_id>" | cut -d'>' -f2 | cut -d'<' -f1)

# Count how many records found
RECORD_COUNT=$(echo "$RECORD_IDS" | grep -c . || true)

if [[ $RECORD_COUNT -eq 0 ]]; then
    log "ERROR: Could not find existing DNS A record for $FULL_HOST"
    log "Creating new record instead..."
    # Fallback: Create new record
    CREATE_URL="https://www.namesilo.com/api/dnsAddRecord?version=1&type=xml&key=${API_KEY}&domain=${DOMAIN}&rrtype=A&rrhost=${SUBDOMAIN}&rrvalue=${PUBLIC_IP}&rrttl=${TTL}"
    RESPONSE=$(curl -s "$CREATE_URL")
    if echo "$RESPONSE" | grep -q "<code>300</code>"; then
        log "SUCCESS: New DNS record created!"
        exit 0
    else
        log "ERROR: Failed to create record. Response: $RESPONSE"
        exit 1
    fi
elif [[ $RECORD_COUNT -gt 1 ]]; then
    log "WARNING: Found $RECORD_COUNT duplicate A records for $FULL_HOST!"
    log "Record IDs: $RECORD_IDS"
    log "Using the first record ID and continuing..."
    RECORD_ID=$(echo "$RECORD_IDS" | head -n 1)
else
    RECORD_ID="$RECORD_IDS"
    log "Found Record ID: $RECORD_ID"
fi

# 3. Update the Record
log "Updating DNS to $PUBLIC_IP..."
UPDATE_URL="https://www.namesilo.com/api/dnsUpdateRecord?version=1&type=xml&key=${API_KEY}&domain=${DOMAIN}&rrid=${RECORD_ID}&rrhost=${SUBDOMAIN}&rrvalue=${PUBLIC_IP}&rrttl=${TTL}"

RESPONSE=$(curl -s "$UPDATE_URL")

if echo "$RESPONSE" | grep -q "<code>300</code>"; then
    log "SUCCESS: DNS updated successfully!"
    
    # If duplicates exist, warn the user
    if [[ $RECORD_COUNT -gt 1 ]]; then
        log "⚠️  WARNING: Duplicate DNS records still exist. Please manually delete them from Namesilo control panel."
    fi
else
    log "ERROR: Update failed."
    log "Response: $RESPONSE"
    exit 1
fi

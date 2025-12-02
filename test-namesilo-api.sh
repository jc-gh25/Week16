#!/bin/bash

# Namesilo API Test Script
# Tests API connection and lists DNS records for jcarl.net

echo "=========================================="
echo "Namesilo API Connection Test"
echo "=========================================="
echo ""

# Configuration
API_KEY="15fc56289aa5698b5d7c41ce"
DOMAIN="jcarl.net"
SUBDOMAIN="project.jcarl.net"
API_ENDPOINT="https://www.namesilo.com/api/dnsListRecords"

echo "Testing API connection..."
echo "Domain: $DOMAIN"
echo "Looking for subdomain: $SUBDOMAIN"
echo ""

# Make API request
RESPONSE=$(curl -s "${API_ENDPOINT}?version=1&type=xml&key=${API_KEY}&domain=${DOMAIN}")

# Check if curl succeeded
if [ $? -ne 0 ]; then
    echo "❌ ERROR: Failed to connect to Namesilo API"
    echo "Please check your internet connection"
    exit 1
fi

# Check if response is empty
if [ -z "$RESPONSE" ]; then
    echo "❌ ERROR: Received empty response from API"
    exit 1
fi

echo "Response received. Parsing..."
echo ""

# Extract reply code (300 = success)
REPLY_CODE=$(echo "$RESPONSE" | grep -oP '(?<=<code>)[^<]+' | head -1)
REPLY_DETAIL=$(echo "$RESPONSE" | grep -oP '(?<=<detail>)[^<]+' | head -1)

echo "API Response Code: $REPLY_CODE"
echo "API Response Detail: $REPLY_DETAIL"
echo ""

# Check if API call was successful
if [ "$REPLY_CODE" != "300" ]; then
    echo "❌ API ERROR: Request failed"
    echo ""
    echo "Common error codes:"
    echo "  110 = Invalid API key"
    echo "  280 = Invalid domain"
    echo "  300 = Success"
    echo ""
    echo "Full response:"
    echo "$RESPONSE"
    exit 1
fi

echo "✅ API Key is valid and working!"
echo ""
echo "=========================================="
echo "DNS Records for $DOMAIN"
echo "=========================================="
echo ""

# Parse and display all DNS records
echo "$RESPONSE" | grep -oP '(?<=<resource_record>).*?(?=</resource_record>)' | while IFS= read -r record; do
    RECORD_ID=$(echo "$record" | grep -oP '(?<=<record_id>)[^<]+')
    TYPE=$(echo "$record" | grep -oP '(?<=<type>)[^<]+')
    HOST=$(echo "$record" | grep -oP '(?<=<host>)[^<]+')
    VALUE=$(echo "$record" | grep -oP '(?<=<value>)[^<]+')
    TTL=$(echo "$record" | grep -oP '(?<=<ttl>)[^<]+')
    
    echo "Record ID: $RECORD_ID"
    echo "  Type: $TYPE"
    echo "  Host: $HOST"
    echo "  Value: $VALUE"
    echo "  TTL: $TTL"
    echo ""
done

# Look for the specific subdomain
echo "=========================================="
echo "Searching for $SUBDOMAIN..."
echo "=========================================="
echo ""

FOUND=false
echo "$RESPONSE" | grep -oP '(?<=<resource_record>).*?(?=</resource_record>)' | while IFS= read -r record; do
    HOST=$(echo "$record" | grep -oP '(?<=<host>)[^<]+')
    
    if [ "$HOST" = "$SUBDOMAIN" ]; then
        FOUND=true
        RECORD_ID=$(echo "$record" | grep -oP '(?<=<record_id>)[^<]+')
        TYPE=$(echo "$record" | grep -oP '(?<=<type>)[^<]+')
        VALUE=$(echo "$record" | grep -oP '(?<=<value>)[^<]+')
        TTL=$(echo "$record" | grep -oP '(?<=<ttl>)[^<]+')
        
        echo "✅ FOUND: $SUBDOMAIN"
        echo ""
        echo "📋 Record Details:"
        echo "  Record ID (rrid): $RECORD_ID  ⬅️  USE THIS FOR UPDATES"
        echo "  Type: $TYPE"
        echo "  Current IP: $VALUE"
        echo "  TTL: $TTL seconds"
        echo ""
        echo "To update this record, you'll need:"
        echo "  - Record ID: $RECORD_ID"
        echo "  - Domain: $DOMAIN"
        echo "  - API Key: (already configured)"
        exit 0
    fi
done

# Check if subdomain was found
if echo "$RESPONSE" | grep -q "<host>$SUBDOMAIN</host>"; then
    echo "✅ Record exists (see details above)"
else
    echo "⚠️  WARNING: No DNS record found for $SUBDOMAIN"
    echo ""
    echo "This subdomain does not exist yet. You may need to:"
    echo "  1. Create it manually in Namesilo dashboard, OR"
    echo "  2. Use the dnsAddRecord API endpoint to create it"
    echo ""
    echo "Available hosts in $DOMAIN:"
    echo "$RESPONSE" | grep -oP '(?<=<host>)[^<]+' | sort -u | while read -r host; do
        echo "  - $host"
    done
fi

echo ""
echo "=========================================="
echo "Test Complete"
echo "=========================================="

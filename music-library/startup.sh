#!/bin/bash
set -e

echo "Starting DNS Auto-Update..."
/update-namesilo-dns.sh || echo "WARNING: Namesilo DNS update failed, continuing anyway..."

echo "Starting Java Application on Port 80..."
exec java \
  -Xmx512m \
  -Xms256m \
  -XX:+UseContainerSupport \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=80 \
  -jar /app/app.jar

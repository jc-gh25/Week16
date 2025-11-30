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
RUN cat > /update-route53.sh << 'EOF'
#!/bin/sh
set -e

echo "Starting Route 53 DNS update..."

# Get the task's public IP from ECS metadata
METADATA_URI="${ECS_CONTAINER_METADATA_URI_V4}/task"
PUBLIC_IP=$(curl -s ${METADATA_URI} | jq -r '.Containers[0].Networks[0].IPv4Addresses[0]')

if [ -z "$PUBLIC_IP" ] || [ "$PUBLIC_IP" = "null" ]; then
    echo "ERROR: Could not retrieve public IP"
    exit 1
fi

echo "Current public IP: $PUBLIC_IP"

# Route 53 configuration
HOSTED_ZONE_ID="Z08164103KW73VKOVN6GY"
DOMAIN_NAME="project.jcarl.net"

# Create the JSON for Route 53 change batch
CHANGE_BATCH=$(cat <<BATCH
{
  "Changes": [{
    "Action": "UPSERT",
    "ResourceRecordSet": {
      "Name": "${DOMAIN_NAME}",
      "Type": "A",
      "TTL": 300,
      "ResourceRecords": [{"Value": "${PUBLIC_IP}"}]
    }
  }]
}
BATCH
)

# Update Route 53
aws route53 change-resource-record-sets \
  --hosted-zone-id ${HOSTED_ZONE_ID} \
  --change-batch "${CHANGE_BATCH}"

echo "Route 53 updated successfully: ${DOMAIN_NAME} -> ${PUBLIC_IP}"
EOF

RUN chmod +x /update-route53.sh

# Create startup script that runs Route 53 update then starts the app
RUN cat > /startup.sh << 'EOF'
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
#!/bin/bash

################################################################################
# Update ECS Service with New Task Definition
# This updates the music-library-service to use the new Namesilo task definition
################################################################################

echo "=========================================="
echo "Update ECS Service"
echo "=========================================="
echo ""

# Configuration
AWS_REGION="us-west-2"
CLUSTER_NAME="music-library-cluster"
SERVICE_NAME="music-library-service"
TASK_FAMILY="music-library-task"

# Check if revision number is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <task-definition-revision>"
    echo ""
    echo "Example: $0 7"
    echo ""
    echo "To find the latest revision, run:"
    echo "  aws ecs list-task-definitions --family-prefix ${TASK_FAMILY} --region ${AWS_REGION}"
    exit 1
fi

REVISION="$1"
TASK_DEFINITION="${TASK_FAMILY}:${REVISION}"

echo "Configuration:"
echo "  Cluster: $CLUSTER_NAME"
echo "  Service: $SERVICE_NAME"
echo "  Task Definition: $TASK_DEFINITION"
echo "  Region: $AWS_REGION"
echo ""

# Confirm with user
read -p "Are you sure you want to update the service? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo "Update cancelled."
    exit 0
fi

echo ""
echo "=========================================="
echo "Updating ECS Service..."
echo "=========================================="
echo ""

# Use MSYS_NO_PATHCONV for Git Bash on Windows
export MSYS_NO_PATHCONV=1

UPDATE_OUTPUT=$(aws ecs update-service \
    --cluster ${CLUSTER_NAME} \
    --service ${SERVICE_NAME} \
    --task-definition ${TASK_DEFINITION} \
    --region ${AWS_REGION} 2>&1)

if [ $? -eq 0 ]; then
    echo "✅ Service update initiated successfully!"
    echo ""
    echo "=========================================="
    echo "Deployment Status"
    echo "=========================================="
    echo ""
    echo "The service is now deploying the new task definition."
    echo "This process typically takes 2-5 minutes."
    echo ""
    echo "Monitor the deployment with:"
    echo ""
    echo "  aws ecs describe-services \\"
    echo "      --cluster ${CLUSTER_NAME} \\"
    echo "      --services ${SERVICE_NAME} \\"
    echo "      --region ${AWS_REGION} \\"
    echo "      --query 'services[0].deployments'"
    echo ""
    echo "Or watch the logs:"
    echo ""
    echo "  aws logs tail /ecs/music-library --follow --region ${AWS_REGION}"
    echo ""
    echo "=========================================="
    echo "What to Look For"
    echo "=========================================="
    echo ""
    echo "In the logs, you should see:"
    echo "  - 'Namesilo DNS Update Script Started'"
    echo "  - 'Public IP retrieved from...'"
    echo "  - 'Namesilo DNS record updated successfully!'"
    echo "  - 'DNS record project.jcarl.net now points to...'"
    echo ""
    echo "Once deployed, test your application at:"
    echo "  http://project.jcarl.net:8080/index.html"
    echo ""
else
    echo "❌ ERROR: Failed to update service"
    echo ""
    echo "Error details:"
    echo "$UPDATE_OUTPUT"
    echo ""
    exit 1
fi

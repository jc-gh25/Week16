#!/bin/bash

# Create Task Definition 5 with correct CPU/Memory settings
# This fixes the resource allocation issue from Task 4

set -e

REGION="us-west-2"
TASK_FAMILY="music-library-task"

echo "=========================================="
echo "Creating Task Definition 5"
echo "=========================================="
echo ""

# Get Task Definition 4 as a base
echo "📋 Fetching Task Definition 4..."
TASK_DEF_4=$(aws ecs describe-task-definition --task-definition ${TASK_FAMILY}:4 --region ${REGION})

# Extract the task definition and modify it
echo "🔧 Modifying CPU and Memory settings..."

# Create new task definition with updated resources
NEW_TASK_DEF=$(echo "$TASK_DEF_4" | jq '.taskDefinition | 
{
  family: .family,
  taskRoleArn: .taskRoleArn,
  executionRoleArn: .executionRoleArn,
  networkMode: .networkMode,
  containerDefinitions: .containerDefinitions,
  requiresCompatibilities: .requiresCompatibilities,
  cpu: "512",
  memory: "1024"
}')

echo "📝 New Task Definition settings:"
echo "$NEW_TASK_DEF" | jq '{
  family: .family,
  cpu: .cpu,
  memory: .memory,
  containerName: .containerDefinitions[0].name,
  image: .containerDefinitions[0].image
}'

echo ""
echo "🚀 Registering new task definition..."

# Register the new task definition
RESULT=$(aws ecs register-task-definition \
  --region ${REGION} \
  --cli-input-json "$NEW_TASK_DEF")

NEW_REVISION=$(echo "$RESULT" | jq -r '.taskDefinition.revision')

echo ""
echo "✅ Task Definition ${TASK_FAMILY}:${NEW_REVISION} created successfully!"
echo ""
echo "📊 Summary:"
echo "  Family: ${TASK_FAMILY}"
echo "  Revision: ${NEW_REVISION}"
echo "  CPU: 512"
echo "  Memory: 1024 MB"
echo ""
echo "🔄 To deploy this task definition, run:"
echo "  aws ecs update-service --cluster music-library-cluster1 --service music-library-service --task-definition ${TASK_FAMILY}:${NEW_REVISION} --force-new-deployment --region ${REGION}"
echo ""
echo "=========================================="

#!/bin/bash

################################################################################
# Create ECS Task Definition with Namesilo DNS Update
# This creates a new task definition revision for the music-library service
################################################################################

echo "=========================================="
echo "Create ECS Task Definition - Revision 7+"
echo "=========================================="
echo ""

# Configuration
AWS_REGION="us-west-2"
TASK_FAMILY="music-library-task"
ECR_IMAGE="913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest"
TASK_ROLE_ARN="arn:aws:iam::913212790762:role/ecsTaskExecutionRole"
EXECUTION_ROLE_ARN="arn:aws:iam::913212790762:role/ecsTaskExecutionRole"

echo "Configuration:"
echo "  Task Family: $TASK_FAMILY"
echo "  Image: $ECR_IMAGE"
echo "  Region: $AWS_REGION"
echo ""

# Create the task definition JSON
echo "Creating task definition JSON..."
echo ""

cat > task-definition-namesilo.json <<EOF
{
  "family": "${TASK_FAMILY}",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "taskRoleArn": "${TASK_ROLE_ARN}",
  "executionRoleArn": "${EXECUTION_ROLE_ARN}",
  "containerDefinitions": [
    {
      "name": "music-library-container",
      "image": "${ECR_IMAGE}",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/music-library",
          "awslogs-region": "${AWS_REGION}",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:8080/index.html || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
EOF

echo "✅ Task definition JSON created: task-definition-namesilo.json"
echo ""

# Register the task definition
echo "=========================================="
echo "Registering Task Definition with AWS ECS"
echo "=========================================="
echo ""

# Use MSYS_NO_PATHCONV for Git Bash on Windows
export MSYS_NO_PATHCONV=1

TASK_DEF_OUTPUT=$(aws ecs register-task-definition \
    --cli-input-json file://task-definition-namesilo.json \
    --region ${AWS_REGION} 2>&1)

if [ $? -eq 0 ]; then
    echo "✅ Task definition registered successfully!"
    echo ""
    
    # Extract the revision number
    REVISION=$(echo "$TASK_DEF_OUTPUT" | grep -o '"revision": [0-9]*' | grep -o '[0-9]*')
    
    echo "=========================================="
    echo "Task Definition Details"
    echo "=========================================="
    echo "  Family: ${TASK_FAMILY}"
    echo "  Revision: ${REVISION}"
    echo "  Full ARN: ${TASK_FAMILY}:${REVISION}"
    echo ""
    echo "=========================================="
    echo "Next Steps"
    echo "=========================================="
    echo ""
    echo "1. Update your ECS service to use this new task definition:"
    echo ""
    echo "   aws ecs update-service \\"
    echo "       --cluster music-library-cluster \\"
    echo "       --service music-library-service \\"
    echo "       --task-definition ${TASK_FAMILY}:${REVISION} \\"
    echo "       --region ${AWS_REGION}"
    echo ""
    echo "2. Monitor the deployment:"
    echo ""
    echo "   aws ecs describe-services \\"
    echo "       --cluster music-library-cluster \\"
    echo "       --services music-library-service \\"
    echo "       --region ${AWS_REGION}"
    echo ""
    echo "3. Check the logs for Namesilo DNS updates:"
    echo ""
    echo "   aws logs tail /ecs/music-library --follow --region ${AWS_REGION}"
    echo ""
else
    echo "❌ ERROR: Failed to register task definition"
    echo ""
    echo "Error details:"
    echo "$TASK_DEF_OUTPUT"
    echo ""
    exit 1
fi

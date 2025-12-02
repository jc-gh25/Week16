#!/bin/bash

# ECS Task Failure Diagnostic Script
# Purpose: Identify why ECS Task 4 keeps failing and rolling back to Task 2
# Region: us-west-2
# Service: music-library

set -e

REGION="us-west-2"
CLUSTER_NAME=""
SERVICE_NAME=""

echo "=========================================="
echo "ECS Task Failure Diagnostic Tool"
echo "=========================================="
echo ""

# Section 1: List all ECS clusters
echo "📋 SECTION 1: Listing all ECS clusters in ${REGION}"
echo "------------------------------------------"
CLUSTERS=$(aws ecs list-clusters --region ${REGION} --query 'clusterArns[]' --output text)

if [ -z "$CLUSTERS" ]; then
    echo "❌ No ECS clusters found in ${REGION}"
    exit 1
fi

echo "Found clusters:"
for cluster in $CLUSTERS; do
    cluster_name=$(echo $cluster | awk -F'/' '{print $NF}')
    echo "  - $cluster_name"
    
    # Check if this is the music-library cluster
    if [[ $cluster_name == *"music-library"* ]] || [[ $cluster_name == *"music"* ]]; then
        CLUSTER_NAME=$cluster_name
        echo "    ✅ This appears to be the music-library cluster!"
    fi
done
echo ""

# If no music-library cluster found, use the first one or prompt
if [ -z "$CLUSTER_NAME" ]; then
    echo "⚠️  No cluster with 'music-library' in the name found."
    CLUSTER_NAME=$(echo $CLUSTERS | awk '{print $1}' | awk -F'/' '{print $NF}')
    echo "Using first cluster: $CLUSTER_NAME"
    echo ""
fi

# Section 2: Find the music-library service
echo "📋 SECTION 2: Finding music-library service in cluster: ${CLUSTER_NAME}"
echo "------------------------------------------"
SERVICES=$(aws ecs list-services --cluster ${CLUSTER_NAME} --region ${REGION} --query 'serviceArns[]' --output text)

if [ -z "$SERVICES" ]; then
    echo "❌ No services found in cluster ${CLUSTER_NAME}"
    exit 1
fi

echo "Found services:"
for service in $SERVICES; do
    service_name=$(echo $service | awk -F'/' '{print $NF}')
    echo "  - $service_name"
    
    if [[ $service_name == *"music-library"* ]] || [[ $service_name == *"music"* ]]; then
        SERVICE_NAME=$service_name
        echo "    ✅ This is the music-library service!"
    fi
done
echo ""

if [ -z "$SERVICE_NAME" ]; then
    echo "⚠️  No service with 'music-library' in the name found."
    SERVICE_NAME=$(echo $SERVICES | awk '{print $1}' | awk -F'/' '{print $NF}')
    echo "Using first service: $SERVICE_NAME"
    echo ""
fi

# Section 3: List recent tasks (running and stopped)
echo "📋 SECTION 3: Listing recent tasks for service: ${SERVICE_NAME}"
echo "------------------------------------------"

echo "🟢 RUNNING TASKS:"
RUNNING_TASKS=$(aws ecs list-tasks --cluster ${CLUSTER_NAME} --service-name ${SERVICE_NAME} --region ${REGION} --desired-status RUNNING --query 'taskArns[]' --output text)

if [ -z "$RUNNING_TASKS" ]; then
    echo "  No running tasks found"
else
    for task in $RUNNING_TASKS; do
        task_id=$(echo $task | awk -F'/' '{print $NF}')
        echo "  - $task_id"
    done
fi
echo ""

echo "🔴 STOPPED TASKS (Last 10):"
STOPPED_TASKS=$(aws ecs list-tasks --cluster ${CLUSTER_NAME} --service-name ${SERVICE_NAME} --region ${REGION} --desired-status STOPPED --max-results 10 --query 'taskArns[]' --output text)

if [ -z "$STOPPED_TASKS" ]; then
    echo "  No stopped tasks found"
else
    for task in $STOPPED_TASKS; do
        task_id=$(echo $task | awk -F'/' '{print $NF}')
        echo "  - $task_id"
    done
fi
echo ""

# Section 4: Get details of stopped tasks to see why they failed
echo "📋 SECTION 4: Analyzing stopped tasks for failure reasons"
echo "------------------------------------------"

if [ ! -z "$STOPPED_TASKS" ]; then
    TASK_DETAILS=$(aws ecs describe-tasks --cluster ${CLUSTER_NAME} --tasks $STOPPED_TASKS --region ${REGION})
    
    echo "$TASK_DETAILS" | jq -r '.tasks[] | 
        "
Task ID: \(.taskArn | split("/") | .[-1])
Task Definition: \(.taskDefinitionArn | split("/") | .[-1])
Last Status: \(.lastStatus)
Desired Status: \(.desiredStatus)
Stopped Reason: \(.stoppedReason // "N/A")
Stop Code: \(.stopCode // "N/A")
Created: \(.createdAt)
Stopped: \(.stoppedAt // "Still running")

Container Details:
\(.containers[] | "  - Container: \(.name)
    Status: \(.lastStatus)
    Exit Code: \(.exitCode // "N/A")
    Reason: \(.reason // "N/A")")
----------------------------------------"'
else
    echo "No stopped tasks to analyze"
fi
echo ""

# Section 5: Get current service details including task definition
echo "📋 SECTION 5: Current service configuration"
echo "------------------------------------------"
SERVICE_DETAILS=$(aws ecs describe-services --cluster ${CLUSTER_NAME} --services ${SERVICE_NAME} --region ${REGION})

CURRENT_TASK_DEF=$(echo "$SERVICE_DETAILS" | jq -r '.services[0].taskDefinition' | awk -F'/' '{print $NF}')
DESIRED_COUNT=$(echo "$SERVICE_DETAILS" | jq -r '.services[0].desiredCount')
RUNNING_COUNT=$(echo "$SERVICE_DETAILS" | jq -r '.services[0].runningCount')

echo "Service: $SERVICE_NAME"
echo "Current Task Definition: $CURRENT_TASK_DEF"
echo "Desired Count: $DESIRED_COUNT"
echo "Running Count: $RUNNING_COUNT"
echo ""

# Section 6: Compare Task Definition 2 and Task Definition 4
echo "📋 SECTION 6: Comparing Task Definitions (Task 2 vs Task 4)"
echo "------------------------------------------"

# Get task definition family name
TASK_FAMILY=$(echo $CURRENT_TASK_DEF | sed 's/:[0-9]*$//')

echo "Task Definition Family: $TASK_FAMILY"
echo ""

# Get Task Definition 2
echo "🔵 TASK DEFINITION 2 (Working):"
echo "---"
TASK_DEF_2=$(aws ecs describe-task-definition --task-definition ${TASK_FAMILY}:2 --region ${REGION} 2>/dev/null || echo "")

if [ ! -z "$TASK_DEF_2" ]; then
    echo "$TASK_DEF_2" | jq -r '.taskDefinition | 
        "Revision: \(.revision)
CPU: \(.cpu // "N/A")
Memory: \(.memory // "N/A")
Network Mode: \(.networkMode)
Requires Compatibilities: \(.requiresCompatibilities | join(", "))

Container Definitions:
\(.containerDefinitions[] | "  - Name: \(.name)
    Image: \(.image)
    CPU: \(.cpu // "N/A")
    Memory: \(.memory // "N/A")
    Port Mappings: \(.portMappings | map("\(.containerPort):\(.hostPort)") | join(", "))
    Environment Variables: \(if .environment then (.environment | length) else 0 end) vars
    Health Check: \(if .healthCheck then "Configured" else "None" end)")"'
else
    echo "Task Definition 2 not found or no longer available"
fi
echo ""

# Get Task Definition 4
echo "🔴 TASK DEFINITION 4 (Failing):"
echo "---"
TASK_DEF_4=$(aws ecs describe-task-definition --task-definition ${TASK_FAMILY}:4 --region ${REGION} 2>/dev/null || echo "")

if [ ! -z "$TASK_DEF_4" ]; then
    echo "$TASK_DEF_4" | jq -r '.taskDefinition | 
        "Revision: \(.revision)
CPU: \(.cpu // "N/A")
Memory: \(.memory // "N/A")
Network Mode: \(.networkMode)
Requires Compatibilities: \(.requiresCompatibilities | join(", "))

Container Definitions:
\(.containerDefinitions[] | "  - Name: \(.name)
    Image: \(.image)
    CPU: \(.cpu // "N/A")
    Memory: \(.memory // "N/A")
    Port Mappings: \(.portMappings | map("\(.containerPort):\(.hostPort)") | join(", "))
    Environment Variables: \(if .environment then (.environment | length) else 0 end) vars
    Health Check: \(if .healthCheck then "Configured" else "None" end)")"'
else
    echo "Task Definition 4 not found or no longer available"
fi
echo ""

# Section 7: Check service events for circuit breaker messages
echo "📋 SECTION 7: Service Events (Circuit Breaker & Deployment Messages)"
echo "------------------------------------------"
echo "$SERVICE_DETAILS" | jq -r '.services[0].events[0:20] | .[] | 
    "\(.createdAt) - \(.message)"'
echo ""

# Section 8: Check deployment circuit breaker configuration
echo "📋 SECTION 8: Deployment Circuit Breaker Configuration"
echo "------------------------------------------"
echo "$SERVICE_DETAILS" | jq -r '.services[0].deploymentConfiguration.deploymentCircuitBreaker | 
    "Enabled: \(.enable)
Rollback on Failure: \(.rollback)"'
echo ""

# Summary and recommendations
echo "=========================================="
echo "🔍 DIAGNOSTIC SUMMARY"
echo "=========================================="
echo ""
echo "Cluster: $CLUSTER_NAME"
echo "Service: $SERVICE_NAME"
echo "Current Task Definition: $CURRENT_TASK_DEF"
echo ""
echo "📌 KEY THINGS TO CHECK:"
echo "  1. Look at the 'Stopped Reason' and 'Exit Code' in Section 4"
echo "  2. Compare the Image tags between Task 2 and Task 4 in Section 6"
echo "  3. Check for health check failures in the service events (Section 7)"
echo "  4. Look for 'Essential container exited' messages"
echo "  5. Verify the container image exists and is accessible"
echo "  6. Check if port mappings or environment variables changed"
echo ""
echo "💡 COMMON CAUSES OF TASK FAILURES:"
echo "  - Container image not found or inaccessible"
echo "  - Application crashes on startup (check logs in CloudWatch)"
echo "  - Health check failures (wrong endpoint or timeout)"
echo "  - Insufficient CPU/Memory allocation"
echo "  - Missing or incorrect environment variables"
echo "  - Port conflicts or incorrect port mappings"
echo ""
echo "🔗 NEXT STEPS:"
echo "  1. Check CloudWatch Logs for the failing tasks"
echo "  2. Verify the Docker image for Task 4 exists in ECR"
echo "  3. Test the container locally if possible"
echo "  4. Review any configuration changes between Task 2 and Task 4"
echo ""
echo "=========================================="
echo "Diagnostic complete!"
echo "=========================================="

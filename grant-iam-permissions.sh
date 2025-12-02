#!/bin/bash

################################################################################
# IAM Permissions Script for ECS Deployment
# 
# Purpose: Grants necessary IAM permissions to a user for ECS task deployment
#          and CloudWatch logging in AWS.
#
# Prerequisites:
#   - AWS CLI installed and configured
#   - Credentials with IAM admin permissions (root account or admin user)
#   - Appropriate AWS region configured
#
# Usage:
#   chmod +x grant-iam-permissions.sh
#   ./grant-iam-permissions.sh
#
# Note: This script creates an inline policy attached directly to the user.
#       For production environments, consider using managed policies instead.
#
################################################################################

set -e  # Exit on any error

# Configuration Variables
AWS_ACCOUNT_ID="913212790762"
AWS_REGION="us-west-2"
TARGET_USER="user1"
POLICY_NAME="ECSDeploymentPolicy"
TASK_DEFINITION_NAME="music-library-task"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}IAM Permissions Setup for ECS Deployment${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# Verify AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI is not installed.${NC}"
    echo "Please install AWS CLI from: https://aws.amazon.com/cli/"
    exit 1
fi

echo -e "${GREEN}✓ AWS CLI found${NC}"

# Verify user has AWS credentials configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}Error: AWS credentials not configured or invalid.${NC}"
    echo "Please configure AWS credentials using: aws configure"
    exit 1
fi

echo -e "${GREEN}✓ AWS credentials verified${NC}"

# Display current AWS account information
CURRENT_ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
CURRENT_USER=$(aws sts get-caller-identity --query Arn --output text)

echo ""
echo "Current AWS Account: $CURRENT_ACCOUNT"
echo "Current User/Role: $CURRENT_USER"
echo ""

# Verify we're in the correct account
if [ "$CURRENT_ACCOUNT" != "$AWS_ACCOUNT_ID" ]; then
    echo -e "${RED}Error: Current account ($CURRENT_ACCOUNT) does not match target account ($AWS_ACCOUNT_ID)${NC}"
    echo "Please switch to the correct AWS account and try again."
    exit 1
fi

echo -e "${GREEN}✓ Correct AWS account confirmed${NC}"

# Verify the target user exists
echo ""
echo "Checking if user '$TARGET_USER' exists..."
if ! aws iam get-user --user-name "$TARGET_USER" &> /dev/null; then
    echo -e "${RED}Error: User '$TARGET_USER' does not exist in this AWS account.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ User '$TARGET_USER' found${NC}"

# Create the inline policy document
echo ""
echo "Creating IAM policy document..."

POLICY_DOCUMENT=$(cat <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ECSTaskDefinitionPermissions",
            "Effect": "Allow",
            "Action": [
                "ecs:RegisterTaskDefinition",
                "ecs:DescribeTaskDefinition"
            ],
            "Resource": "arn:aws:ecs:${AWS_REGION}:${AWS_ACCOUNT_ID}:task-definition/${TASK_DEFINITION_NAME}:*"
        },
        {
            "Sid": "ECSServicePermissions",
            "Effect": "Allow",
            "Action": [
                "ecs:UpdateService",
                "ecs:DescribeServices"
            ],
            "Resource": "arn:aws:ecs:${AWS_REGION}:${AWS_ACCOUNT_ID}:service/*/*"
        },
        {
            "Sid": "IAMPassRolePermission",
            "Effect": "Allow",
            "Action": "iam:PassRole",
            "Resource": [
                "arn:aws:iam::${AWS_ACCOUNT_ID}:role/ecsTaskExecutionRole",
                "arn:aws:iam::${AWS_ACCOUNT_ID}:role/ecsTaskRole"
            ]
        },
        {
            "Sid": "CloudWatchLogsPermissions",
            "Effect": "Allow",
            "Action": [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": "arn:aws:logs:${AWS_REGION}:${AWS_ACCOUNT_ID}:log-group:/ecs/*"
        }
    ]
}
EOF
)

echo -e "${GREEN}✓ Policy document created${NC}"

# Attach the inline policy to the user
echo ""
echo "Attaching policy to user '$TARGET_USER'..."
echo "Policy Name: $POLICY_NAME"
echo ""

aws iam put-user-policy \
    --user-name "$TARGET_USER" \
    --policy-name "$POLICY_NAME" \
    --policy-document "$POLICY_DOCUMENT"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Policy successfully attached to user '$TARGET_USER'${NC}"
else
    echo -e "${RED}✗ Failed to attach policy${NC}"
    exit 1
fi

# Verify the policy was attached
echo ""
echo "Verifying policy attachment..."
aws iam get-user-policy \
    --user-name "$TARGET_USER" \
    --policy-name "$POLICY_NAME" > /dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Policy verification successful${NC}"
else
    echo -e "${RED}✗ Policy verification failed${NC}"
    exit 1
fi

# Display summary
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Summary of permissions granted to '$TARGET_USER':"
echo "  • ECS Task Definition: Register and Describe"
echo "  • ECS Services: Update and Describe"
echo "  • IAM PassRole: For task execution and task roles"
echo "  • CloudWatch Logs: Create log groups and streams"
echo ""
echo "Policy Details:"
echo "  • Account ID: $AWS_ACCOUNT_ID"
echo "  • Region: $AWS_REGION"
echo "  • Task Definition: $TASK_DEFINITION_NAME"
echo "  • Policy Name: $POLICY_NAME"
echo ""
echo "To view the attached policy, run:"
echo "  aws iam get-user-policy --user-name $TARGET_USER --policy-name $POLICY_NAME"
echo ""
echo "To remove this policy, run:"
echo "  aws iam delete-user-policy --user-name $TARGET_USER --policy-name $POLICY_NAME"
echo ""

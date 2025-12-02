#!/bin/bash

################################################################################
# Docker Build and Push Script for Music Library with Namesilo DNS
# This script helps build and push the Docker image to AWS ECR
################################################################################

echo "=========================================="
echo "Music Library - Docker Build & Push"
echo "=========================================="
echo ""

# Configuration
AWS_ACCOUNT_ID="913212790762"
AWS_REGION="us-west-2"
ECR_REPOSITORY="music-library"
IMAGE_TAG="latest"
ECR_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}"

echo "Configuration:"
echo "  AWS Account: $AWS_ACCOUNT_ID"
echo "  AWS Region: $AWS_REGION"
echo "  ECR Repository: $ECR_REPOSITORY"
echo "  Image Tag: $IMAGE_TAG"
echo "  Full ECR URI: $ECR_URI"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ ERROR: Docker is not running or not installed"
    echo ""
    echo "Please ensure Docker Desktop is running before executing this script."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Step 1: Replace the old Dockerfile with the new one
echo "=========================================="
echo "Step 1: Updating Dockerfile"
echo "=========================================="
echo ""

if [ -f "music-library/Dockerfile.namesilo" ]; then
    echo "Backing up old Dockerfile..."
    cp music-library/Dockerfile music-library/Dockerfile.route53.backup
    
    echo "Replacing with Namesilo version..."
    cp music-library/Dockerfile.namesilo music-library/Dockerfile
    
    echo "✅ Dockerfile updated successfully"
else
    echo "❌ ERROR: Dockerfile.namesilo not found"
    exit 1
fi

echo ""

# Step 2: Build the Docker image
echo "=========================================="
echo "Step 2: Building Docker Image"
echo "=========================================="
echo ""
echo "This may take several minutes..."
echo ""

cd music-library

if docker build -t ${ECR_REPOSITORY}:${IMAGE_TAG} .; then
    echo ""
    echo "✅ Docker image built successfully"
else
    echo ""
    echo "❌ ERROR: Docker build failed"
    exit 1
fi

cd ..
echo ""

# Step 3: Tag the image for ECR
echo "=========================================="
echo "Step 3: Tagging Image for ECR"
echo "=========================================="
echo ""

if docker tag ${ECR_REPOSITORY}:${IMAGE_TAG} ${ECR_URI}; then
    echo "✅ Image tagged successfully"
else
    echo "❌ ERROR: Failed to tag image"
    exit 1
fi

echo ""

# Step 4: Login to ECR
echo "=========================================="
echo "Step 4: Logging into AWS ECR"
echo "=========================================="
echo ""

if aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com; then
    echo "✅ Successfully logged into ECR"
else
    echo "❌ ERROR: Failed to login to ECR"
    echo ""
    echo "Make sure AWS CLI is configured with valid credentials:"
    echo "  aws configure"
    exit 1
fi

echo ""

# Step 5: Push the image to ECR
echo "=========================================="
echo "Step 5: Pushing Image to ECR"
echo "=========================================="
echo ""
echo "This may take several minutes..."
echo ""

if docker push ${ECR_URI}; then
    echo ""
    echo "✅ Image pushed successfully to ECR"
else
    echo ""
    echo "❌ ERROR: Failed to push image to ECR"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ BUILD AND PUSH COMPLETE!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "  1. Create a new ECS task definition (revision 7+) using this image:"
echo "     Image URI: $ECR_URI"
echo ""
echo "  2. Update your ECS service to use the new task definition"
echo ""
echo "  3. Monitor the logs to see the Namesilo DNS update in action"
echo ""
echo "Image Details:"
echo "  Repository: ${ECR_REPOSITORY}"
echo "  Tag: ${IMAGE_TAG}"
echo "  Full URI: ${ECR_URI}"
echo ""

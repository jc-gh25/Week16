#!/bin/bash
# build-on-aws.sh - Build Docker image on AWS CloudShell (no local Docker needed)
# Usage: Run this script in AWS CloudShell after uploading your music-library folder

set -e  # Exit on any error

# Configuration
AWS_REGION="us-west-2"
AWS_ACCOUNT_ID="913212790762"
ECR_REPO="music-library"
IMAGE_TAG="latest"
ECR_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"

echo "=========================================="
echo "Building Docker Image on AWS CloudShell"
echo "=========================================="
echo "ECR Repository: ${ECR_URI}"
echo "Region: ${AWS_REGION}"
echo ""

# Step 1: Authenticate Docker to ECR
echo "[1/4] Authenticating to Amazon ECR..."
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
echo "✓ Authentication successful"
echo ""

# Step 2: Build the Docker image
echo "[2/4] Building Docker image..."
cd music-library
docker build -f Dockerfile.namesilo -t ${ECR_REPO}:${IMAGE_TAG} .
echo "✓ Image built successfully"
echo ""

# Step 3: Tag the image for ECR
echo "[3/4] Tagging image for ECR..."
docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_URI}
echo "✓ Image tagged"
echo ""

# Step 4: Push to ECR
echo "[4/4] Pushing image to ECR..."
docker push ${ECR_URI}
echo "✓ Image pushed successfully"
echo ""

echo "=========================================="
echo "✓ COMPLETE! Image available at:"
echo "  ${ECR_URI}"
echo "=========================================="

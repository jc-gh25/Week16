# Namesilo DNS Update - Deployment Guide

## Overview
This guide walks you through updating your Music Library application to use Namesilo DNS instead of Route 53.

## What Changed
- **Old**: Docker container used AWS Route 53 API to update DNS
- **New**: Docker container uses Namesilo API to update DNS
- **Why**: You're using Namesilo as your DNS provider for jcarl.net

## Files Created
1. `Dockerfile.namesilo` - New Dockerfile with Namesilo DNS update script
2. `build-and-push-namesilo.sh` - Automates Docker build and push to ECR
3. `create-task-def-namesilo.sh` - Creates new ECS task definition
4. `update-ecs-service-namesilo.sh` - Updates ECS service to use new task
5. `test-namesilo-api.sh` - Tests Namesilo API connection (already tested ✅)

## Prerequisites
✅ Namesilo DNS record created for project.jcarl.net (DONE)
✅ Record ID obtained: 8ad154e44ae9c94cd8f22be2bea457d2 (DONE)
✅ API key tested and working (DONE)
⚠️ Docker must be installed and running (REQUIRED FOR NEXT STEPS)
⚠️ AWS CLI must be configured (REQUIRED FOR NEXT STEPS)

## Important Notes

### About Docker
- **Docker commands MUST be run where Docker is installed**
- Your current Windows Git Bash machine does NOT have Docker
- You need to either:
  1. Install Docker Desktop on Windows, OR
  2. Run these commands on a machine with Docker installed (like an EC2 instance)

### About Your Current Task
- Task 2 is currently running with the OLD Route 53 image
- **DO NOT stop or restart Task 2 until the new image is tested**
- The new task will run alongside Task 2 initially for testing

## Step-by-Step Deployment

### Step 1: Prepare Your Environment

**If using Windows with Docker Desktop:**
```bash
# Make sure Docker Desktop is running
# Check Docker status:
docker --version
docker info
```

**If using a remote machine with Docker:**
```bash
# SSH into your Docker-enabled machine
# Copy the Week16 folder to that machine
```

### Step 2: Build and Push Docker Image

Navigate to your Week16 project folder and run:

```bash
cd /c/Users/user1/Desktop/Back\ End\ Software\ Dev/PortableGit/projects/Week16

# Make the script executable
chmod +x build-and-push-namesilo.sh

# Run the build and push script
./build-and-push-namesilo.sh
```

**What this script does:**
1. Checks if Docker is running
2. Backs up your old Dockerfile (saves as Dockerfile.route53.backup)
3. Replaces Dockerfile with the Namesilo version
4. Builds the Docker image
5. Tags it for ECR
6. Logs into AWS ECR
7. Pushes the image to ECR

**Expected output:**
```
✅ Docker is running
✅ Dockerfile updated successfully
✅ Docker image built successfully
✅ Image tagged successfully
✅ Successfully logged into ECR
✅ Image pushed successfully to ECR
```

**If it fails:**
- Docker not running: Start Docker Desktop
- AWS login fails: Run `aws configure` to set up credentials
- Build fails: Check the error messages for missing dependencies

### Step 3: Create New Task Definition

```bash
# Make the script executable
chmod +x create-task-def-namesilo.sh

# Run the script
./create-task-def-namesilo.sh
```

**What this script does:**
1. Creates a JSON file with the new task definition
2. Registers it with AWS ECS
3. Shows you the new revision number (should be 7 or higher)

**Expected output:**
```
✅ Task definition registered successfully!
  Family: music-library-task
  Revision: 7
```

**Note the revision number** - you'll need it for the next step!

### Step 4: Update ECS Service (CAREFUL!)

⚠️ **IMPORTANT**: This will deploy the new container. Make sure Steps 1-3 completed successfully first.

```bash
# Make the script executable
chmod +x update-ecs-service-namesilo.sh

# Run the script with the revision number from Step 3
./update-ecs-service-namesilo.sh 7
```

**What this script does:**
1. Asks for confirmation (type "yes")
2. Updates the ECS service to use the new task definition
3. ECS will start a new task with the Namesilo DNS update
4. Once healthy, ECS will stop the old task

**Expected output:**
```
✅ Service update initiated successfully!
```

### Step 5: Monitor the Deployment

**Watch the logs in real-time:**
```bash
aws logs tail /ecs/music-library --follow --region us-west-2
```

**Look for these messages:**
```
[INFO] Namesilo DNS Update Script Started
[INFO] Step 1: Retrieving public IP from ECS task metadata...
[SUCCESS] Public IP retrieved from external service: X.X.X.X
[INFO] Step 2: Updating Namesilo DNS record for project.jcarl.net with IP X.X.X.X...
[INFO] API Response Code: 300
[INFO] API Response Detail: success
[SUCCESS] Namesilo DNS record updated successfully!
[SUCCESS] Namesilo DNS Update Script Completed Successfully
```

**Check deployment status:**
```bash
aws ecs describe-services \
    --cluster music-library-cluster \
    --services music-library-service \
    --region us-west-2 \
    --query 'services[0].deployments'
```

Look for:
- `runningCount: 1` for the new task
- `desiredCount: 1`
- `status: PRIMARY`

### Step 6: Test the Application

Once the deployment is complete (2-5 minutes):

1. **Test DNS resolution:**
   ```bash
   nslookup project.jcarl.net
   ```
   Should show the new IP address

2. **Test the application:**
   ```bash
   curl http://project.jcarl.net:8080/index.html
   ```
   Or open in browser: http://project.jcarl.net:8080/index.html

3. **Test the API:**
   ```bash
   curl http://project.jcarl.net:8080/albums
   ```

### Step 7: Verify Namesilo DNS Update

Run your test script again to confirm the DNS record was updated:

```bash
./test-namesilo-api.sh
```

Check that the IP address matches your new ECS task's public IP.

## Troubleshooting

### Docker Build Fails
**Problem**: "Docker is not running"
**Solution**: Start Docker Desktop and wait for it to fully start

**Problem**: "mvn command not found" during build
**Solution**: The Dockerfile uses a Maven image, so this shouldn't happen. Check your Dockerfile wasn't corrupted.

### ECR Push Fails
**Problem**: "no basic auth credentials"
**Solution**: Run the ECR login command again:
```bash
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 913212790762.dkr.ecr.us-west-2.amazonaws.com
```

**Problem**: "denied: Your authorization token has expired"
**Solution**: Same as above - re-login to ECR

### Task Definition Registration Fails
**Problem**: "Invalid IAM role ARN"
**Solution**: Check that the role ARNs in the script match your actual IAM roles

### Service Update Fails
**Problem**: "Service not found"
**Solution**: Verify cluster and service names:
```bash
aws ecs list-clusters --region us-west-2
aws ecs list-services --cluster music-library-cluster --region us-west-2
```

### Container Fails to Start
**Problem**: Task stops immediately after starting
**Solution**: Check CloudWatch logs:
```bash
aws logs tail /ecs/music-library --region us-west-2
```

Look for error messages in the logs.

### Namesilo DNS Update Fails
**Problem**: "Failed to get response from Namesilo API"
**Solution**: 
- Check that the container has internet access
- Verify the API key is correct in the Dockerfile
- Check Namesilo API status

**Problem**: "API returned error code 110"
**Solution**: Invalid API key - double-check the key in Dockerfile.namesilo

**Problem**: "API returned error code 280"
**Solution**: Invalid domain or record ID - verify in Dockerfile.namesilo

## Rollback Plan

If something goes wrong, you can rollback to the previous task definition:

```bash
# List task definitions to find the previous revision
aws ecs list-task-definitions --family-prefix music-library-task --region us-west-2

# Update service to use previous revision (e.g., revision 6)
aws ecs update-service \
    --cluster music-library-cluster \
    --service music-library-service \
    --task-definition music-library-task:6 \
    --region us-west-2
```

## Key Configuration Values

These are embedded in the Dockerfile and scripts:

- **Namesilo API Key**: `15fc56289aa5698b5d7c41ce`
- **Domain**: `jcarl.net`
- **Subdomain**: `project`
- **Record ID**: `8ad154e44ae9c94cd8f22be2bea457d2`
- **TTL**: `7207` seconds (~2 hours)
- **ECR Repository**: `913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest`

## What Happens When Container Starts

1. Container starts up
2. `/startup.sh` runs automatically
3. `/update-namesilo-dns.sh` executes:
   - Gets container's public IP from ECS metadata (or external service)
   - Calls Namesilo API to update project.jcarl.net DNS record
   - Verifies the update
4. If DNS update fails, container continues anyway (won't crash)
5. Java application starts on port 8080
6. Container is marked healthy after health check passes

## Success Criteria

✅ Docker image builds without errors
✅ Image pushes to ECR successfully
✅ Task definition registers (revision 7+)
✅ ECS service updates successfully
✅ New task starts and becomes healthy
✅ Logs show "Namesilo DNS record updated successfully!"
✅ `nslookup project.jcarl.net` shows the new IP
✅ Application accessible at http://project.jcarl.net:8080/index.html
✅ Old task (Task 2) stops gracefully

## Next Steps After Successful Deployment

1. **Monitor for a few hours** to ensure stability
2. **Test DNS updates** by restarting the task (it should update DNS each time)
3. **Document the new process** for future deployments
4. **Consider automation** - set up CI/CD pipeline for automatic builds
5. **Set up alerts** for task failures or DNS update failures

## Questions or Issues?

If you encounter any issues not covered in this guide:
1. Check CloudWatch logs first
2. Verify all configuration values
3. Test each component individually (Docker build, ECR push, etc.)
4. Ask for help with specific error messages

---

**Remember**: Task 2 is still running with the old Route 53 code. Don't stop it until you've verified the new Namesilo version works correctly!

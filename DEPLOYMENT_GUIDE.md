# ECS Task Definition Fix - Complete Deployment Guide

## Problem Summary
Your Route 53 update script wasn't running because the ECS task definition was **missing the `--task-role-arn` parameter**. Without this, the container has no permissions to call AWS services.

## What Changed

### Before (❌ Missing Task Role)
```bash
aws ecs register-task-definition \
  --family music-library-task \
  --network-mode awsvpc \
  --requires-compatibilities FARGATE \
  --cpu 256 \
  --memory 512 \
  --container-definitions '[...]'
```

### After (✅ With Task Role)
```bash
aws ecs register-task-definition \
  --family music-library-task \
  --network-mode awsvpc \
  --requires-compatibilities FARGATE \
  --cpu 256 \
  --memory 512 \
  --task-role-arn arn:aws:iam::913212790762:role/ECS-Task-Route53-Role \
  --execution-role-arn arn:aws:iam::913212790762:role/ecsTaskExecutionRole \
  --container-definitions '[...]'
```

## Key Additions

| Parameter | Purpose | Value |
|-----------|---------|-------|
| `--task-role-arn` | Grants container permissions to call AWS services (Route 53, EC2) | `arn:aws:iam::913212790762:role/ECS-Task-Route53-Role` |
| `--execution-role-arn` | Grants ECS permissions to pull images and manage logs | `arn:aws:iam::913212790762:role/ecsTaskExecutionRole` |
| `--logConfiguration` | CloudWatch logs for debugging | `/ecs/music-library` |

## Step-by-Step Deployment

### Step 1: Verify IAM Roles Exist
```bash
# Check task role
aws iam get-role --role-name ECS-Task-Route53-Role --region us-west-2

# Check execution role
aws iam get-role --role-name ecsTaskExecutionRole --region us-west-2
```

### Step 2: Create CloudWatch Log Group (Optional but Recommended)
```bash
aws logs create-log-group \
  --log-group-name /ecs/music-library \
  --region us-west-2
```

### Step 3: Register New Task Definition
```bash
# Run the corrected script
bash ecs-task-definition-corrected.sh
```

**Expected output:**
```json
{
  "taskDefinition": {
    "family": "music-library-task",
    "revision": "2",
    "taskDefinitionArn": "arn:aws:ecs:us-west-2:913212790762:task-definition/music-library-task:2",
    "taskRoleArn": "arn:aws:iam::913212790762:role/ECS-Task-Route53-Role",
    ...
  }
}
```

### Step 4: Update ECS Service to Use New Task Definition
```bash
# Get your service name first
aws ecs list-services \
  --cluster music-library-cluster1 \
  --region us-west-2

# Update service with new task definition
aws ecs update-service \
  --cluster music-library-cluster1 \
  --service music-library-service \
  --task-definition music-library-task:2 \
  --region us-west-2
```

### Step 5: Verify Deployment
```bash
# Check service status
aws ecs describe-services \
  --cluster music-library-cluster1 \
  --services music-library-service \
  --region us-west-2

# Check running tasks
aws ecs list-tasks \
  --cluster music-library-cluster1 \
  --region us-west-2

# Get task details
aws ecs describe-tasks \
  --cluster music-library-cluster1 \
  --tasks <task-arn> \
  --region us-west-2
```

## Debugging Route 53 Update

### Check Container Logs
```bash
# View logs in CloudWatch
aws logs tail /ecs/music-library --follow --region us-west-2

# Or get specific log stream
aws logs get-log-events \
  --log-group-name /ecs/music-library \
  --log-stream-name ecs/music-library/<task-id> \
  --region us-west-2
```

### Expected Log Output (Success)
```
Starting Route 53 DNS update...
DEBUG: Private IP: 10.0.1.45
DEBUG: Public IP from ENI: 54.123.45.67
Current public IP: 54.123.45.67
Route 53 updated successfully: project.jcarl.net -> 54.123.45.67
```

### Expected Log Output (Failure - Before Fix)
```
Starting Route 53 DNS update...
DEBUG: Private IP: 10.0.1.45
DEBUG: Public IP from ENI: None
ERROR: Could not retrieve public IP
WARNING: Route 53 update failed, continuing anyway...
```

## Troubleshooting Checklist

- [ ] Task role `ECS-Task-Route53-Role` exists in IAM
- [ ] Task role has both policies attached:
  - [ ] `ECS-Route53-Update-Policy` (Route 53 permissions)
  - [ ] `Route53AndEC2Policy` (EC2 + Route 53 permissions)
- [ ] New task definition revision created (check revision number increased)
- [ ] ECS service updated to use new task definition
- [ ] New tasks are running (old tasks should be replaced)
- [ ] CloudWatch logs show Route 53 update success
- [ ] Route 53 hosted zone has correct record: `project.jcarl.net`
- [ ] Hosted zone ID matches Dockerfile: `Z08164103KW73VKOVN6GY`

## Verification Commands

```bash
# Verify Route 53 record was updated
aws route53 list-resource-record-sets \
  --hosted-zone-id Z08164103KW73VKOVN6GY \
  --region us-west-2 \
  --query 'ResourceRecordSets[?Name==`project.jcarl.net.`]'

# Should show your current public IP
```

## Important Notes

1. **Task Definition Revisions**: Each time you register a task definition, it creates a new revision (1, 2, 3, etc.). You don't delete old ones.

2. **Service Update**: The service will automatically replace old tasks with new ones running the updated task definition.

3. **Gradual Rollout**: ECS will drain connections from old tasks before stopping them (respects `deregistrationDelay`).

4. **Rollback**: If something goes wrong, you can quickly rollback:
   ```bash
   aws ecs update-service \
     --cluster music-library-cluster1 \
     --service <your-service-name> \
     --task-definition music-library-task:1 \
     --region us-west-2
   ```

## Next Steps

1. Run the corrected task definition script
2. Update your ECS service
3. Monitor CloudWatch logs for Route 53 update success
4. Verify DNS record updated: `nslookup project.jcarl.net`
5. Test your application at `http://project.jcarl.net:8080`

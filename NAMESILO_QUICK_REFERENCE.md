# Namesilo DNS Update - Quick Reference

## 🚀 Quick Start (Run These Commands in Order)

### Prerequisites Check
```bash
# Check Docker is running
docker --version

# Check AWS CLI is configured
aws sts get-caller-identity
```

### Step 1: Build & Push (5-10 minutes)
```bash
cd /c/Users/user1/Desktop/Back\ End\ Software\ Dev/PortableGit/projects/Week16
chmod +x build-and-push-namesilo.sh
./build-and-push-namesilo.sh
```

### Step 2: Create Task Definition (30 seconds)
```bash
chmod +x create-task-def-namesilo.sh
./create-task-def-namesilo.sh
```
**📝 Note the revision number!** (e.g., 7)

### Step 3: Update Service (2-5 minutes)
```bash
chmod +x update-ecs-service-namesilo.sh
./update-ecs-service-namesilo.sh 7  # Use your revision number
```
Type "yes" when prompted

### Step 4: Monitor Logs
```bash
aws logs tail /ecs/music-library --follow --region us-west-2
```
Look for: "Namesilo DNS record updated successfully!"

### Step 5: Test
```bash
# Test DNS
nslookup project.jcarl.net

# Test application
curl http://project.jcarl.net:8080/index.html
```

---

## 📋 What Each Script Does

| Script | Purpose | Time |
|--------|---------|------|
| `build-and-push-namesilo.sh` | Builds Docker image and pushes to ECR | 5-10 min |
| `create-task-def-namesilo.sh` | Creates new ECS task definition | 30 sec |
| `update-ecs-service-namesilo.sh` | Deploys new task to ECS | 2-5 min |
| `test-namesilo-api.sh` | Tests Namesilo API (already done ✅) | 5 sec |

---

## ⚠️ Important Reminders

1. **Docker Required**: Must run where Docker is installed
2. **Don't Stop Task 2**: Keep old task running until new one works
3. **Git Bash on Windows**: Use `MSYS_NO_PATHCONV=1` for AWS CLI (scripts handle this)
4. **Revision Number**: Remember it from Step 2 for Step 3

---

## 🔧 Key Configuration

```
API Key:    15fc56289aa5698b5d7c41ce
Domain:     jcarl.net
Subdomain:  project
Record ID:  8ad154e44ae9c94cd8f22be2bea457d2
ECR Image:  913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
```

---

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| "Docker is not running" | Start Docker Desktop |
| "no basic auth credentials" | Re-run ECR login (in build script) |
| Task stops immediately | Check logs: `aws logs tail /ecs/music-library` |
| DNS not updating | Check logs for Namesilo API errors |
| Can't access app | Wait 2-5 min for deployment, check security groups |

---

## 🔄 Rollback Command

If something goes wrong:
```bash
aws ecs update-service \
    --cluster music-library-cluster \
    --service music-library-service \
    --task-definition music-library-task:6 \
    --region us-west-2
```
(Replace `:6` with your previous working revision)

---

## ✅ Success Checklist

- [ ] Docker image built successfully
- [ ] Image pushed to ECR
- [ ] Task definition created (revision 7+)
- [ ] Service updated
- [ ] New task running and healthy
- [ ] Logs show "Namesilo DNS record updated successfully!"
- [ ] `nslookup project.jcarl.net` shows new IP
- [ ] App accessible at http://project.jcarl.net:8080/index.html

---

## 📚 Full Documentation

See `NAMESILO_DEPLOYMENT_GUIDE.md` for detailed explanations, troubleshooting, and rollback procedures.

---

## 🎯 What Changed

**Before (Route 53):**
- Used AWS Route 53 API
- Required AWS credentials in container
- Complex IP lookup via EC2 API

**After (Namesilo):**
- Uses Namesilo API
- Simple HTTP API call
- Gets IP from ECS metadata or external service
- No AWS credentials needed for DNS update

---

## 📞 Need Help?

1. Check CloudWatch logs first
2. Review `NAMESILO_DEPLOYMENT_GUIDE.md`
3. Verify all configuration values
4. Test components individually

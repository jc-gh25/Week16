# Build Docker Image on AWS CloudShell (NO LOCAL DOCKER NEEDED)

## Why CloudShell?
- **Zero setup required** - Docker is pre-installed
- **No EC2 costs** - CloudShell is free
- **Already authenticated** - Uses your AWS credentials
- **Takes 5 minutes total**

---

## Step-by-Step Instructions

### 1. Open AWS CloudShell
1. Go to AWS Console: https://console.aws.amazon.com/
2. Make sure you're in **us-west-2** region (top right)
3. Click the **CloudShell icon** (terminal icon) in the top navigation bar
4. Wait ~30 seconds for CloudShell to start

### 2. Upload Your Code
In CloudShell, click **Actions** → **Upload file**

Upload this **entire folder** as a ZIP:
```
C:\Users\user1\Desktop\Back End Software Dev\PortableGit\projects\Week16\music-library
```

**OR** upload these files individually:
- `music-library/Dockerfile.namesilo`
- `music-library/package.json`
- `music-library/server.js`
- Any other files in the music-library folder

### 3. Extract (if you uploaded a ZIP)
```bash
unzip music-library.zip
```

### 4. Upload and Run the Build Script
Upload the `build-on-aws.sh` file to CloudShell, then:

```bash
chmod +x build-on-aws.sh
./build-on-aws.sh
```

### 5. Done!
The script will:
- ✓ Authenticate to ECR
- ✓ Build your Docker image
- ✓ Push to ECR at `913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest`

**Total time: ~3-5 minutes**

---

## Troubleshooting

### "Permission denied" error
```bash
chmod +x build-on-aws.sh
```

### "No such file or directory: music-library"
Make sure you're in the right directory:
```bash
ls -la
# You should see the music-library folder
```

### ECR repository doesn't exist
Create it first:
```bash
aws ecr create-repository --repository-name music-library --region us-west-2
```

### Need to check if image was pushed successfully
```bash
aws ecr describe-images --repository-name music-library --region us-west-2
```

---

## Alternative: Quick One-Liner (if you're already in CloudShell with files uploaded)

```bash
cd music-library && \
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 913212790762.dkr.ecr.us-west-2.amazonaws.com && \
docker build -f Dockerfile.namesilo -t music-library:latest . && \
docker tag music-library:latest 913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest && \
docker push 913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
```

---

## Why Not EC2?
The EC2 approach would require:
- Creating IAM roles
- Launching an instance
- Waiting for it to start
- SSH key management
- Manual cleanup
- **Costs money** (even t2.micro)

CloudShell is **free, instant, and already has everything you need**.

---

## Next Steps After Build
Once the image is pushed, you can deploy it to:
- ECS/Fargate
- EC2 instance
- App Runner
- Elastic Beanstalk

Your image will be at:
```
913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
```

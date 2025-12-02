#!/bin/bash
# Corrected ECS Task Definition with Task Role for Route 53 Updates
# Account ID: 913212790762
# Region: us-west-2

aws ecs register-task-definition \
  --family music-library-task \
  --network-mode awsvpc \
  --requires-compatibilities FARGATE \
  --cpu 256 \
  --memory 512 \
  --task-role-arn arn:aws:iam::913212790762:role/ECS-Task-Route53-Role \
  --execution-role-arn arn:aws:iam::913212790762:role/ecsTaskExecutionRole \
  --container-definitions '[
    {
      "name": "music-library",
      "image": "913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "MYSQL_HOST",
          "value": "music-library-db.cv4kawuomqo5.us-west-2.rds.amazonaws.com"
        },
        {
          "name": "MYSQL_PORT",
          "value": "3306"
        },
        {
          "name": "MYSQL_DATABASE",
          "value": "music_library"
        },
        {
          "name": "MYSQL_USER",
          "value": "admin"
        },
        {
          "name": "MYSQL_PASSWORD",
          "value": "<your-password-here>"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/music-library",
          "awslogs-region": "us-west-2",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]' \
  --region us-west-2

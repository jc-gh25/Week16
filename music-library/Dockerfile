# Stage 1: Build with Maven (Using AWS Mirror to avoid Rate Limits)
FROM public.ecr.aws/docker/library/maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime (Using AWS Mirror)
FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install dependencies (curl, bash, ssl certs)
RUN apk add --no-cache curl bash ca-certificates

# Set URL
ENV PUBLIC_URL=http://project.jcarl.net

# Copy scripts
COPY update-namesilo-dns.sh /update-namesilo-dns.sh
COPY startup.sh /startup.sh

# Run sed to remove Windows carriage returns (\r) from the scripts
RUN sed -i 's/\r$//' /update-namesilo-dns.sh /startup.sh

# Make executable
RUN chmod +x /update-namesilo-dns.sh /startup.sh

COPY --from=build /app/target/*.jar /app/app.jar

# Expose standard HTTP port
EXPOSE 80
ENTRYPOINT ["/startup.sh"]
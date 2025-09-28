#!/bin/bash

echo "🚀 Starting Smart Edu Platform Deployment..."

# Build the application
echo "📦 Building application..."
mvn clean package -DskipTests

# Build Docker images
echo "🐳 Building Docker images..."
docker-compose down
docker-compose build --no-cache

# Start services
echo "🔧 Starting services..."
docker-compose up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check application health
echo "🏥 Checking application health..."
curl -f http://localhost:8080/actuator/health

if [ $? -eq 0 ]; then
    echo "✅ Deployment successful! Application is running on http://localhost:8080"
    echo "📊 Check logs with: docker-compose logs -f app"
else
    echo "❌ Deployment failed! Check logs with: docker-compose logs app"
    exit 1
fi
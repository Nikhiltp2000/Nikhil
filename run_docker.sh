#!/bin/bash

# Function to display usage instructions
display_usage() {
    echo "Usage: $0 <profile> <version>"
    echo "Available profiles: dev, prod"
}

# Check if the required arguments (profile and version) are provided
if [ $# -ne 2 ]; then
    display_usage
    exit 1
fi

# Check if the provided profile is valid
if [ "$1" != "dev" ] && [ "$1" != "prod" ]; then
    echo "Invalid profile. Available profiles: dev, prod"
    exit 1
fi

# Set the version provided as the second argument
version=$2

# Clean and install the Maven project with the specified profile
mvn clean install -P$1

# Check if Maven build was successful
if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting..."
    exit 1
fi

# Build a Docker image for the application with the specified version
docker build -t app-img:$version .

# Check if Docker image build was successful
if [ $? -ne 0 ]; then
    echo "Docker image build failed. Exiting..."
    exit 1
fi

# Stop and remove the existing container if it exists
docker stop app-container 2>/dev/null
docker rm app-container 2>/dev/null


# Run the Docker container with the specified name and the newly created image
docker run -p 8080:8080 --network="host" --name app-container -e "SPRING_PROFILES_ACTIVE=$1" app-img:$version

# Display the container ID
container_id=$(docker ps -q -f "name=app-container")
echo "Container ID: $container_id"
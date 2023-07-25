#!/bin/bash

# Function to display usage instructions
display_usage() {
    echo "Usage: $0 <profile>"
    echo "Available profiles: dev, prod"
}

# Check if the required argument (profile) is provided
if [ $# -ne 1 ]; then
    display_usage
    exit 1
fi

# Check if the provided profile is valid
if [ "$1" != "dev" ] && [ "$1" != "prod" ]; then
    echo "Invalid profile. Available profiles: dev, prod"
    exit 1
fi

# Clean and install the Maven project
mvn clean install -P$1

# Check if Maven build was successful
if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting..."
    exit 1
fi

# Run the Spring Boot application with the selected profile
mvn spring-boot:run -Dspring-boot.run.profiles=$1


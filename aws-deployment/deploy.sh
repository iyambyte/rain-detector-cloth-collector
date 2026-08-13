#!/bin/bash
# EC2 Provisioning Script for Ubuntu 22.04 LTS

echo "Updating system..."
sudo apt-get update -y
sudo apt-get upgrade -y

echo "Installing Java 17..."
sudo apt-get install openjdk-17-jdk -y

echo "Installing Nginx..."
sudo apt-get install nginx -y

echo "Setting up Application Directory..."
sudo mkdir -p /var/www/clothcollector
sudo chown -R $USER:$USER /var/www/clothcollector

echo "Setup complete! Please upload your 'dist' folder and 'clothcollector-0.0.1-SNAPSHOT.jar' to /var/www/clothcollector"

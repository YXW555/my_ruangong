# PowerShell script: Set Aliyun environment variables and start Spring Boot application
# Make sure you run this script from the backend directory (where pom.xml is located)

# Set environment variables
$env:ALIYUN_ACCESS_KEY_ID = "LTAI5tCTqqaDCkJUua23F7bb"
$env:ALIYUN_ACCESS_KEY_SECRET = "vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi"

# Display confirmation
Write-Host "Environment variables set successfully" -ForegroundColor Green
Write-Host "ALIYUN_ACCESS_KEY_ID = $env:ALIYUN_ACCESS_KEY_ID" -ForegroundColor Cyan
Write-Host "ALIYUN_ACCESS_KEY_SECRET = ***HIDDEN***" -ForegroundColor Cyan

# Start Spring Boot application
Write-Host ""
Write-Host "Starting Spring Boot application with Aliyun NLP API..." -ForegroundColor Yellow
mvn spring-boot:run
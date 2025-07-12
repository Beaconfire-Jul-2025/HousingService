#!/bin/bash

# Script to generate JWT token for HR user
# Requires JWT_KEY environment variable to be set

set -e

# Check if JWT_KEY environment variable is set
if [ -z "$JWT_KEY" ]; then
    echo "Error: JWT_KEY environment variable is not set"
    exit 1
fi

# JWT Header (Base64 encoded)
header='{"alg":"HS256","typ":"JWT"}'
header_b64=$(echo -n "$header" | base64 | tr -d '=' | tr '/+' '_-' | tr -d '\n')

# JWT Payload with HR user details
# Set issued at time (iat) to current timestamp
# Set expiration time (exp) to 24 hours from now
current_time=$(date +%s)
exp_time=$((current_time + 86400)) # 24 hours = 86400 seconds

payload="{\"sub\":\"HR\",\"iat\":$current_time,\"exp\":$exp_time,\"role\":\"ROLE_HR\"}"
payload_b64=$(echo -n "$payload" | base64 | tr -d '=' | tr '/+' '_-' | tr -d '\n')

# Create signature
signature_input="${header_b64}.${payload_b64}"
signature=$(echo -n "$signature_input" | openssl dgst -sha256 -hmac "$JWT_KEY" -binary | base64 | tr -d '=' | tr '/+' '_-' | tr -d '\n')

# Construct the JWT token
jwt_token="${header_b64}.${payload_b64}.${signature}"

# Output the token
echo "$jwt_token"
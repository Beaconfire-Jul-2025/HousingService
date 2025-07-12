#!/bin/bash

set -e

if [ -z "$BASE_URL" ]; then
    echo "Error: BASE_URL environment variable is not set"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

JWT_TOKEN_SCRIPT="$SCRIPT_DIR/000-token.sh"

if [ ! -f "$JWT_TOKEN_SCRIPT" ]; then
    echo "Error: JWT token generation script not found at $JWT_TOKEN_SCRIPT"
    exit 1
fi

chmod +x "$JWT_TOKEN_SCRIPT"

echo "Generating JWT token for HR user..."
JWT_TOKEN=$("$JWT_TOKEN_SCRIPT")

if [ -z "$JWT_TOKEN" ]; then
    echo "Error: Failed to generate JWT token"
    exit 1
fi

echo "JWT token generated successfully"

echo "Testing GET /housing/facility-report/1"

GET_RESPONSE=$(curl -s -X GET \
    --location "${BASE_URL}/housing/facility-report/1" \
    --header "Authorization: Bearer $JWT_TOKEN")

if [ -n "$GET_RESPONSE" ]; then
    echo "GET /housing/facility-report/1 successful:"
    echo "$GET_RESPONSE"

else
    echo "Error: GET /housing/facility-report/1 failed or returned empty response."
    exit 1
fi
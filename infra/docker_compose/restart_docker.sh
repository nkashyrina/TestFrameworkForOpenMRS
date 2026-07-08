#!/bin/bash

PROJECT_NAME="openmrs"

echo ">>> Stopping docker-compose"
docker compose -p "$PROJECT_NAME" down
echo ">>> Containers stopped"

echo ">>> docker pull all browser images"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JSON_FILE="$SCRIPT_DIR/config/browsers.json"

echo "Using file: $JSON_FILE"

parse_images() {
    grep -E '"image"[[:space:]]*:[[:space:]]*"[^"]+"' "$JSON_FILE" | \
    sed -E 's/^[[:space:]]*"image"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/' | \
    sed 's/[[:space:]]*$//'
}

IMAGES=$(parse_images)

if [ -z "$IMAGES" ]; then
    echo "No images found in $JSON_FILE"
    echo "Check the file format. Example:"
    echo '{ "image": "nginx:latest" }'
    echo 'or'
    echo '[{ "image": "nginx:latest" }]'
    exit 1
fi

for image in $IMAGES; do
    echo "Pulling $image..."
    docker pull "$image"
done

echo ">>> Starting Docker Compose"
docker compose -p "$PROJECT_NAME" up -d

#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export BROWSER=${BROWSER:-chrome}

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

SKIP_BUILD=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    firefox)
      export BROWSER="firefox"
      ;;
    --skip-build)
      SKIP_BUILD=true
      ;;
  esac
  shift
done

echo "### Selected browser: $BROWSER ###"

if [ "$BROWSER" = "firefox" ]; then
  docker pull selenoid/vnc_firefox:125.0
else
  docker pull selenoid/vnc_chrome:127.0
fi

echo '### Java version ###'
java --version

if [ "$SKIP_BUILD" = false ]; then
  echo "### Stopping and removing old containers ###"
  docker compose down
  docker_containers=$(docker ps -a -q)

  if [ -n "$docker_containers" ]; then
    docker stop $docker_containers
    docker rm $docker_containers
  fi

  docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')
  if [ -n "$docker_images" ]; then
    echo "### Removing images: $docker_images ###"
    docker rmi $docker_images
  fi

  echo "### Running build ###"
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :rococo-e2e:test

  echo "### Starting all containers ###"
  docker compose up -d
else
  echo "### Skipping build and image cleanup ###"

  echo "### Recreating test container with new browser ###"
  docker compose rm -f rococo-e2e
  docker compose up -d rococo-e2e
fi

docker ps -a

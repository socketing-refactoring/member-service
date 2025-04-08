#!/usr/bin/env bash

echo "Running Spotless formatting..."

if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OS" == "Windows_NT" ]]; then
  echo "Detected Windows environment"
  bash ./gradlew lintApply
else
  echo "Detected Unix/Linux/macOS environment"
  /bin/bash ./gradlew lintApply
fi

#!/usr/bin/env bash
set -euo pipefail

KEYSTORE_PATH=${1:-release.keystore}
ALIAS=${2:-launcher_key}

keytool -genkey -v \
  -keystore "$KEYSTORE_PATH" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

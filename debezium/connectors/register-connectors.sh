#!/bin/bash
DESTINATION='localhost:8083/connectors'
for FILENAME in *.json; do
  echo "Registering connector from config file $FILENAME..."
  curl -i -X POST -H "Content-Type: application/json" -d @"$FILENAME" "$DESTINATION"
done

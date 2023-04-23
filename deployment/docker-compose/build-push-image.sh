#!/bin/bash

/usr/local/bin/docker-compose -f ./deployment/docker-compose/docker-compose.build.yml push trip-service voucher-service air-quality-service user-service stream-data-generator
#!/bin/sh

CONFIG=$1
SCALAR_CONFIG_DIR=network/scalardl

docker-compose -f ${SCALAR_CONFIG_DIR}/${CONFIG} down

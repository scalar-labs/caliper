#!/bin/sh

CONFIG=$1
SLEEP_SEC=$2
SCALAR_CONFIG_DIR=network/scalardl

rm -f ${SCALAR_CONFIG_DIR}/cfssl/data/*
docker-compose -f ${SCALAR_CONFIG_DIR}/${CONFIG} up -d

echo "Waiting for starting Scalar DL servers (${SLEEP_SEC} seconds)"
sleep ${SLEEP_SEC}

docker-compose -f ${SCALAR_CONFIG_DIR}/${CONFIG} exec -T cassandra cqlsh -f /create_schema.cql

# About the network

This directory contains a sample __Fabric v1.4.1__ network with the following properties.

## Topology
* The network has 3 participating organizations.
* The network has 3 orderer nodes in __Kafka mode__.
  - 4 Kafka brokers
  - 3 Zookeeper servers
* Each organization has 1 peer in the network.
* The peers use __CouchDB__ as the world-state database.
* A channel named `mychannel` is created and the peers are joined.

## Communication protocol
* The `docker-compose.yaml` file specifies a network __without TLS__ communication.

## Platform configurations

The following network configuration file is available, containing the listed chaincodes that will be deployed (installed and instantiated).

* `fabric-ccp-go.json` (__golang__ implementations)
  * `marbles` __with__ CouchDB index metadata and rich query support.
  * `drm`
  * `simple`
  * `smallbank`

## Usage
The network is configured in __docker swarm mode__. Do the following steps before starting.

1. Setup swarm mode using commands such as `docker swarm init` and `docker swarm join`.
2. Change the placement policy in `docker-compose.yaml` file. For example, specify the hostname in your environment for `node.hostname` in the `constraints` entry.

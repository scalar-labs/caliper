## Scalar DL adapter for Hyperledger Caliper

Hyperledger Caliper is a blockchain performance benchmark framework, which allows users to test different blockchain solutions with predefined use cases, and get a set of performance test results. Scalar DL adapter provides facilities to test Scalar DL with Hyperledger Caliper.

Currently supported performance indicators (same as Caliper):
* Success rate
* Transaction/Read throughput
* Transaction/Read latency(minimum, maximum, average, percentile)
* Resource consumption (CPU, Memory, Network IO,...)

For more information on using Caliper please consult the [documentation site](https://hyperledger.github.io/caliper/). As for details of Scalar DL, see [this site](https://scalardl.readthedocs.io/en/latest/).

## Pre-requisites

Make sure following tools are installed:
* node-gyp, python2, make, g++ and git (for fetching and compiling some packages during install)
* Node.js v8.X LTS or v10.X LTS (for running Caliper)
* Docker and Docker Compose (only needed when running local examples, or using Caliper through its Docker image)

## Building Caliper

To install the basic dependencies of the repository, and to resolve the cross-references between the different packages in the repository, you must execute the following commands from the root of the repository directory:
1. `npm i`: Installs development-time dependencies, such as [Lerna](https://github.com/lerna/lerna#readme) and the license checking package.
2. `npm run repoclean`: Cleans up the `node_modules` directory of all packages in the repository. Not needed for a freshly cloned repository.
3. `npm run bootstrap`: Installs the dependencies of all packages in the repository and links any cross-dependencies between the packages. It will take some time to finish installation. If it is interrupted by `ctrl+c`, please recover the `package.json` file first and then run `npm run bootstrap` again.

Or as a one-liner:
```console
user@ubuntu:~/caliper$ npm i && npm run repoclean -- --yes && npm run bootstrap
```

> __Note:__ do not run any of the above commands with `sudo`, as it will cause the bootstrap process to fail.

## Running Sample Benchmarks

Before running a benchmark, clone configuration files from [here](https://github.com/scalar-labs/caliper-benchmarks).

To run a benchmark, go to `packages/caliper-cli` directory and execute the following command.

```
node caliper.js launch master \\
  --caliper-workspace /path/to/caliper-benchmarks \\
  --caliper-benchconfig benchmarks/scenario/smallbank/config.yaml \\
  --caliper-networkconfig networks/scalardl/scalardl_smallbank.json
```

- --caliper-workspace : path to a workspace directory that has configuration files (required).
- --caliper-benchconfig : relative path from the workspace to the benchmark configuration file (required).
- --caliper-networkconfig : relative path from the workspace to the config file of the blockchain network under test (required).

All predefined benchmarks can be found in `caliper-benchmarks/benchmarks`. Example network configurations for Scalar DL such as client properties and contract settings can be found in `caliper-benchmarks/networks/scalardl`.

## Running Sample Benchmarks in Distributed Clients

Before running a benchmark in distributed clients, you need to run a MQTT broker such as [Mosquitto](https://mosquitto.org/).

First, launch Caliper with the master mode. In the following case, the MQTT broker is running localhost.

```
node caliper.js launch master \\
  --caliper-workspace /path/to/caliper-benchmarks \\
  --caliper-benchconfig benchmarks/scenario/smallbank/config.yaml \\
  --caliper-networkconfig networks/scalardl/scalardl_smallbank.json \\
  --caliper-worker-remote true \\
  --caliper-worker-communication-method mqtt \\
  --caliper-worker-communication-address mqtt://localhost:1883
```

Then, launch Caliper with the worker mode.

```
node caliper.js launch worker \\
  --caliper-workspace /path/to/caliper-benchmarks \\
  --caliper-benchconfig benchmarks/scenario/smallbank/config.yaml \\
  --caliper-networkconfig networks/scalardl/scalardl_smallbank.json \\
  --caliper-worker-remote true \\
  --caliper-worker-communication-method mqtt \\
  --caliper-worker-communication-address mqtt://localhost:1883
```

Make sure to use same configuration files in both master and worker. When the number of workers reached to the one specified in the benchmark configuration file, the test will start.

## Checking results

You can find a result HTML file in `caliper-benchmarks`. Note that the file will be overwritten in each benchmark run.

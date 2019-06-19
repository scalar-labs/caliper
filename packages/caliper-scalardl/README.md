## Scalar DL adapter for Hyperledger Caliper

Hyperledger Caliper is a blockchain performance benchmark framework, which allows users to test different blockchain solutions with predefined use cases, and get a set of performance test results. Scalar DL adapter provides facilities to test Scalar DL with Hyperledger Caliper.

Currently supported performance indicators (same as Caliper):
* Success rate
* Transaction/Read throughput
* Transaction/Read latency(minimum, maximum, average, percentile)
* Resource consumption (CPU, Memory, Network IO,...)

For more information on using Caliper please consult the [documentation site](https://hyperledger.github.io/caliper/). As for details of Scalar DL, see also [this site](https://scalardl.readthedocs.io/en/latest/).

## Pre-requisites

Make sure following tools are installed:
* NodeJS 8 (LTS), 9, or 10 (LTS) *we do not support higher versions as the dependancy chain does not permit this*
* node-gyp
* Docker
* Docker-compose

## Building Caliper
Caliper is split into pacakges that are managed by Lerna, a tool for managing JavaScript projects with multiple packages. To build Caliper, it is necessary to first pull the required base dependancies, and then bootstrap the Caliper project. Note that if you modify base code, it is necessary to rebuild the project

* Run `npm install` in Caliper root folder to install base dependencies locally
* Run `npm run repoclean` in Caliper root folder to ensure that all the packages are clean
* Run `npm run bootstrap` to bootstrap the packages in the Caliper repository. This will install all package dependancies and link any cross dependancies. It will take some time to finish installation. If it is interrupted by ctrl+c, please recover the file package.json first and then run `npm run bootstrap` again.

## Running a Benchmark

To run a benchmark, go to `packages/caliper-cli` directory and execute the following command.

```
node caliper.js benchmark run -w <path to workspace> -c <benchmark config> -n <blockchain config>
```

- -w : path to a workspace directory (required)
- -c : relative path from the workspace to the benchmark configuration file (required).
- -n : relative path from the workspace to the config file of the blockchain network under test (required).

All predefined benchmarks can be found in `packages/caliper-samples/benchmark`. Example network configurations for Scalar DL such as client properties and contract settings can be found in `packages/caliper-samples/network/scalardl`.

A complete example is as follows.

```
node caliper.js benchmark run -w ../caliper-samples -c benchmark/simple/config.yaml -n network/scalardl/scalardl_simple.json
```

**NOTE:** Caliper CLI, which is a wrapper of the above command can be used for other blockchain solutions. But it is still under developing (e.g., not published to npm), so we do not use it at this moment.

## Checking results

You can find a result HTML file (report-YYYYMMDDTXXXX.html) in the execution directory.

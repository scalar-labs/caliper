/**
 * Copyright 2019 Scalar, Inc. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * @file, definition of the Scalar DL class, which implements the Caliper's NBI for Scalar DL.
 */

'use strict';

const fs = require('fs');
const {ClientService} = require('@scalar-labs/scalardl-node-client-sdk');
const {BlockchainInterface, CaliperUtils, TxStatus} = require('caliper-core');
const logger = CaliperUtils.getLogger('scalardl.js');

/**
    Read the connection details from the config file.
    @param {object} config Adapter config.
    @param {string} workspace_root The absolute path to the root location for the configuration files.
    @return {object} clientProperties.
*/
function getClientProperties(config, workspace_root) {
    let host = config.scalardl.network.client_properties.host;
    let port = config.scalardl.network.client_properties.port;
    let chid = config.scalardl.network.client_properties.cert_holder_id;
    let cver = config.scalardl.network.client_properties.cert_version;
    let tls  = config.scalardl.network.client_properties.tls_enabled;
    let cert_pem = fs.readFileSync(CaliperUtils.resolvePath(config.scalardl.network.client_properties.cert_path, workspace_root)).toString();
    let private_key_pem = fs.readFileSync(CaliperUtils.resolvePath(config.scalardl.network.client_properties.private_key_path, workspace_root)).toString();

    return {
        'scalar.ledger.client.server_host': host,
        'scalar.ledger.client.server_port': port,
        'scalar.ledger.client.cert_holder_id': chid,
        'scalar.ledger.client.cert_pem': cert_pem,
        'scalar.ledger.client.cert_version': cver,
        'scalar.ledger.client.private_key_pem': private_key_pem,
        'scalar.ledger.client.tls.enabled': tls,
    };
}

/**
    Read the contract details from the config file.
    @param {object} config Adapter config.
    @return {object} function map.
*/
function getFunctionMap(config) {
    let fcnMap = {};
    for (const contract of config.scalardl.contracts) {
        fcnMap[contract.fcn] = contract.id;
    }
    return fcnMap;
}

/**
 * Implements {BlockchainInterface} for a ScalarDL backend.
 */
class ScalarDL extends BlockchainInterface {

    /**
   * Create a new instance of the {ScalarDL} class.
   * @param {string} config_path The path of the Scalar DL network configuration file.
   * @param {string} workspace_root The absolute path to the root location for the application configuration files.
   */
    constructor(config_path, workspace_root) {
        super(config_path);
        this.statusInterval = null;
        this.bcType = 'scalardl';
        this.workspaceRoot = workspace_root;
    }

    /**
     * Initialize the {ScalarDL} object.
     * @return {Promise} The return promise.
     * @async
     */
    async init() {
        // Do something that we would like to do just once for a run
        logger.info('compiling all contracts......');
        let config  = require(this.configPath);

        // Build contracts
        let execSync = require('child_process').execSync;
        let contractRoot = CaliperUtils.resolvePath(config.contract.path, this.workspaceRoot);
        let result =  execSync('./gradlew assemble', {cwd: contractRoot});
        logger.info(result.toString());

        // Register a client
        let cp = getClientProperties(config, this.workspaceRoot);
        let clientService = new ClientService(cp);
        try {
            logger.info('registering a client......');
            let res = await clientService.registerCertificate();
            let status = res.getStatus();
            if (status !== 200) {
                throw new Error(`Scalar DL responded with status "${status}"`);
            }
        } catch(err) {
            logger.error(`Scalar DL client registration failed: ${(err.stack ? err.stack : err)}`);
            throw err;
        }

        return CaliperUtils.sleep(2000);
    }

    /**
     * Deploy the contracts specified in the network configuration file.
     * @async
     */
    async installSmartContract() {
        let config  = require(this.configPath);
        let cp = getClientProperties(config, this.workspaceRoot);
        let clientService = new ClientService(cp);

        try {
            logger.info('installing all contracts......');

            let contractRoot = CaliperUtils.resolvePath(config.contract.path, this.workspaceRoot);
            for (const contract of config.scalardl.contracts) {
                logger.info(`Installing contract ${contract.id}...`);

                let buff = fs.readFileSync(CaliperUtils.resolvePath(contract.path, contractRoot));
                let res = await clientService.registerContract(contract.id, contract.name, new Uint8Array(buff));

                let status = res.getStatus();
                if (status !== 200) {
                    throw new Error(`Scalar DL responded with status "${status}"`);
                }
            }
        } catch(err) {
            logger.error(`Scalar DL contracts install failed: ${(err.stack ? err.stack : err)}`);
            throw err;
        }
    }

    /**
     * Return the ScalarDL context associated with the given callback module name.
     * @param {string} name Unused.
     * @param {object} args Unused.
     * @return {object} The assembled ScalarDL context.
     * @async
     */
    async getContext(name, args) {
        let config  = require(this.configPath);
        let context = config.scalardl.context;

        if(typeof context === 'undefined') {
            let cp = getClientProperties(config, this.workspaceRoot);
            let clientService = new ClientService(cp);
            let fcnMap = getFunctionMap(config);
            context = {clientService: clientService, functionMap: fcnMap};
        }

        return Promise.resolve(context);
    }

    /**
     * Release the given ScalarDL context.
     * @param {object} context The ScalarDL context to release.
     * @async
     */
    async releaseContext(context) {
        // nothing to do
    }

    /**
   * Invoke a smart contract.
   * @param {Object} context Context object.
   * @param {String} contractID Identity of the contract.
   * @param {String} contractVer Version of the contract.
   * @param {Array} args Array of JSON formatted arguments for multiple transactions.
   * @param {Number} timeout Request timeout, in seconds.
   * @return {Promise<object>} The promise for the result of the execution.
   */
    async invokeSmartContract(context, contractID, contractVer, args, timeout) {
        let promises = [];
        args.forEach((item, index)=>{
            // Not good args handling in Caliper framework
            let tmp = [];
            let func;
            for(let key in item) {
                if(key === 'verb' || key === 'transaction_type') {
                    func = item[key].toString();
                }
                else {
                    tmp.push(item[key].toString());
                }
            }
            if(!func) {
                func = tmp.shift();
            }
            promises.push(this.execContract(context, contractID, contractVer, func, item));
        });
        return await Promise.all(promises);
    }

    /**
   * Execute a Scalar DL contract with the specified args.
   * @param {Object} context Context object.
   * @param {String} contractID Identity of the contract.
   * @param {String} contractVer Version of the contract.
   * @param {String} fcn Function name (contract nick name) to be executed.
   * @param {Object} args JSON formatted arguments for a single execution
   * @return {Promise<TxStatus>} Result and stats of the transaction invocation.
   */
    async execContract(context, contractID, contractVer, fcn, args) {
        let clientService = context.clientService;
        let fcnMap = context.functionMap;
        let txStatus = new TxStatus();

        if (context.engine) {
            context.engine.submitCallback(1);
        }

        try {
            let res = await clientService.executeContract(fcnMap[fcn], args);
            let status = res.getStatus();
            if (status === 200) {
                txStatus.SetStatusSuccess();
                txStatus.SetResult(res.getResult());
            } else {
                throw new Error(`Scalar DL responded with status "${status}"`);
            }
        } catch (err) {
            logger.error(`Scalar DL contract execution failed: ${(err.stack ? err.stack : err)}`);
            txStatus.SetStatusFail();
        }

        return txStatus;
    }

    /**
     * Query the given smart contract according to the specified options.
     * @param {object} context The ScalarDL context returned by {getContext}.
     * @param {string} contractID The name of the contract.
     * @param {string} contractVer The version of the contract.
     * @param {string} key The argument to pass to the smart contract query.
     * @param {string} [fcn=query] The contract query function name.
     * @return {Promise<object>} The promise for the result of the execution.
     */
    async queryState(context, contractID, contractVer, key, fcn = 'query') {
        /*
          Note:
          When implementing a query contract for Scalar DL,
          use this general-purpose key name and map arbitrary resources
          for querying them as following examples.
          - 'account' in the 'simple' workload
          - 'customer_id' in the 'smallbank' workload
          - 'item_id' in the 'drm' workload
        */
        let args = {'query_key': key};
        return this.execContract(context, contractID, contractVer, fcn, args);
    }

    /**
   * Get adapter specific transaction statistics.
   * @param {JSON} stats txStatistics object
   * @param {Array} results array of txStatus objects.
   */
    getDefaultTxStats(stats, results) {
        // empty
    }
}
module.exports = ScalarDL;

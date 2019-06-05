/**
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * @file, definition of the Scalar DL client worker
 */

'use strict';

const {CaliperLocalClient, CaliperUtils} = require('caliper-core');
const ScalarDLClient = require('./scalardl');

let caliperClient;
/**
 * Message handler
 */
process.on('message', async (message) => {

    if (!message.hasOwnProperty('type')) {
        process.send({type: 'error', data: 'unknown message type'});
        return;
    }

    try {
        switch (message.type) {
        case 'init': {
            const blockchain = new ScalarDLClient(message.absNetworkFile, message.networkRoot);
            caliperClient = new CaliperLocalClient(blockchain);
            process.send({type: 'ready', data: {pid: process.pid, complete: true}});
            break;
        }
        case 'test': {
            let result = await caliperClient.doTest(message);

            await CaliperUtils.sleep(200);
            process.send({type: 'testResult', data: result});
            break;
        }
        default: {
            process.send({type: 'error', data: 'unknown message type [' + message.type + ']'});
        }
        }
    }
    catch (err) {
        process.send({type: 'error', data: err.toString()});
    }
});

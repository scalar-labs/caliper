/**
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * @file, definition of the Scalar DL client factory
 */

'use strict';

const childProcess = require('child_process');
const path = require('path');

/**
 * Class used to spawn Scalar DL client workers
 */
class ScalarDLClientFactory {

    /**
     * Require paths to configuration data used when calling new on scalardl.js
     * @param {String} absNetworkFile absolute workerPath
     * @param {Sting} workspaceRoot root location
     */
    constructor(absNetworkFile, workspaceRoot){
        this.absNetworkFile = absNetworkFile;
        this.workspaceRoot = workspaceRoot;
    }


    /**
     * Spawn the worker and perform required init
     * @returns {Object} the child process
     */
    async spawnWorker() {
        const child = childProcess.fork(path.join(__dirname, './scalardlClientWorker.js'));

        const msg = {
            type: 'init',
            absNetworkFile: this.absNetworkFile,
            networkRoot: this.workspaceRoot
        };
        child.send(msg);

        return child;
    }
}

module.exports = ScalarDLClientFactory;

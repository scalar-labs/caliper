/**
 * Copyright 2020 Scalar, Inc. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * @file, definition of the Scalar DL client factory
 */

'use strict';

const ScalarDLAdapter = require('./scalardl');

/**
 * Constructs a Scalar DL adapter.
 * @param {number} workerIndex The zero-based index of the worker who wants to create an adapter instance. -1 for the master process.
 * @return {Promise<BlockchainInterface>} The initialized adapter instance.
 * @async
 */
async function adapterFactory(workerIndex) {
    return new ScalarDLAdapter(workerIndex);
}

module.exports.adapterFactory = adapterFactory;

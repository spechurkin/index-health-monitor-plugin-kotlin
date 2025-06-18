/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
*/

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
*/

package org.opensearch.indexhealthmonitor.action

import org.opensearch.common.Table
import org.opensearch.core.rest.RestStatus
import org.opensearch.indexhealthmonitor.service.JsonConverterService.Companion.getMessageFormatted
import org.opensearch.rest.BaseRestHandler.RestChannelConsumer
import org.opensearch.rest.BytesRestResponse
import org.opensearch.rest.RestChannel
import org.opensearch.rest.RestHandler.Route
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestRequest.Method.GET
import org.opensearch.rest.action.cat.AbstractCatAction
import org.opensearch.transport.client.node.NodeClient

class EchoAction : AbstractCatAction() {

    override fun getName(): String {
        return "echo_action"
    }

    override fun routes(): List<Route> {
        return listOf(
            Route(GET, "/echo"),
            Route(GET, "/echo/{message}")
        )
    }

    override fun doCatRequest(request: RestRequest?, client: NodeClient?): RestChannelConsumer {
        val message: String = request?.param("message") ?: "no message provided"

        return RestChannelConsumer { channel: RestChannel ->
            channel.sendResponse(
                BytesRestResponse(
                    RestStatus.OK,
                    "application/json",
                    getMessageFormatted(message)
                )
            )
        }
    }

    override fun getTableWithHeader(request: RestRequest?): Table {
        return Table()
    }

    override fun documentation(sb: StringBuilder) {
        sb.append(documentation())
    }

    companion object {
        /**
         * Documents this REST action.
         *
         * @return Syntax documentation.
         */
        fun documentation(): String {
            return """
                /echo
                /echo/{message}
                """
        }
    }
}

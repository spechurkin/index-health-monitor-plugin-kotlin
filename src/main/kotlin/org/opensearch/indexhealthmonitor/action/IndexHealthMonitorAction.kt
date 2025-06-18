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

import org.opensearch.action.admin.cluster.health.ClusterHealthRequest
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse
import org.opensearch.common.Table
import org.opensearch.indexhealthmonitor.service.JsonConverterService.Companion.getIndexHealthStatus
import org.opensearch.rest.BaseRestHandler.RestChannelConsumer
import org.opensearch.rest.RestChannel
import org.opensearch.rest.RestHandler.Route
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestRequest.Method.GET
import org.opensearch.rest.RestResponse
import org.opensearch.rest.action.RestResponseListener
import org.opensearch.rest.action.cat.AbstractCatAction
import org.opensearch.rest.action.cat.RestTable
import org.opensearch.transport.client.node.NodeClient

class IndexHealthMonitorAction : AbstractCatAction() {

    override fun getName(): String {
        return "index_health_monitor_action"
    }

    override fun routes(): List<Route> {
        return listOf(
            Route(GET, "/nn2/metrics/"),
            Route(GET, "/nn2/metrics/clusterName"),
            Route(GET, "/nn2/metrics/taskMaxWaitingTime"),
            Route(GET, "/nn2/metrics/status"),
            Route(GET, "/nn2/metrics/activeShards"),
            Route(GET, "/nn2/metrics/relocatingShards"),
            Route(GET, "/nn2/metrics/initializingShards"),
            Route(GET, "/nn2/metrics/unassignedShards"),
            Route(GET, "/nn2/metrics/delayedUnassignedShards"),
            Route(GET, "/nn2/metrics/numberOfNodes"),
            Route(GET, "/nn2/metrics/numberOfDataNodes"),
            Route(GET, "/nn2/metrics/activePrimaryShards"),
            Route(GET, "/nn2/metrics/activeShardsPercent")
        )
    }

    override fun doCatRequest(request: RestRequest?, client: NodeClient?): RestChannelConsumer {
        val metric: String? = request?.path()?.split("/".toRegex())?.last()

        val healthRequest = ClusterHealthRequest()
        return RestChannelConsumer { channel: RestChannel? ->
            client?.admin()?.cluster()
                ?.health(healthRequest, object : RestResponseListener<ClusterHealthResponse?>(channel) {
                    override fun buildResponse(response: ClusterHealthResponse?): RestResponse {
                        return RestTable.buildResponse(
                            response?.let {
                                getIndexHealthStatus(
                                    it,
                                    metric
                                )
                            }, channel
                        )
                    }
                })
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
                /nn2/metrics
                /nn2/metrics/clusterName
                /nn2/metrics/taskMaxWaitingTime
                /nn2/metrics/status
                /nn2/metrics/activeShards
                /nn2/metrics/relocatingShards
                /nn2/metrics/initializingShards
                /nn2/metrics/unassignedShards
                /nn2/metrics/delayedUnassignedShards
                /nn2/metrics/numberOfNodes
                /nn2/metrics/numberOfDataNodes
                /nn2/metrics/activePrimaryShards
                /nn2/metrics/activeShardsPercent
                """
        }
    }
}

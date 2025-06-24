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

package org.opensearch.indexhealthmonitor.service

import com.fasterxml.jackson.core.JsonFactory
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse
import org.opensearch.common.Table
import java.io.StringWriter
import java.time.Instant

class JsonConverterService {
    companion object {
        fun getMessageFormatted(message: String?): String {
            return pairToJson("message", message)
        }

        fun getIndexHealthStatus(response: ClusterHealthResponse, metric: String?): Table {
            val map = mapOf(
                "clusterName" to response.clusterName,
                "currentDate" to Instant.now(),
                "taskMaxWaitingTime" to response.taskMaxWaitingTime,
                "status" to response.status.name,
                "activeShards" to response.activeShards,
                "relocatingShards" to response.relocatingShards,
                "initializingShards" to response.initializingShards,
                "unassignedShards" to response.unassignedShards,
                "delayedUnassignedShards" to response.delayedUnassignedShards,
                "numberOfNodes" to response.numberOfNodes,
                "numberOfDataNodes" to response.numberOfDataNodes,
                "activePrimaryShards" to response.activePrimaryShards,
                "activeShardsPercent" to response.activeShardsPercent
            )

            val table = Table()
            table.startHeaders()
            table.addCell("Index Health Status JSON")
            table.endHeaders()

            if (metric.isNullOrEmpty()) {
                table.startRow()
                table.addCell(mapToJson(map))
                table.endRow()
            } else if (map.containsKey(metric)) {
                table.startRow()
                table.addCell(pairToJson(metric, map[metric]))
                table.endRow()
            }

            return table
        }

        private fun mapToJson(map: Map<String, Any>): String {
            val writer = StringWriter()
            val factory = JsonFactory()
            val generator = factory.createGenerator(writer)

            generator.writeStartObject()

            for ((key, value) in map) {
                generator.writeStringField(key, value.toString())
            }

            generator.writeEndObject()
            generator.close()

            return writer.toString()
        }

        private fun pairToJson(key: String, value: Any?): String {
            val writer = StringWriter()
            val factory = JsonFactory()
            val generator = factory.createGenerator(writer)

            generator.writeStartObject()
            generator.writeStringField(key, value.toString())
            generator.writeEndObject()
            generator.close()

            return writer.toString()
        }
    }
}

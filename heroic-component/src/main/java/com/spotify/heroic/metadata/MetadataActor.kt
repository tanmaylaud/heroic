/*
 * Copyright (c) 2019 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.heroic.metadata

import com.spotify.heroic.common.Statistics

interface MetadataActor {
    val statistics: Statistics
        get() = Statistics.empty()

    /**
     * Buffer a write for the specified series.
     */
    fun write(request: WriteMetadata.Request): WriteMetadata

    fun findTags(request: FindTags.Request): FindTags

    fun findSeries(request: FindSeries.Request): FindSeries

    fun findSeriesIds(request: FindSeriesIds.Request): FindSeriesIds

    fun countSeries(request: CountSeries.Request): CountSeries

    fun deleteSeries(request: DeleteSeries.Request): DeleteSeries

    fun findKeys(request: FindKeys.Request): FindKeys
}
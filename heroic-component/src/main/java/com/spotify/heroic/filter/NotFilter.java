/*
 * Copyright (c) 2015 Spotify AB.
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

package com.spotify.heroic.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;
import com.spotify.heroic.ObjectHasher;
import com.spotify.heroic.common.Series;

@AutoValue
@JsonTypeName("not")
public abstract class NotFilter implements Filter {
    @JsonCreator
    public static NotFilter create(@JsonProperty("filter") Filter filter) {
        return new AutoValue_NotFilter(filter);
    }
    public static final String OPERATOR = "not";
    public abstract Filter filter();

    @Override
    public boolean apply(Series series) {
        return !filter().apply(series);
    }

    @Override
    public <T> T visit(final Visitor<T> visitor) {
        return visitor.visitNot(this);
    }

    @Override
    public String toString() {
        return "[" + OPERATOR + ", " + filter() + "]";
    }

    @Override
    public Filter optimize() {
        return filter().visit(new Visitor<Filter>() {
            @Override
            public Filter visitNot(final NotFilter not) {
                return not.filter().optimize();
            }

            @Override
            public Filter defaultAction(final Filter filter) {
                return NotFilter.create(filter.optimize());
            }
        });
    }

    @Override
    public String operator() {
        return OPERATOR;
    }

    @Override
    public int compareTo(Filter o) {
        if (!NotFilter.class.isAssignableFrom(o.getClass())) {
            return operator().compareTo(o.operator());
        }

        return filter().compareTo(o);
    }

    @Override
    public String toDSL() {
        return "!(" + filter().toDSL() + ")";
    }

    @Override
    public void hashTo(final ObjectHasher hasher) {
        hasher.putObject(getClass(), () -> {
            hasher.putField("filter", filter(), hasher.with(Filter::hashTo));
        });
    }

    public static NotFilter of(final Filter filter) {
        return NotFilter.create(filter);
    }
}

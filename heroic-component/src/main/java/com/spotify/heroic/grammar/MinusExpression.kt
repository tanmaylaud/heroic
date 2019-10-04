/*
 * Copyright (c) 2019 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"): you may not use this file except in compliance
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

package com.spotify.heroic.grammar

data class MinusExpression(
    @JvmField val context: Context,
    val left: Expression,
    val right: Expression
): Expression {
    override fun getContext() = context

    override fun eval(scope: Expression.Scope): Expression? {
        return left.eval(scope).sub(right.eval(scope))
    }

    override fun <R : Any?> visit(visitor: Expression.Visitor<R>): R {
        return visitor.visitMinus(this)
    }

    override fun toRepr() = "${left.toRepr()} - ${right.toRepr()}"
}

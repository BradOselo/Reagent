/*
 * Copyright 2017 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reagent.operator

import reagent.Emitter
import reagent.Many
import reagent.Maybe
import reagent.One
import reagent.Task
import kotlin.DeprecationLevel.ERROR

fun <I, O> Many<I>.map(mapper: (I) -> O): Many<O> = ManyMap(this, mapper)

fun <I, O> Maybe<I>.map(mapper: (I) -> O): Maybe<O> = MaybeMap(this, mapper)

fun <I, O> One<I>.map(mapper: (I) -> O): One<O> = OneMap(this, mapper)

@Suppress("DeprecatedCallableAddReplaceWith") // TODO https://youtrack.jetbrains.com/issue/KT-19512
@Deprecated("Task has no items so mapping does not make sense.", level = ERROR)
fun <O> Task.map(mapper: (Nothing) -> O): Task = this

internal class ManyMap<in U, out D>(
    private val upstream: Many<U>,
    private val mapper: (U) -> D
) : Many<D>() {
  override suspend fun subscribe(emit: Emitter<D>) {
    upstream.subscribe { emit(mapper(it)) }
  }
}

internal class MaybeMap<in U, out D>(
    private val upstream: Maybe<U>,
    private val mapper: (U) -> D
) : Maybe<D>() {
  override suspend fun produce() = upstream.produce()?.let(mapper)
}

internal class OneMap<in U, out D>(
    private val upstream: One<U>,
    private val mapper: (U) -> D
) : One<D>() {
  override suspend fun produce() = mapper(upstream.produce())
}

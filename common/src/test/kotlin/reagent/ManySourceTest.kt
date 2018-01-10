/*
 * Copyright 2016 Jake Wharton
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
package reagent

import reagent.tester.testMany
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ManySourceTest {
  @Test fun just() {
    Many.just("Hello")
        .testMany {
          item("Hello")
          complete()
        }
  }

  @Test fun fromArray() {
    Many.fromArray("Hello", "World")
        .testMany {
          item("Hello")
          item("World")
          complete()
        }
  }

  @Test fun arrayToMany() {
    arrayOf("Hello", "World")
        .toMany()
        .testMany {
          item("Hello")
          item("World")
          complete()
        }
  }

  @Test fun fromIterable() {
    Many.fromIterable(listOf("Hello", "World"))
        .testMany {
          item("Hello")
          item("World")
          complete()
        }
  }

  @Test fun iterableToMany() {
    listOf("Hello", "World")
        .toMany()
        .testMany {
          item("Hello")
          item("World")
          complete()
        }
  }

  @Test fun empty() {
    Many.empty<Any>()
        .testMany {
          complete()
        }
  }

  @Test fun error() {
    val exception = RuntimeException("Oops!")
    Many.error<Any>(exception)
        .testMany {
          error(exception)
        }
  }

  @Test fun returning() {
    var called = false
    Many.returning { called = true; 0 }
        .testMany {
          item(0)
          complete()
        }
    assertTrue(called)
  }

  @Test fun returningThrowing() {
    val exception = RuntimeException("Oops!")
    Many.returning { throw exception }
        .testMany {
          error(exception)
        }
  }

  @Test fun running() {
    var called = false
    Many.running<Any> { called = true }
        .testMany {
          complete()
        }
    assertTrue(called)
  }

  @Test fun runningThrowing() {
    val exception = RuntimeException("Oops!")
    Many.running<Any> { throw exception }
        .testMany {
          error(exception)
        }
  }

  @Test fun defer() {
    var called = 0
    val deferred = Many.defer { called++; Many.just("Hello") }
    deferred.testMany {
      item("Hello")
      complete()
    }
    assertEquals(1, called)
    deferred.testMany {
      item("Hello")
      complete()
    }
    assertEquals(2, called)
  }
}

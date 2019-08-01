/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.graphgen.client

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

fun main() {
    var previousS: String? = null
    while (true) {
        try {
            val s = Socket("10.0.1.148", 3333)
                .use {
                    BufferedReader(InputStreamReader(it.getInputStream()))
                        .useLines { it.toList() }
                        .joinToString(separator = " ")
                }

            println(s)
            if (previousS != s) {
                val p = Runtime.getRuntime().exec("dot -Tpng -ograph.png")

                BufferedWriter(OutputStreamWriter(p.outputStream)).use {
                    it.write(s)
                    it.newLine()
                }

                p.inputStream
                    .use {
                        BufferedReader(InputStreamReader(it))
                            .useLines { it.toList() }
                            .joinToString()
                            .let(::println)
                    }
            }
            previousS = s
            Thread.sleep(1000)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
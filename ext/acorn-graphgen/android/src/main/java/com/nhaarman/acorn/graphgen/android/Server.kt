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

package com.nhaarman.acorn.graphgen.android

import java.io.BufferedWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

internal class Server(
    private val graph: Graph
) {

    private val serverSocket by lazy { ServerSocket(3333) }

    fun start() = thread {
        while (true) {
            println("LISTENING")
            val socket = serverSocket.accept()
            dealWith(socket)
        }
    }

    private fun dealWith(socket: Socket) = thread {
        println("CONNECTED")
        BufferedWriter(PrintWriter(socket.getOutputStream()))
            .use {
                println("SENDING ${graph.toDot()}")
                it.append(graph.toDot())
                it.newLine()
            }

//        socket.close()
    }
}
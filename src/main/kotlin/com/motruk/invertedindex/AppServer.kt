package com.motruk.invertedindex

import com.motruk.invertedindex.data.DataSource
import com.motruk.invertedindex.data.DataSourceImpl
import java.io.DataInputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

const val QUIT_COMMAND = "close"
const val SOCKET_PORT = 333

object AppServer {
    private lateinit var dataSource: DataSource

    @JvmStatic
    fun main(args: Array<String>) {
        val server = ServerSocket(SOCKET_PORT)
        println("Server Started")
        dataSource = DataSourceImpl()

        val socket = server.accept()
        println("New client connected")
        val `in` = DataInputStream(socket.getInputStream())
        val out = DataOutputStream(socket.getOutputStream())


        var userEnter: String
        while (true) {
            out.writeUTF("Enter word to find, '$QUIT_COMMAND' to exit")
            userEnter = `in`.readUTF()
            out.writeUTF("User Write - $userEnter")
            if (userEnter.equals(QUIT_COMMAND, true)) {
                println("Client is disconnected");
                closeConnection(socket, `in`, out)
                break
            }

            println("Finding a word for the user: $userEnter")
            val dataResult = dataSource.findData(userEnter)
            sendDataToClient(
                out = out,
                data = dataResult
            )
        }
    }

    private fun sendDataToClient(out: DataOutput, data: String) {
        val dataList = data.chunked(150)
        out.writeInt(dataList.size)
        dataList.forEach { text ->
            out.writeUTF(text)
        }
    }

    private fun closeConnection(socket: Socket, `in`: DataInputStream, out: DataOutputStream) {
        try {
            socket.close()
            `in`.close()
            out.close()
            dataSource.dispose()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}






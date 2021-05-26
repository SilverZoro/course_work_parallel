package com.motruk.invertedindex

import java.io.DataInput
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.*


object AppClient {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val inputScanner = Scanner(System.`in`)
            val socket = Socket("localhost", SOCKET_PORT)
            val inputStream = DataInputStream(socket.getInputStream())
            val outputStream = DataOutputStream(socket.getOutputStream())
            var userInput: String

            while (true) {
                println(inputStream.readUTF())
                userInput = inputScanner.nextLine()
                outputStream.writeUTF(userInput)
                if (userInput.equals(QUIT_COMMAND, true)) {
                    println("Bye bye")
                    killConnection(socket, inputStream, outputStream)
                    break
                }
                println(inputStream.readUTF())
                println(getResultFromServer(inputStream))
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }

    private fun getResultFromServer(inputStream: DataInput): String {
        val textCount = inputStream.readInt() - 1
        var text = ""
        for (i in 0..textCount) {
            text += inputStream.readUTF()
        }
        return text
    }

    private fun killConnection(socket: Socket, `in`: DataInputStream, out: DataOutputStream) {
        try {
            socket.close()
            `in`.close()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

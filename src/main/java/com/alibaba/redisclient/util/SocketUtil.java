package com.alibaba.redisclient.util;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/***
 * @author pei
 * @date 2023/8/31 18:05
 */
public class SocketUtil {

    public static void main(String[] args) throws IOException {
        sendRequest("127.0.0.1", 6379, 10);
    }

    public static ArrayList<Socket> sockets = new ArrayList<>();

    public static void sendRequest(String host, int port, Integer num) throws IOException {
        for (int i = 0; i < num; i++) {
            Socket socket = new Socket(host, port);
            System.out.println("创建socket成功,本地socket端口：" + socket.getLocalPort()+",当前远程地址:"+host+":"+port);
            sockets.add(socket);
        }
    }
    public static void closeSocket()  {
        sockets.forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
            }
        });
    }

}

package com.alibaba.redisclient.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: SocketClient2
 * Description:
 *
 * @author Anakki
 * @date 2023/9/8 22:55
 */
public class SocketClient2 {
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    public static void startSocketClient(Integer seconds,String ip,Integer port) throws IOException, InterruptedException {
        //创建socket连接
        Socket socket = new Socket(ip, port);
        executorService.submit(()->{
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try{
                    String startMessage = "我发送开始,当前时间"+new Date();
                    writer.write(startMessage);
                    writer.flush();//刷新缓冲流，把write提交进去，不然服务端没接收到内容
                    System.out.println("本机端口:" + socket.getLocalPort() + "发送了数据:" + startMessage);
                    Thread.sleep(1000L * seconds);
                    String endMessage = "我发送完毕,当前时间"+new Date();
                    writer.write(endMessage);
                    writer.flush();//刷新缓冲流，把write提交进去，不然服务端没接收到内容
                    System.out.println("本机端口:" + socket.getLocalPort() + "发送了数据:" + endMessage);
            }catch (Exception e){
                try {
                    writer.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println(e.getMessage());
            }
        });
        //发送数据到服务端
    }
}

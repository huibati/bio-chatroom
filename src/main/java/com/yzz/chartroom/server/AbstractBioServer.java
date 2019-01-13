package com.yzz.chartroom.server;

import com.yzz.chartroom.protocol.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/13
 *
 * @Since 0.0.1
 */
public abstract class AbstractBioServer {
    //默认端口
    public static final int DEFAULT_PORT = 8080;

    //默认地址
    public static final String DEFAULT_LOCATION = "127.0.0.1";

    //客户端连接(socket连接)的队列最大长度
    public static final int DEFAULT_BACKLOG = 50;

    //服务端口
    protected final int port;

    //连接队列的最大长度
    protected final int backlog;

    //地址
    protected final String location;

    //Socket监听服务对象
    protected ServerSocket serverSocket;

    //Socket服务监听地址包装类
    protected InetAddress inetAddress;

    //这是一个线程安全的非阻塞的队列
    protected volatile  ConcurrentLinkedQueue<Socket> clientSockets = new ConcurrentLinkedQueue<>();

    //日志
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    protected AbstractBioServer(int port, int backlog, String location) throws IOException {
        this.port = port;
        this.backlog = backlog;
        this.location = location;
        inetAddress = InetAddress.getByName(location);
        //创建一个端口监听对象，监听客户端连接，此刻服务端已经就绪
        serverSocket = new ServerSocket(port, backlog, inetAddress);
        logger.log(Level.INFO, "BIO server has started in " + location + " listening port " + port);
    }

    /**
     * 清除资源 从队列中去移除
     *
     * @param client
     */
    public void remove(Socket client) {
        try {
            boolean result = clientSockets.remove(client);
            if (result && client != null && !client.isClosed()) {
                //关闭连接
                client.close();
                logger.log(Level.WARNING, "移除client" + client.getInetAddress().getHostAddress() + ":" + client.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * 监听客户端连接
     *
     * @throws IOException
     */
    protected abstract void listen() throws IOException;

    /**
     * 提供客户端消息服务
     *
     * @param client
     */
    protected abstract void provideService(Socket client);

    /**
     * 获取客户端消息
     *
     * @param client
     * @return
     * @throws IOException
     */
    protected abstract Message readRequest(Socket client) throws IOException;

    /**
     * 转发客户端消息
     *
     * @param message
     */
    protected abstract void dispatch(Message message);
}

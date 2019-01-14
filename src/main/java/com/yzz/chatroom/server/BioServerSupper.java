package com.yzz.chatroom.server;

import com.yzz.chatroom.protocol.BioChartRoomProtocolSupper;
import com.yzz.chatroom.protocol.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * describe: 聊天室服务端
 * E-mail:yzzstyle@163.com  date:2019/1/12
 * 你可以简单的这样去启动一个服务端
 *
 * @Since 0.0.1
 * <p>
 * public class Server {
 * public static void main(String[] args) {
 * try {
 * BioServer bioServer = BioServer.newServerByPort(9090);
 * bioServer.listen();
 * } catch (Exception e) {
 * e.printStackTrace();
 * }
 * }
 * }
 */
public class BioServerSupper extends AbstractBioServer {

    private final int core = Runtime.getRuntime().availableProcessors();

    //循环监听线程
    private final ThreadPoolExecutor providerPool = new ThreadPoolExecutor(core, core, 2, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    //等待队列
    private volatile ConcurrentLinkedQueue<Socket> waitQueue = new ConcurrentLinkedQueue();


    //处理消息线程池
    private ExecutorService handelMessagePool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 创建一个默认的聊天室服务端
     *
     * @return
     * @throws IOException
     */
    public static BioServer createServerByDefault() throws IOException {
        return new BioServer(DEFAULT_PORT, DEFAULT_BACKLOG, DEFAULT_LOCATION);
    }

    /**
     * 创建一个自定义端口的服务端
     *
     * @param port
     * @return
     * @throws IOException
     */
    public static BioServerSupper newServerByPort(int port) throws IOException {
        return new BioServerSupper(port, DEFAULT_BACKLOG, DEFAULT_LOCATION);
    }

    /**
     * 创建一个自定义端口和连接队列深度的服务
     *
     * @param port    端口
     * @param logback 连接队列最大num
     * @return
     * @throws IOException
     */
    public static BioServerSupper newServerWithDefaultLocation(int port, int logback) throws IOException {
        return new BioServerSupper(port, logback, DEFAULT_LOCATION);
    }

    /**
     * 自定义端口、连接大小、地址的服务端对象
     *
     * @param port
     * @param logback
     * @param location
     * @return
     * @throws IOException
     */
    public static BioServerSupper newServerDynamic(int port, int logback, String location) throws IOException {
        return new BioServerSupper(port, logback, location);
    }

    protected BioServerSupper(int port, int backlog, String location) throws IOException {
        super(port, backlog, location);
    }

    /**
     * 轮询监听客户端输入
     */
    public void loopClientForProviderServer() {
        providerPool.execute(
                () -> {
                    while (true) {
                        Socket[] clients = new Socket[clientSockets.size()];
                        //监听
                        listenClientInputStatus(clientSockets.toArray(clients));
                    }
                }
        );
    }

    /**
     * 监听客户端输入状态
     *
     * @param clients
     */
    public void listenClientInputStatus(Socket[] clients) {
        for (Socket client : clients) {
            if (!clientIsOk(client)) {
                remove(client);
                continue;
            }
            if (waitQueue.contains(client)) {
                continue;
            }
            try {
                InputStream inputStream = client.getInputStream();
                if (inputStream.available() > 0) {
                    //如果有消息，则加入等待处理的队列，一旦加入不可重复消费
                    waitQueue.add(client);
                    //提供转发服务
                    provideService(client);
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
                remove(client);
            }
        }
    }

    /**
     * 判断客户端是否有效
     *
     * @param client
     * @return
     */
    public boolean clientIsOk(Socket client) {
        if (client == null || client.isClosed() || client.isInputShutdown() || client.isOutputShutdown()) {
            return false;
        }
        return true;
    }

    /**
     * 监听，服务的入口
     *
     * @throws IOException
     */
    @Override
    public void listen() throws IOException {
        //开启监听客户端的消息流状态
        loopClientForProviderServer();
        //轮询监听
        while (true) {
            Socket client = serverSocket.accept();
            clientSockets.add(client);
            logger.log(Level.INFO, client.getInetAddress().getHostAddress() + "加入了supper聊天室");
            logger.log(Level.INFO, "当前在线人数：" + clientSockets.size());
        }
    }


    /**
     * 提供妆发服务
     *
     * @param client
     */
    @Override
    protected void provideService(Socket client) {
        handelMessagePool.execute(() -> {
            //检查
            if (!clientIsOk(client)) {
                return;
            }
            try {
                //获取客户端的消息
                Message message = readRequest(client);
                if (message == null) {
                    return;
                }
                if (client != null) {
                    //出队，任务消息转发完毕，可再次提供服务
                    waitQueue.remove(client);
                }
                //转发客户端的消息
                dispatch(message);
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.WARNING, e.getMessage());
                remove(client);
            }
        });
    }

    @Override
    protected Message readRequest(Socket client) throws IOException {
        InputStream in = client.getInputStream();
        //自定义协议负责去解析消息
        return BioChartRoomProtocolSupper.getInstance().parse(in);
    }

    @Override
    protected void dispatch(Message message) {
        Iterator<Socket> clients = clientSockets.iterator();
        Socket client = null;
        //遍历发送至客户端，该方法是同步方法，so，客户端收到的消息是先后顺序和其进入聊天室的顺序是一致的
        while (clients.hasNext()) {
            try {
                client = clients.next();
                logger.log(Level.INFO, Thread.currentThread().getName() + "->转发消息至" + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                //协议去写入消息至客户端
                if (clientIsOk(client)) {
                    BioChartRoomProtocolSupper.getInstance().write(client.getOutputStream(), message);
                }
            } catch (IOException e) {
                //这里的异常表示客户端已经下线，此时需要去清除客户端连接资源
                e.printStackTrace();
                logger.log(Level.INFO, e.getMessage());
                remove(client);
            }
        }
    }
}

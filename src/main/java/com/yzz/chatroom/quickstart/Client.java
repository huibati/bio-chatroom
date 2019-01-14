package com.yzz.chatroom.quickstart;

import com.yzz.chatroom.client.BioClient;
import com.yzz.chatroom.server.BioServer;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/12
 *
 * @Since 0.0.1
 */
public class Client {
    private final static String MSG = "please input msg: \n 1. -a=xxx 设置address \n 2. -p=xxx 设置端口号 \n 3. -n=xxx 设置nickName ";
    private static Logger logger = Logger.getLogger("bio-chatroom-client");
    public static final String NICK_NAME = "尹忠政";


    public static void main(String[] args) {

        try {
            Scanner sc = new Scanner(System.in);
            BioClient bioClient = init(args);
            bioClient.listen12n(message -> System.out.println(message.toString()));
            while (true) {
                String content = sc.next();
                bioClient.sendMsg(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public static BioClient init(String[] args) throws IOException {
        logger.log(Level.WARNING, MSG);
        Argument argument = new Argument();
        for (String a : args) {
            initArguement(a, argument);
        }
        argument.check();
        return new BioClient(argument.getPort(), argument.getAddress(), argument.getNickName());
    }

    public static void initArguement(String arg, Argument argument) {
        if (arg.startsWith("-a")) {
            argument.setAddress(arg.replaceAll("-a=", ""));
            return;
        }
        if (arg.startsWith("-p")) {
            try {
                argument.setPort(Integer.valueOf(arg.replaceAll("-p=", "")));
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage());
                logger.log(Level.WARNING, MSG);
            }
            return;
        }
        if (arg.startsWith("-n")) {
            argument.setNickName(arg.replaceAll("-n=", ""));
            return;
        }
    }

    static class Argument {
        private String address;
        private int port;
        private String nickName;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public void check() {
            if (null == address || address.equals("")) {
                address = BioServer.DEFAULT_LOCATION;
            }

            if (port == 0) {
                port = BioServer.DEFAULT_PORT;
            }

            if (null == nickName || nickName.equals("")) {
                nickName = NICK_NAME;
            }
        }
    }
}

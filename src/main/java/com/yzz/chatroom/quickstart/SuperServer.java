package com.yzz.chatroom.quickstart;

import com.yzz.chatroom.server.BioServer;
import com.yzz.chatroom.server.BioServerSupper;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/13
 *
 * @Since 0.0.1
 */
public class SuperServer {
    private final static String MSG = "please input msg: \n 1. -p=xxx 设置端口号 ";
    private static Logger logger = Logger.getLogger("bio-chatroom-supper--client");

    public static void main(String[] args) {
        BioServerSupper serverSupper = null;
        try {
            serverSupper = BioServerSupper.newServerByPort(args.length == 0 ? BioServer.DEFAULT_PORT : Integer.valueOf(args[0].replaceAll("-p=", "")));
            serverSupper.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

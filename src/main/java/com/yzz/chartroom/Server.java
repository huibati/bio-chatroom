package com.yzz.chartroom;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/12
 *
 * @Since 0.0.1
 */
public class Server {

    private final static String MSG = "please input msg: \n 1. -p=xxx 设置端口号 ";
    private static Logger logger = Logger.getLogger("bio-chartroom-client");

    public static void main(String[] args) {
        try {
            logger.log(Level.WARNING, MSG);
            BioServer bioServer = BioServer.newServerByPort(args.length == 0 ? BioServer.DEFAULT_PORT : Integer.valueOf(args[0].replaceAll("-p=","")));
            bioServer.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

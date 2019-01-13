package com.yzz.chartroom.protocol;

import java.io.*;

/**
 * describe: 自定义协议的操作类包括 读和写 升级版
 * E-mail:yzzstyle@163.com  date:2019/1/12
 * 使用 jdk自带的序列化机制，将Message消息对象作为CS交互的介质
 *
 * @Since 0.0.1
 */
public class BioChartRoomProtocolSupper implements Protocol {

    private static volatile BioChartRoomProtocolSupper bioChartRoomProtocolSupper;

    @Override
    public void write(OutputStream outputStream, Header header, String content) throws IOException {
        BioChartRoomProtocol.getInstance().write(outputStream, header, content);
    }

    @Override
    public void write(OutputStream outputStream, Message message) throws IOException {
        BioChartRoomProtocol.getInstance().write(outputStream, message);
    }

    @Override
    public Message parse(InputStream inputStream) throws IOException {
        //有数据
        if (inputStream.available() > 0) {
            return BioChartRoomProtocol.getInstance().parse(inputStream);
        }
        return null;
    }

    public static BioChartRoomProtocolSupper getInstance() {
        if (null != bioChartRoomProtocolSupper) {
            return bioChartRoomProtocolSupper;
        }
        synchronized (BioChartRoomProtocolSupper.class) {
            if (null == bioChartRoomProtocolSupper) {
                bioChartRoomProtocolSupper = new BioChartRoomProtocolSupper();
            }
        }
        return bioChartRoomProtocolSupper;
    }
}

package com.yzz.chatroom.protocol;

import java.io.*;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/13
 *
 * @Since 0.0.1
 */
public interface Protocol {

    /**
     * 些消息 适用于客户单发消息
     *
     * @param outputStream
     * @param header
     * @param content
     * @throws IOException
     */
    void write(OutputStream outputStream, Header header, String content) throws IOException;

    /***
     * 写消息 使用服务端转发消息
     * @param outputStream
     * @param message
     * @throws IOException
     */
    void write(OutputStream outputStream, Message message) throws IOException;

    /**
     * 读操作，将消息转化成消息对象
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    Message parse(InputStream inputStream) throws IOException;
}

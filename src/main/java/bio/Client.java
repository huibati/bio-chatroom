package bio;

import bio.protocol.Message;

import java.util.Scanner;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/12
 *
 * @Since 0.0.1
 */
public class Client {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            String nickName = args.length == 0 ? "尹忠政" : args[0];
            BioClient bioClient = new BioClient(9090, "127.0.0.1", nickName);
            bioClient.listen12n(message -> {
                System.out.println(message.toString());
            });
            while (true) {
                String content = sc.next();
                bioClient.sendMsg(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

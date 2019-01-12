package bio;

/**
 * describe:
 * E-mail:yzzstyle@163.com  date:2019/1/12
 *
 * @Since 0.0.1
 */
public class Server {
    public static void main(String[] args) {
        try {
            BioServer bioServer = BioServer.newServerByPort(args.length == 0 ? 2333 : Integer.valueOf(args[0]));
            bioServer.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

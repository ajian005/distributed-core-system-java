package core.system.redis.publish2subscribe;
/**
 * 消息发布者 （即publish client）https://www.yuque.com/duzhi-zlcvv/egd9g9/bnq1ut6ihu705pvl#uHe1U
 */

import redis.clients.jedis.Jedis;

public class KVStorePubClient {
    private Jedis jedis;

    public KVStorePubClient(String host, int port, String password) {
        jedis = new Jedis(host, port);
        //KVStore的实例密码
        String authString = jedis.auth(password);
        if (!authString.equals("OK")) {
            System.err.println("AUTH Failed: " + authString);
            return;
        }
    }

    public void pub(String channel, String message) {
        System.out.println("  >>> 发布(PUBLISH) > Channel:" + channel + " > 发送出的Message:" + message);
        jedis.publish(channel, message);
    }

    public void close(String channel) {
        System.out.println("  >>> 发布(PUBLISH)结束 > Channel:" + channel + " > Message:quit");
        //消息发布者结束发送，即发送一个“quit”消息；
        jedis.publish(channel, "quit");
    }
}
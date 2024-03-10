package core.system.redis.publish2subscribe;

/**
 * 示例主程序
 */

import java.util.UUID;

import redis.clients.jedis.JedisPubSub;

public class KVStorePubSubTest {
    //ApsaraDB for Redis的连接信息，从控制台可以获得
    static final String host = "xxxxxxxxxx.m.cnhza.kvstore.aliyuncs.com";
    static final int port = 6379;
    static final String password = "password";//password

    public static void main(String[] args) throws Exception {
        KVStorePubClient pubClient = new KVStorePubClient(host, port, password);
        final String channel = "KVStore频道-A";
        //消息发送者开始发消息，此时还无人订阅，所以此消息不会被接收
        pubClient.pub(channel, "Aliyun消息1：（此时还无人订阅，所以此消息不会被接收）");
        //消息接收者
        KVStoreSubClient subClient = new KVStoreSubClient(host, port, password);
        JedisPubSub listener = new KVStoreMessageListener();
        subClient.setChannelAndListener(listener, channel);
        //消息接收者开始订阅
        subClient.start();
        //消息发送者继续发消息
        for (int i = 0; i < 5; i++) {
            String message = UUID.randomUUID().toString();
            pubClient.pub(channel, message);
            Thread.sleep(1000);
        }
        //消息接收者主动取消订阅
        subClient.unsubscribe(channel);
        Thread.sleep(1000);
        pubClient.pub(channel, "Aliyun消息2：（此时订阅取消，所以此消息不会被接收）");
        //消息发布者结束发送，即发送一个“quit”消息；
        //此时如果有其他的消息接收者，那么在listener.onMessage()中接收到“quit”时，将执行“unsubscribe”操作。
        pubClient.close(channel);
    }
}
package core.system.redis.publish2subscribe;

/**
 * 消息订阅者 （即subscribe client）
 */

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class KVStoreSubClient extends Thread {
    private Jedis jedis;
    private String channel;
    private JedisPubSub listener;

    public KVStoreSubClient(String host, int port, String password) {
        jedis = new Jedis(host, port);
        //ApsaraDB for Redis的实例密码
        String authString = jedis.auth(password);//password
        if (!authString.equals("OK")) {
            System.err.println("AUTH Failed: " + authString);
            return;
        }
    }

    public void setChannelAndListener(JedisPubSub listener, String channel) {
        this.listener = listener;
        this.channel = channel;
    }

    private void subscribe() {
        if (listener == null || channel == null) {
            System.err.println("Error:SubClient> listener or channel is null");
        }
        System.out.println("  >>> 订阅(SUBSCRIBE) > Channel:" + channel);
        System.out.println();
        //接收者在侦听订阅的消息时，将会阻塞进程，直至接收到quit消息（被动方式），或主动取消订阅
        jedis.subscribe(listener, channel);
    }

    public void unsubscribe(String channel) {
        System.out.println("  >>> 取消订阅(UNSUBSCRIBE) > Channel:" + channel);
        System.out.println();
        listener.unsubscribe(channel);
    }

    @Override
    public void run() {
        try {
            System.out.println();
            System.out.println("----------订阅消息SUBSCRIBE 开始-------");
            subscribe();
            System.out.println("----------订阅消息SUBSCRIBE 结束-------");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
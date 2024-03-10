package core.system.redis.publish2subscribe;

/**
 * 消息监听者
 */

import redis.clients.jedis.JedisPubSub;

public class KVStoreMessageListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        System.out.println("  <<< 订阅(SUBSCRIBE)< Channel:" + channel + " >接收到的Message:" + message);
        System.out.println();
        //当接收到的message为quit时，取消订阅（被动方式）
        if (message.equalsIgnoreCase("quit")) {
            this.unsubscribe(channel);
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        // TODO Auto-generated method stub
    }
}
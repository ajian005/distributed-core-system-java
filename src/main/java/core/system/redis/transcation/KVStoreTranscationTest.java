package core.system.redis.transcation;

/**
 *
 */

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class KVStoreTranscationTest {
    static final String host = "xxxxxx.m.cnhza.kvstore.aliyuncs.com";
    static final int port = 6379;
    static final String password = "password";
    //**注意这两个key的内容是不同的
    static String client1_key = "KVStore-Transcation-1";
    static String client2_key = "KVStore-Transcation-2";

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis(host, port);
        // ApsaraDB for Redis的实例密码
        String authString = jedis.auth(password);//password
        if (!authString.equals("OK")) {
            System.err.println("认证失败: " + authString);
            jedis.close();
            return;
        }
        jedis.set(client1_key, "0");
        // 启动另一个thread，模拟另外的client
        new KVStoreTranscationTest().new OtherKVStoreClient().start();
        Thread.sleep(500);
        Transaction tx = jedis.multi();//开始事务
        // 以下操作会集中提交服务器端处理，作为“原子操作”
        tx.incr(client1_key);
        tx.incr(client1_key);
        Thread.sleep(400);//此处Thread的暂停对事务中前后连续的操作并无影响，其他Thread的操作也无法执行
        tx.incr(client1_key);
        Thread.sleep(300);//此处Thread的暂停对事务中前后连续的操作并无影响，其他Thread的操作也无法执行
        tx.incr(client1_key);
        Thread.sleep(200);//此处Thread的暂停对事务中前后连续的操作并无影响，其他Thread的操作也无法执行
        tx.incr(client1_key);
        List<Object> result = tx.exec();//提交执行
        // 解析并打印出结果
        for (Object rt : result) {
            System.out.println("Client 1 > 事务中> " + rt.toString());
        }
        jedis.close();
    }

    class OtherKVStoreClient extends Thread {
        @Override
        public void run() {
            Jedis jedis = new Jedis(host, port);
            // ApsaraDB for Redis的实例密码
            String authString = jedis.auth(password);// password
            if (!authString.equals("OK")) {
                System.err.println("AUTH Failed: " + authString);
                jedis.close();
                return;
            }
            jedis.set(client2_key, "100");
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client 2 > " + jedis.incr(client2_key));
            }
            jedis.close();
        }
    }
}
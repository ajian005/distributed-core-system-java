package core.system.redis.pipeline;

/**
 *
 */

import java.util.Date;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisPipelinePerformanceTest {
    static final String host = "xxxxxx.m.cnhza.kvstore.aliyuncs.com";
    static final int port = 6379;
    static final String password = "password";

    public static void main(String[] args) {
        Jedis jedis = new Jedis(host, port);
        //ApsaraDB for Redis的实例密码
        String authString = jedis.auth(password);// password
        if (!authString.equals("OK")) {
            System.err.println("AUTH Failed: " + authString);
            jedis.close();
            return;
        }
        //连续执行多次命令操作
        final int COUNT = 5000;
        String key = "KVStore-Tanghan";
        // 1 ---不使用pipeline操作---
        jedis.del(key);//初始化key
        Date ts1 = new Date();
        for (int i = 0; i < COUNT; i++) {
            //发送一个请求，并接收一个响应（Send Request and  Receive Response）
            jedis.incr(key);
        }
        Date ts2 = new Date();
        System.out.println("不用Pipeline > value为:" + jedis.get(key) + " > 操作用时：" + (ts2.getTime() - ts1.getTime()) + "ms");
        //2 ----对比使用pipeline操作---
        jedis.del(key);//初始化key
        Pipeline p1 = jedis.pipelined();
        Date ts3 = new Date();
        for (int i = 0; i < COUNT; i++) {
            //发出请求 Send Request
            p1.incr(key);
        }
        //接收响应 Receive Response
        p1.sync();
        Date ts4 = new Date();
        System.out.println("使用Pipeline > value为:" + jedis.get(key) + " > 操作用时：" + (ts4.getTime() - ts3.getTime()) + "ms");
        jedis.close();
    }
}
package core.system.redis.pipeline;

/**
 *
 */
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class PipelineClientTest {
    static final String host = "xxxxxxxx.m.cnhza.kvstore.aliyuncs.com";
    static final int port = 6379;
    static final String password = "password";

    public static void main(String[] args) {
        Jedis jedis = new Jedis(host, port);
        // ApsaraDB for Redis的实例密码
        String authString = jedis.auth(password);// password
        if (!authString.equals("OK")) {
            System.err.println("AUTH Failed: " + authString);
            jedis.close();
            return;
        }
        String key = "KVStore-Test1";
        jedis.del(key);//初始化
        // -------- 方法1
        Pipeline p1 = jedis.pipelined();
        System.out.println("-----方法1-----");
        for (int i = 0; i < 5; i++) {
            p1.incr(key);
            System.out.println("Pipeline发送请求");
        }
        // 发送请求完成，开始接收响应
        System.out.println("发送请求完成，开始接收响应");
        List<Object> responses = p1.syncAndReturnAll();
        if (responses == null || responses.isEmpty()) {
            jedis.close();
            throw new RuntimeException("Pipeline error: 没有接收到响应");
        }
        for (Object resp : responses) {
            System.out.println("Pipeline接收响应Response: " + resp.toString());
        }
        System.out.println();

        //-------- 方法2
        System.out.println("-----方法2-----");
        jedis.del(key);//初始化
        Pipeline p2 = jedis.pipelined();
        //需要先声明Response
        Response<Long> r1 = p2.incr(key);
        System.out.println("Pipeline发送请求");
        Response<Long> r2 = p2.incr(key);
        System.out.println("Pipeline发送请求");
        Response<Long> r3 = p2.incr(key);
        System.out.println("Pipeline发送请求");
        Response<Long> r4 = p2.incr(key);
        System.out.println("Pipeline发送请求");
        Response<Long> r5 = p2.incr(key);
        System.out.println("Pipeline发送请求");
        try {
            r1.get();  //此时还未开始接收响应，所以此操作会出错
        } catch (Exception e) {
            System.out.println(" <<< Pipeline error：还未开始接收响应  >>> ");
        }
        // 发送请求完成，开始接收响应
        System.out.println("发送请求完成，开始接收响应");
        p2.sync();
        System.out.println("Pipeline接收响应Response: " + r1.get());
        System.out.println("Pipeline接收响应Response: " + r2.get());
        System.out.println("Pipeline接收响应Response: " + r3.get());
        System.out.println("Pipeline接收响应Response: " + r4.get());
        System.out.println("Pipeline接收响应Response: " + r5.get());
        jedis.close();
    }
}
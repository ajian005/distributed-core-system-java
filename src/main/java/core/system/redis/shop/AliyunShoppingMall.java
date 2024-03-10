package core.system.redis.shop;

/**
 * 网上商城商品相关性分析 https://help.aliyun.com/zh/redis/use-cases/correlation-analysis-on-e-commerce-store-items?spm=a2c4g.11186623.0.0.3c7964974ri1XN
 */

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

public class AliyunShoppingMall {
    public static void main(String[] args) {
        //ApsaraDB for Redis的连接信息，从控制台可以获得
        String host = "xxxxxxxx.m.cnhza.kvstore.aliyuncs.com";
        int port = 6379;
        Jedis jedis = new Jedis(host, port);
        try {
            //ApsaraDB for Redis的实例密码
            String authString = jedis.auth("password");//password
            if (!authString.equals("OK")) {
                System.err.println("AUTH Failed: " + authString);
                return;
            }
            //产品列表
            String key0 = "阿里云:产品:啤酒";
            String key1 = "阿里云:产品:巧克力";
            String key2 = "阿里云:产品:可乐";
            String key3 = "阿里云:产品:口香糖";
            String key4 = "阿里云:产品:牛肉干";
            String key5 = "阿里云:产品:鸡翅";
            final String[] aliyunProducts = new String[]{key0, key1, key2, key3, key4, key5};
            //初始化，清除可能的已有旧数据
            for (int i = 0; i < aliyunProducts.length; i++) {
                jedis.del(aliyunProducts[i]);
            }
            //模拟用户购物
            for (int i = 0; i < 5; i++) {//模拟多人次的用户购买行为
                customersShopping(aliyunProducts, i, jedis);
            }
            System.out.println();
            //利用ApsaraDB for Redis来输出各个商品间的关联关系
            for (int i = 0; i < aliyunProducts.length; i++) {
                System.out.println(">>>>>>>>>>与" + aliyunProducts[i] + "一起被购买的产品有<<<<<<<<<<<<<<<");
                List<Tuple> relatedList = jedis.zrevrangeWithScores(aliyunProducts[i], 0, -1);
                for (Tuple item : relatedList) {
                    System.out.println("商品名称：" + item.getElement() + "， 共同购买次数:" + Double.valueOf(item.getScore()).intValue());
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //jedis.quit();
            jedis.close();
        }
    }

    private static void customersShopping(String[] products, int i, Jedis jedis) {
        //简单模拟3种购买行为，随机选取作为用户的购买选择
        int bought = (int) (Math.random() * 3);
        if (bought == 1) {
            //模拟业务逻辑：用户购买了如下产品
            System.out.println("用户" + i + "购买了" + products[0] + "，" + products[2] + "，" + products[1]);
            //将产品之间的关联情况记录到ApsaraDB for Redis的SortSet之中
            jedis.zincrby(products[0], 1, products[1]);
            jedis.zincrby(products[0], 1, products[2]);
            jedis.zincrby(products[1], 1, products[0]);
            jedis.zincrby(products[1], 1, products[2]);
            jedis.zincrby(products[2], 1, products[0]);
            jedis.zincrby(products[2], 1, products[1]);
        } else if (bought == 2) {
            //模拟业务逻辑：用户购买了如下产品
            System.out.println("用户" + i + "购买了" + products[4] + "，" + products[2] + "，" + products[3]);
            //将产品之间的关联情况记录到ApsaraDB for Redis的SortSet之中
            jedis.zincrby(products[4], 1, products[2]);
            jedis.zincrby(products[4], 1, products[3]);
            jedis.zincrby(products[3], 1, products[4]);
            jedis.zincrby(products[3], 1, products[2]);
            jedis.zincrby(products[2], 1, products[4]);
            jedis.zincrby(products[2], 1, products[3]);
        } else if (bought == 0) {
            //模拟业务逻辑：用户购买了如下产品
            System.out.println("用户" + i + "购买了" + products[1] + "," + products[5]);
            //将产品之间的关联情况记录到ApsaraDB for Redis的SortSet之中
            jedis.zincrby(products[5], 1, products[1]);
            jedis.zincrby(products[1], 1, products[5]);
        }
    }
}
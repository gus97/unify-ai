package cn.migu.unify.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
//@RestController
@Component
public class UnifyAiApplication {

    @Autowired
    @Qualifier("r3")
    private RedisTemplate r3;
//
//    @Autowired
//    @Qualifier("m1")
//    private JdbcTemplate m1;
//
//    @Autowired
//    @Qualifier("m2")
//    private JdbcTemplate m2;

//    private Logger downLog = LoggerFactory.getLogger(UnifyAiApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(UnifyAiApplication.class, args);


        ////--remotePic=file:/Users/gus97/pic/remote/ --rootPath=/Users/gus97/pic/local/
    }

    @RequestMapping("/mysql-gus")
    public Object mysqlTest1() {

        return r3.execute((RedisCallback) redisConnection -> {
            redisConnection.select(9);



            return new String(redisConnection.get("boo".getBytes()));
        });

    }
//
//    @RequestMapping("/mysql-test")
//    public Object mysqlTest2() {
//
//        return m2.queryForList("select * from boo");
//
//    }
//
//
//    @RequestMapping("/string")
//    public Object test1() throws InterruptedException {
//
//        return r3.execute((RedisCallback) redisConnection -> {
//            redisConnection.select(9);
//            return new String(redisConnection.get("boo".getBytes()));
//        });
//
//    }
//
//    @RequestMapping("/map")
//    public Map test2() {
//        HashOperations<String, Object, Object> hash = r3.opsForHash();
//        Map<String, Object> map = new HashMap<>();
//        map.put("key1", "value1");
//        map.put("key2", "value2");
//        hash.putAll("key-map", map);
//        return hash.entries("key-map");
//    }
//
//    @RequestMapping("/list")
//    public List test3() {
//        ListOperations<String, Object> list = r3.opsForList();
//        list.rightPush("key-list", "v1");
//        list.rightPush("key-list", "v2");
//        return list.range("key-list", 0, 1);
//    }
//
//    @RequestMapping("/zset")
//    public String test4() {
//        ZSetOperations<String, Object> set = r3.opsForZSet();
//
//        set.add("uid_aid_cid", "/a/b/xx_1.jpg", 10000);
//        set.add("uid_aid_cid", "/a/b/xx_2.jpg", 10000);
//        set.add("uid_aid_cid", "/a/b/xx_3.jpg", 10000);
//
//        return "ok";
//    }


//    @RequestMapping("/s1")
//    public String s1() throws UnknownHostException {
//        List<BasicNameValuePair> res = new ArrayList();
//        res.add(new BasicNameValuePair("k1","{a:b}"));
//        res.add(new BasicNameValuePair("k2","v2"));
//        return  HttpClientUtils.request4HC("http://127.0.0.1:9999/s2", res);
//    }
//
//    @RequestMapping("/s2")
//    public String s2(HttpServletRequest request) {
//
//        System.out.println(request.getParameter("k1"));
//        System.out.println(request.getParameter("k2"));
//        return "s2";
//    }


}

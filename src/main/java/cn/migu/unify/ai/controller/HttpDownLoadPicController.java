//package cn.migu.unify.ai.controller;
//
//import cn.migu.unify.ai.utils.DistributedIDS;
//import cn.migu.unify.ai.utils.http.GetRunnable;
//import cn.migu.unify.ai.utils.http.HttpClientPool;
//import cn.migu.unify.ai.utils.http.StopVo;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.client.methods.HttpGet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//
//@RestController
//public class HttpDownLoadPicController {
//
//    private Logger downLog = LoggerFactory.getLogger("pic-down");
//
//    @Autowired
//    @Qualifier("r3")
//    private RedisTemplate r3;
//
//    @Value("${rootPath}")
//    String localPath;
//
//    @RequestMapping("/startDownPic")
//    public String startDownPic(HttpServletRequest request) {
//
//        //http://127.0.0.1:9999/user101/user101_a_c_1014_3.jpg
//
//        //先验证参数合法性，即地址能否返回200
//        String picUrl = request.getParameter("picUrl");
//
//        String userPic = request.getParameter("userPic");
//
//        if (picUrl == null || userPic == null) {
//
//            return "-101";
//        }
////
////        if (userPic.split("_").length != 4) {
////            return "-102";
////        }
//
//        String secondPath = userPic.split("_")[0];
//
//        String sTime = userPic.split("_")[1];
//
//        if (!StringUtils.isNumeric(sTime)) {
//            return "-103";
//        }
//
//        String key = userPic.split("_")[0];
//
//        //先查询又没有一个已经启动中的任务，强行停止
//        if (r3.hasKey(key + ":running")) {
//            r3.delete(key + ":running");
//            downLog.warn("强行停止了一个可能正在运行的用户任务: " + key);
//        }
//
//        int startTime = Integer.parseInt(userPic.split("_")[1]);
//
//        HttpGet httpget = new HttpGet(picUrl);
//
//        DistributedIDS idWorker = new DistributedIDS(0, 0);
//
//        long id = idWorker.nextId();
//
//        downLog.info("1==>" + id + "---" + picUrl + "---" + userPic + "---" + startTime);
//
//        //设置当前任务为启动状态
//        ExecutorService executors = Executors.newFixedThreadPool(10);
//
//
//        //为当前用户插入一个u_a_c 唯一的key，防止重复启动
//        r3.opsForValue().append(key + ":running", id + "");
//
//
//        new Thread(() -> {
//
//            StopVo sv = new StopVo(id, 0, System.currentTimeMillis());
//
//            int incrementTime = 1;
//
//            while (r3.hasKey(key + ":running")) {
//
//                try {
//                    //当前用户一轮5张图片异步之后，休眠1秒，继续下一轮5张图片
//                    String path = localPath;
//                    for (int i = 1; i <= 5; i++) {
//                        String remotePicPath = picUrl + "/" + userPic.replace(sTime, (startTime + incrementTime + "_")) + i + ".jpg";
//
//                        HttpGet httpGet = new HttpGet(remotePicPath);
//
//                        HttpClientPool.config(httpget);
//
//                        File userFile = new File(localPath + secondPath);
//
//
//                        if (!userFile.exists()) {
//
//                            userFile.mkdirs();
//
//                            path = localPath + secondPath + "/";
//
//                        } else if (userFile.exists() && userFile.isDirectory()) {
//
//                            path = localPath + secondPath + "/";
//
//                        }
//
//                        //启动线程抓取
//                        executors.execute(new GetRunnable(r3, HttpClientPool.httpClient, httpGet,
//                                path + userPic.replace(sTime, (startTime + incrementTime + "_")) + i + ".jpg",
//                                startTime + incrementTime, key, sv));
//                    }
//
//
//                    //System.out.println(incrementTime);
//
//                    incrementTime++;
//
//                    if (incrementTime % 10 == 0 && (System.currentTimeMillis() - sv.getTime() >= 10000) && sv.getNum() == 0) {
//
//                        downLog.warn(userPic + "==========>距最后一次下载,已连续" + (System.currentTimeMillis() - sv.getTime()) / 1000L + "秒没有发现文件");
//
//                        if (System.currentTimeMillis() - sv.getTime() >= 60000) {
//
//                            r3.delete(key + ":running");
//                            downLog.warn(userPic + "==========>距最后一次下载,已连续" + (System.currentTimeMillis() - sv.getTime()) / 1000L + "秒没有发现文件,准备自动停止!!!");
//                        }
//                    }
//
//                    Thread.sleep(1000);
//                    if (!r3.hasKey(key + ":running")) {
//                        downLog.warn(userPic + "========>即将停止");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            executors.shutdown();
//
//            downLog.warn("executors will shutdown!!!");
//
//        }).start();
//
//
//        return "0";
//
//    }
//
//    @RequestMapping("/stopDownPic")
//    public String stopDownPic(HttpServletRequest request) {
//
//        String ids = request.getParameter("IDS".toLowerCase());
//
//        if (ids == null) {
//
//            return "-201";
//        }
//        if (r3.hasKey(ids + ":running")) {
//
//            r3.delete(ids + ":running");
//
//            return "0";
//        } else {
//            return "-202";
//        }
//    }
//
//}

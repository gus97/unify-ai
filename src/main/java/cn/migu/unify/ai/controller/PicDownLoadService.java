package cn.migu.unify.ai.controller;

import cn.migu.unify.ai.utils.http.GetRunnable;
import cn.migu.unify.ai.utils.http.HttpClientPool;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;


@RestController
public class PicDownLoadService {

    private Logger downLog = LoggerFactory.getLogger("pic-down");

    @Autowired
    @Qualifier("r3")
    private RedisTemplate r3;

    @Value("${rootPath}")
    String localPath;

    @Value("${delayTime}")
    String delayTime;

    @Value("${maxStopTime}")
    String maxStopTime;

    @Value("${sleepTime}")
    String sleepTime;

    @RequestMapping("/startDownPic")
    public String startDownPic(HttpServletRequest request) {

        try {
            Thread.sleep(Long.parseLong(delayTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //--remoteSrc=file:C:/gus/data/remote/ --rootPath=C:/gus/data/local/ --maxStopTime=1
        //http://127.0.0.1:9999/startDownPic?txtUrl=http://127.0.0.1:9999/txt/1.txt&channelId=12345
        //redis-cli -n 9 keys "*" | xargs redis-cli -n 9 del
        //先验证参数合法性，即地址能否返回200
        String txtUrl = request.getParameter("txtUrl");

        String channelId = request.getParameter("channelId");

        String picRootPath = request.getParameter("picRootPath");

        downLog.info("txtUrl" + "====================>>" + txtUrl);
        downLog.info("channelId" + "====================>>" + channelId);
        downLog.info("picRootPath" + "====================>>" + picRootPath);

        if (txtUrl == null || channelId == null || "".equals(channelId.replace(" ", "")) || picRootPath == null) {

            return "-101";
        }

//        String picRootPath= "http://snapshot.migucloud.com/testreal//testprd/201711317/5M5RREGK_C0_1510562787805/";
//        String txtUrl= "http://snapshot.migucloud.com/testreal/testprd/201711317/5M5RREGK_C0_1510562787805/im_index";
//        String channelId="20171113164826_5M5RREGK";

        //启动任务
        final int p;

        //标识点 offset
        if (r3.opsForValue().get(channelId + ":running") != null) {
            p = Integer.valueOf(r3.opsForValue().get(channelId + ":running").toString());
            r3.delete(channelId + ":running");

            try {
                downLog.info("由于重复请求或意外宕机引起请求冲突：[" + channelId + "] 等待1秒释放之前的任务");

                System.out.println("======================>>>" + r3.hasKey(channelId + ":running") + "-----" + channelId + ":running");

                Thread.sleep(1000);

                r3.opsForValue().set(channelId + ":running", "0");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            r3.opsForValue().set(channelId + ":running", "0");
            p = 0;
        }


        new Thread(() -> {
            int k = p;

            long t1 = System.currentTimeMillis();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (r3.hasKey(channelId + ":running")) {


                //读取远程全量图片集合
                CloseableHttpResponse response = null;

                try {

                    HttpGet httpGet = new HttpGet(txtUrl);



                    HttpClientPool.config(httpGet);

                    response = HttpClientPool.httpClient.execute(httpGet, HttpClientContext.create());

                    HttpEntity entity = response.getEntity();

                    //字节特别小的注意可能不是图片，丢弃即可
                    if (entity == null) {
                        downLog.warn("远程文本地址错误:" + txtUrl);
                        break;
                    }

                    int i = 0;

                    if (System.currentTimeMillis() - t1 > 1000 * 60 * Integer.parseInt(maxStopTime)) {

                        downLog.info("经过指定时间未发现增量图片,channelId=[" + channelId + "] 即将停止================================>>>>>>");

                        r3.delete(channelId + ":running");

                        downLog.info("channelId=[" + channelId + "] auto crash================================>>>>>>");

                        break;
                    }

                    String responseContent = EntityUtils.toString(entity, "UTF-8");


                    if (responseContent.length() < 1024) {
                        continue;
                    }


                    String[] url = new String(responseContent).split("\n");

                    for (String s : url) {

                        if (!r3.hasKey(channelId + ":running")) {

                            downLog.warn("发现停止任务请求,暂停该线程: " + Thread.currentThread().getName());
                            return;
                        }

                        i++;

                        if (i <= k && k != 0) {

                            continue;
                        }

                        t1 = System.currentTimeMillis();

                        downLog.info(channelId + " 发现新图片：" + picRootPath + "--------" + s + "============================>>");

                        HttpGet httpGetPic = new HttpGet(picRootPath + s);

                        File userFile = new File(localPath + channelId);


                        if (!userFile.exists()) {

                            userFile.mkdirs();

                        }

                        new GetRunnable(r3, HttpClientPool.httpClient, httpGetPic,
                                localPath + channelId + "/" + s.substring(s.lastIndexOf("/") + 1, s.length()),
                                channelId + "-onlineframe").downPic();

                        k++;

                        if (r3.hasKey(channelId + ":running")) {

                            r3.opsForValue().set(channelId + ":running", k + "");
                        } else {
                            if (!r3.hasKey(channelId + ":running")) {

                                downLog.warn("发现停止任务请求,暂停该线程: " + Thread.currentThread().getName());
                                return;
                            }
                        }
                    }

                    Thread.sleep(Long.parseLong(sleepTime));

                    if (!r3.hasKey(channelId + ":running")) {

                        downLog.warn("发现停止任务请求,暂停该线程: " + Thread.currentThread().getName());
                        return;
                    }

                    downLog.info("触发任务 channelId=[" + channelId + "] ==================>>>>>>" + txtUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                    printErrorStack(e, downLog);
                } catch (InterruptedException e) {
                    printErrorStack(e, downLog);
                    e.printStackTrace();
                } finally {
                    try {
                        if (response != null)
                            response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        return "0";
    }

    //http://127.0.0.1:9999/stopDownPic?channelId=12345
    @RequestMapping("/stopDownPic")
    public String stopDownPic(HttpServletRequest request) {

        String channelId = request.getParameter("channelId");

        if (channelId == null) {

            return "-201";
        }
        if (r3.hasKey(channelId + ":running")) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    r3.delete(channelId + ":running");
                }
            }).start();

            return "0";

        } else {

            return "-202";
        }
    }


    public static void printErrorStack(Exception e, Logger downLog) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        e.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        downLog.error(buffer.toString());
    }

}

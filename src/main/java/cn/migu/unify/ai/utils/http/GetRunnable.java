package cn.migu.unify.ai.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * add by guxichang
 */

public class GetRunnable {

    private Logger downLog = LoggerFactory.getLogger("pic-down");

    private final CloseableHttpClient httpClient;
    private final HttpGet httpget;
    private final String localFilePath;
    private final String key;
    private RedisTemplate redisTemplate;

    public GetRunnable(RedisTemplate redisTemplate, CloseableHttpClient httpClient, HttpGet httpget,
                       String localFilePath, String key) {
        this.redisTemplate = redisTemplate;
        this.httpClient = httpClient;
        this.httpget = httpget;
        this.localFilePath = localFilePath;
        this.key = key;

    }

    public void downPic() {

        CloseableHttpResponse response = null;

        InputStream in;

        FileOutputStream fout = null;
        try {

            response = httpClient.execute(httpget, HttpClientContext.create());

            HttpEntity entity = response.getEntity();

            //字节特别小的注意可能不是图片，丢弃即可
            if (entity == null || entity.getContentLength() < 1024) {

                downLog.info("丢失的pic: " + key + "========>" + localFilePath);

                EntityUtils.consume(entity);

                return;

            }

            //不要关闭in，管道复用
            in = entity.getContent();

            File file = new File(localFilePath);

            fout = new FileOutputStream(file);

            int l;

            byte[] tmp = new byte[1024];

            while ((l = in.read(tmp)) != -1) {

                fout.write(tmp, 0, l);

            }

            // 将文件输出到本地
            fout.flush();

            EntityUtils.consume(entity);

            //记录redis
            //ZRANGEBYSCORE uid_aid_cid 10000 10000

            redisTemplate.opsForZSet().add(key, localFilePath,0);

            downLog.info("DownLoadPic: " + key + "========>" + localFilePath);


        } catch (IOException e) {

            e.printStackTrace();

        }  finally {
            try {
                if (response != null)
                    response.close();
                if (fout != null)
                    fout.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
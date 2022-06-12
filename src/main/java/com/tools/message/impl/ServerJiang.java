package com.tools.message.impl;

import com.tools.message.MessageNotify;
import com.tools.pojo.News;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class ServerJiang extends MessageNotify {
    private final String SendKey;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String titleFormat;
    private final String contentFormat;

    @Override
    public void sendMessage(News news) {
        String title = "监控结果通知";
        String desp = String.format("项目：%s_通知\r\n\r\n类型：%s\r\n\r\n时间：%s\r\n\r\n内容：%s \r\n\r\n链接：%s",news.getSite(),news.getType(),simpleDateFormat.format(new Date()),news.getNewsTitle(),news.getNewsUrl());
        if (StringUtils.isNotEmpty(titleFormat)){
            title = super.formatMessage(titleFormat, news);
        }
        if (StringUtils.isNotEmpty(contentFormat)){
            desp = formatMessage(contentFormat, news);
        }
        sendMessage(title,desp);
    }

    @Override
    public void sendMessage(String msg) {
        sendMessage("通知",msg);
    }

    public void sendMessage(String title,String desp) {
        String url = String.format("https://sctapi.ftqq.com/%s.send",SendKey);
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("desp", desp));
        params.add(new BasicNameValuePair("channel", "66|9"));
        post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        try {
            HttpResponse response = client.execute(post);
            String res = EntityUtils.toString(response.getEntity());
            System.out.println(res);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.tools.message;

import com.tools.pojo.News;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 发送消息提醒
 * */
public abstract class MessageNotify {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public abstract void sendMessage(News news);

    public abstract void sendMessage(String msg);

    public String formatMessage(String format,News news){
        return format.replaceAll("[{]url}", news.getNewsUrl())
                .replaceAll("[{]title}",news.getNewsTitle())
                .replaceAll("[{]type}",news.getType())
                .replaceAll("[{]site}",news.getSite())
                .replaceAll("[{]time}",simpleDateFormat.format(new Date()));
    }
}

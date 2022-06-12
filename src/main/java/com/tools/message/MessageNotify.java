package com.tools.message;

import com.tools.pojo.News;
import org.apache.commons.lang3.StringUtils;

/**
 * 发送消息提醒
 * */
public abstract class MessageNotify {
    public abstract void sendMessage(News news);

    public abstract void sendMessage(String msg);

    public String formatMessage(String format,News news){
        return format.replaceAll("[{]url}", news.getNewsUrl())
                .replaceAll("[{]title}",news.getNewsTitle())
                .replaceAll("[{]type}",news.getType())
                .replaceAll("[{]site}",news.getSite());
    }
}

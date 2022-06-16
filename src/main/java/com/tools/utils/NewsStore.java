package com.tools.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.tools.enums.SiteEnum;
import com.tools.message.MessageNotify;
import com.tools.pojo.News;
import java.util.HashMap;
import java.util.List;

/**
 * @author hcp
 */
public class NewsStore {
  static HashMap<String,List<News>> hashMap = new HashMap<>();
  static MessageNotify messageNotify;
  static {
    for (SiteEnum siteEnum : SiteEnum.values()) {
      hashMap.put(siteEnum.getTypeCode(),new FixSizeLinkedList<>(siteEnum.getListSize()));
    }
  }

  public static void setMessageNotify(MessageNotify messageNotify) {
    NewsStore.messageNotify = messageNotify;
  }

  /**
   * 新增新闻,成功返回true，失败返回false
   * */
  private static void addNews(News news,boolean isSend,String type){
    List<News> newsList = hashMap.get(type);
    if (newsList.contains(news)){
      return;
    }
    if (isSend && CollectionUtil.isNotEmpty(newsList)){
      doNotify(news);
    }
    newsList.add(news);
  }

  /**
   * 新增新闻,成功返回true，失败返回false
   * */
  public static void addNewsList(List<News> newsSet,String type){
    if (CollectionUtil.isEmpty(newsSet)){
      System.out.println("无数据");
      return;
    }
    boolean notifyFlag = true;
    List<News> newsList = hashMap.get(type);
    if (CollectionUtil.isEmpty(newsList) || newsSet.size()>10){
      notifyFlag = false;
    }
    boolean finalNotifyFlag = notifyFlag;
    newsSet.forEach(news -> addNews(news, finalNotifyFlag,type));
  }

  private static void doNotify(News news) {
    messageNotify.sendMessage(news);
  }

}

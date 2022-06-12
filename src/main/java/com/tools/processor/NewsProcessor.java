package com.tools.processor;

import cn.hutool.core.collection.CollectionUtil;
import com.tools.download.DownloadFile;
import com.tools.enums.SiteEnum;
import com.tools.pojo.News;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tools.utils.NewsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;


/**
 * @author hcp
 */
public class NewsProcessor implements PageProcessor {
  Logger logger = LoggerFactory.getLogger(DownloadFile.class);


  static Site site = Site.me()
          .addHeader("user-agent","Chrome/102.0.0.0")
          .setCharset("utf-8")
          .setTimeOut(5000);

  @Override
  public void process(Page page) {
    List<News> newsList = new ArrayList<>();
    String url = page.getUrl().toString();
    String siteType = "";
    SiteEnum siteEnum = SiteEnum.parseByUrl(url);
    String siteCode = siteEnum.getSiteCode();
    String typeCode = siteEnum.getTypeCode();
    List<Selectable> nodes = parseList(page,siteEnum);

    if (CollectionUtil.isEmpty(nodes)) {
      logger.error("当前页面无可解析元素，url：{}",page.getUrl());
      logger.error(String.valueOf(page.getHtml()));
      return;
    }
    for (Selectable node : nodes) {
      News news = new News();
      String title = parseTitle(node,siteEnum);
      String href = parseHref(node,siteEnum);
      news.setNewsUrl(href);
      news.setNewsTitle(title);
      news.setType(typeCode);
      news.setSite(siteCode);
      newsList.add(news);
    }
    NewsStore.addNewsList(newsList,typeCode);
  }

  private List<Selectable>  parseList(Page page, SiteEnum siteEnum) {
    if (SiteEnum.BINANCE48.getSiteCode().equals(siteEnum.getSiteCode())){
      return page.getHtml().$(".css-1q4wrpt .css-k5e9j4").nodes();
    }
    if (SiteEnum.HOO.getSiteCode().equals(siteEnum.getSiteCode())){
      return page.getHtml().$(".article-list .article-list-item").nodes();
    }
    if (SiteEnum.MEXC.getSiteCode().equals(siteEnum.getSiteCode())){
      return page.getHtml().$(".article-list .article-list-item").nodes();
    }
    return null;
  }

  private String parseTitle(Selectable node,SiteEnum siteEnum) {
    if (SiteEnum.BINANCE48.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByClass("css-eoufru").text();
    }
    if (SiteEnum.HOO.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").text();
    }
    if (SiteEnum.MEXC.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").text();
    }
    return "";
  }

  private String parseHref(Selectable node,SiteEnum siteEnum) {
    if (SiteEnum.BINANCE48.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").attr("href");
    }
    if (SiteEnum.HOO.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").attr("href");

    }
    if (SiteEnum.MEXC.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").attr("href");

    }
    return "";
  }

  private SiteEnum parseType(String url) {
    if (url.indexOf("48")>-1){
      return SiteEnum.BINANCE48;
    }
    if(url.indexOf("49")>-1){
      return SiteEnum.BINANCE49;
    }

    if(url.indexOf("50")>-1){
      return SiteEnum.BINANCE50;
    }
    logger.error("异常请求，{}",url);
    return null;
  }

  @Override
  public Site getSite() {
    return site;
  }
}

package com.tools.processor;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tools.download.DownloadFile;
import com.tools.enums.SiteEnum;
import com.tools.pojo.News;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tools.utils.NewsStore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class NewsProcessor implements PageProcessor {
  Logger logger = LoggerFactory.getLogger(DownloadFile.class);

  private List<String> codeList;


  static Site site = Site.me()
          .addHeader("user-agent","Chrome/102.0.0.0")
          .setCharset("utf-8")
          .setTimeOut(5000);

  @Override
  public void process(Page page) {
    List<News> newsList = new ArrayList<>();
    String url = page.getUrl().toString();
    SiteEnum siteEnum = SiteEnum.parseByUrl(url);
    String siteCode = siteEnum.getSiteCode();
    String typeCode = siteEnum.getTypeCode();
    List<Selectable> nodes = parseList(page,siteEnum);
    if (siteCode.equals("binance")){
      List<News> news = parseBinance(page);
      NewsStore.addNewsList(news,typeCode);
      return;
    }
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

  private List<News> parseBinance(Page page) {
    List<News> newsList = new ArrayList<>();
    JSONArray jsonArray = JSONObject.parseObject(page.getHtml().getDocument().getElementById("__APP_DATA").html()).getJSONObject("routeProps").getJSONObject("b723").getJSONArray("catalogs");
    for (int i = 0; i < jsonArray.size(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String catalogId = jsonObject.getString("catalogId");
      if (! getCodeList().contains(catalogId)){
        continue;
      }
      String catalogName = jsonObject.getString("catalogName");
      JSONArray articles = jsonObject.getJSONArray("articles");
      for (int i1 = 0; i1 < articles.size(); i1++) {
        JSONObject data = articles.getJSONObject(i1);
        String id = data.getString("id");
        String code = data.getString("code");
        String title = data.getString("title");
        String type = data.getString("type");
        String releaseDate = data.getString("releaseDate");

        News news = new News();
        String url = String.format("https://www.binance.com/zh-CN/support/announcement/%s", code);
        news.setNewsUrl(url);
        news.setNewsTitle(title);
        news.setType(catalogName);
        news.setSite(SiteEnum.BINANCE.getSiteName());
        newsList.add(news);
      }
    }
    return newsList;
  }

  private List<Selectable>  parseList(Page page, SiteEnum siteEnum) {
    if (SiteEnum.HOO.getSiteCode().equals(siteEnum.getSiteCode())){
      return page.getHtml().$(".article-list .article-list-item").nodes();
    }
    if (SiteEnum.MEXC.getSiteCode().equals(siteEnum.getSiteCode())){
      return page.getHtml().$(".article-list .article-list-item").nodes();
    }
    return null;
  }

  private String parseTitle(Selectable node,SiteEnum siteEnum) {
    if (SiteEnum.HOO.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").text();
    }
    if (SiteEnum.MEXC.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").text();
    }
    return "";
  }

  private String parseHref(Selectable node,SiteEnum siteEnum) {
    if (SiteEnum.HOO.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").attr("href");

    }
    if (SiteEnum.MEXC.getSiteCode().equals(siteEnum.getSiteCode())){
      return new Html(node.get()).getDocument().getElementsByTag("a").attr("href");

    }
    return "";
  }

  @Override
  public Site getSite() {
    return site;
  }

  private List<String> getCodeList(){
    if(CollectionUtil.isEmpty(codeList)){
      codeList = new ArrayList<>();
      codeList.add("48");
      codeList.add("49");
      codeList.add("50");
    }
    return codeList;
  }
}

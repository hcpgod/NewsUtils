package com.tools;

import cn.hutool.core.collection.CollectionUtil;
import com.tools.download.DownloadFile;
import com.tools.enums.SiteEnum;
import com.tools.message.MessageNotify;
import com.tools.message.impl.ServerJiang;
import com.tools.pojo.News;
import com.tools.processor.NewsProcessor;
import com.tools.utils.UserAgentUtil;
import com.tools.utils.NewsStore;
import com.tools.utils.Spider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 拉取交易所公告
 * 入口运行
 *
 * @author hcp
 * @Auther:
 * @Description:
 */
public class Run {
    public static Spider spider;
    public static int Intervals = 10;
    public static Logger logger;
    public static Properties properties;

    static List<String> siteList = new ArrayList<>();
    static {
        siteList.add("https://support.mexc.com/hc/en-001/sections/360000547811-New-Listings");
        siteList.add("https://help.hoorhi.shop/hc/zh-cn/sections/6541592498201-%E6%96%B0%E5%B8%81%E4%B8%8A%E7%BA%BF");
        siteList.add("https://www.binance.com/zh-CN/support/announcement/c-48?navId=48");
    }

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(Run.class);
        // 读取配置文件
        readConfig();
        // 读取浏览器头
        UserAgentUtil.init();
        // 初始化配置
        initConfig();
        // 开启页面抓取任务
        doTask();
        // 发放任务任务
        ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2);
        scheduled.scheduleAtFixedRate(() -> addUrlSchedule(), 0, Intervals, TimeUnit.SECONDS);
    }

    private static void addUrlSchedule() {
        if (spider != null && CollectionUtil.isNotEmpty(siteList)){
            siteList.forEach(Run::addRequest);
        }
    }

    private static void initConfig() {
        // 配置间隔时间
        Logger logger = LoggerFactory.getLogger(Run.class);
        String interval = properties.getProperty("interval");
        if (StringUtils.isNotEmpty(interval)){
            Intervals = Integer.valueOf(interval);
        }
        logger.warn("拉取页面时间间隔为{}秒",Intervals);
        //
        String titleFormat = properties.getProperty("TitleFormat");
        String contentFormat = properties.getProperty("ContentFormat");
        String sendKey = properties.getProperty("SendKey");
        String isSendTestMsg = properties.getProperty("IsSendTestMsg");
        String channel = properties.getProperty("channel");
        MessageNotify messageNotify = new ServerJiang(sendKey, titleFormat, contentFormat,channel);
        NewsStore.setMessageNotify(messageNotify);
        if (StringUtils.isNotEmpty(isSendTestMsg)){
            logger.warn("已启用初始化测试消息，开始发送测试信息");
            testMessage(messageNotify);
        }
        // 推送消息模板配置
        messageNotify.sendMessage("server酱已连接成功，有最新公告会及时发送！");
    }

    private static void readConfig() {
        String path = new Run().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] pathSplit = path.split("/");
        String jarName = pathSplit[pathSplit.length - 1];
        String jarPath = path.replace(jarName, "");
        String pathName=jarPath+"config.properties";
        Properties properties = new Properties();
        File file = new File(pathName);
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        try {
            fis = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fis, "utf-8");
            properties.load(inputStreamReader);
            Run.properties = properties;
        } catch (Exception e) {
            logger.error("配置文件错误，请检查config.properties文件");
        }finally {
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //获取配置文件数据
    }

    private static void testMessage(MessageNotify messageNotify) {
        News news = new News();
        news.setNewsTitle("幣安將上線AVAXBUSD 1-20倍永續合約(这是一条测试通知)");
        news.setNewsUrl("https://www.binance.com/zh-CN/support/announcement/c6f4b9ca32374bd488af6fc050205bca");
        news.setSite(SiteEnum.BINANCE.getSiteName());
        news.setType(SiteEnum.BINANCE.getTypename());
        messageNotify.sendMessage(news);
    }

    private static void doTask() {
        // 解析器
        spider = Spider.create(new NewsProcessor());
        spider.setDownloader(new DownloadFile());
        spider.setEmptySleepTime(1000);
        spider.startUrls(siteList).thread(6).start();
    }

    public static void addRequest(String url){
        Request request = new Request(url).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 0);
        spider.addRequest(request);
    }
}

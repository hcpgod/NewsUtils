package com.tools;

import com.tools.download.DownloadFile;
import com.tools.enums.SiteEnum;
import com.tools.message.MessageNotify;
import com.tools.message.impl.ServerJiang;
import com.tools.pojo.News;
import com.tools.processor.NewsProcessor;
import com.tools.utils.NewsStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.List;
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

    public static int Intervals = 5;

    public static void main(String[] args) {
        // 初始化配置
        initConfig(args);
        // 开启页面抓取任务
        ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2);
        scheduled.scheduleAtFixedRate(() -> doTask(), 0, Intervals, TimeUnit.SECONDS);
    }

    private static void initConfig(String[] args) {
        // 配置间隔时间
        Logger logger = LoggerFactory.getLogger(Run.class);
        String interval = System.getenv("Interval");
        if (StringUtils.isNotEmpty(interval)){
            Intervals = Integer.valueOf(interval);
        }
        logger.warn("拉取页面时间间隔为{}秒",Intervals);

        String titleFormat = System.getenv("TitleFormat");
        String contentFormat = System.getenv("ContentFormat");
        String sendKey = System.getenv("SendKey");
        String isSendTestMsg = System.getenv("IsSendTestMsg");
        MessageNotify messageNotify = new ServerJiang("SCT91306TSGyBzLMwtrVezeYmy6Y9kmAu", titleFormat, contentFormat);
        NewsStore.setMessageNotify(messageNotify);
        if (StringUtils.isNotEmpty(isSendTestMsg)){
            logger.warn("已启用初始化测试消息，开始发送测试信息");
            testMessage(messageNotify);
        }
        // 推送消息模板配置
        messageNotify.sendMessage("server酱已连接成功，有最新公告会及时发送！");
    }

    private static void testMessage(MessageNotify messageNotify) {
        News news = new News();
        news.setNewsTitle("幣安將上線AVAXBUSD 1-20倍永續合約(这是一条测试通知)");
        news.setNewsUrl("https://www.binance.com/zh-CN/support/announcement/c6f4b9ca32374bd488af6fc050205bca");
        news.setSite(SiteEnum.BINANCE48.getSiteName());
        news.setType(SiteEnum.BINANCE48.getTypename());
        messageNotify.sendMessage(news);
    }

    private static void doTask() {
        List<String> siteList = new ArrayList<>();
        siteList.add("https://support.mexc.com/hc/en-001/sections/360000547811-New-Listings");
        siteList.add("https://help.hoorhi.shop/hc/zh-cn/sections/6541592498201-%E6%96%B0%E5%B8%81%E4%B8%8A%E7%BA%BF");
        siteList.add("https://www.binance.com/zh-CN/support/announcement/c-48?navId=48");
        siteList.add("https://www.binance.com/zh-CN/support/announcement/c-49?navId=49");
        siteList.add("https://www.binance.com/zh-CN/support/announcement/c-50?navId=50");
        // 解析器
        Spider spider = Spider.create(new NewsProcessor());
        spider.setDownloader(new DownloadFile());
        spider.startUrls(siteList).start();
    }

}

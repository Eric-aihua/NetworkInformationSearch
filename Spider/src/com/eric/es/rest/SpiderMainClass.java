package com.eric.es.rest;

import com.eric.es.rest.com.eric.webmagic.spider.BaiduBaikePageProcessor;
import com.eric.es.rest.com.eric.webmagic.spider.StdOutputPipeline;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * 通过webmagic框架从百度百科上抓取信息
 * Created by Eric on 2016/1/10.
 */
public class SpiderMainClass {
    public static void main(String args[]) {
        BloomFilterDuplicateRemover bloomFilterDuplicateRemover = new BloomFilterDuplicateRemover(9999);
        Spider.create(new BaiduBaikePageProcessor()).addUrl("http://http://baike.baidu.com/view/908354.htm").
                addUrl("http://baike.baidu.com/view/284853.htm").
                setScheduler(new QueueScheduler().setDuplicateRemover(bloomFilterDuplicateRemover)).
                addPipeline(new StdOutputPipeline()).
                thread(5).start();
        System.out.println("爬虫退出");

    }
}

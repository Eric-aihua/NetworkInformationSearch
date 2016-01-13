package com.eric.es.rest.com.eric.webmagic.spider;

import com.eric.es.rest.utils.ClientUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Eric on 2016/1/10.
 */
public class BaiduBaikePageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUseGzip(true);
    private DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    private static Client client = ClientUtils.getClient("elasticsearch", "localhost", 9300);
    private static final int SIZE_PER_PAGE = 1000;
    private static final String INDEX_NAME = "baike";


    @Override
    public void process(Page page) {
        XContentBuilder xContentBuilder = null;
        page.addTargetRequests(page.getHtml().links().regex("(http://baike\\.baidu\\.com/view/\\d+\\.htm)").all());
        page.addTargetRequests(page.getHtml().links().regex("(http://baike\\.baidu\\.com/subview/\\d+/\\d+\\.htm)").all());
        page.putField("title", page.getHtml().xpath("//h1//allText()").toString());

        if (page.getResultItems().get("title") == null) {
            page.setSkip(true);
        }
        String lastModifyTime = null;
        try {

            lastModifyTime = page.getHtml().xpath("//span[@ class='j-modified-time']/text()").toString();
            List<String> tagList = page.getHtml().xpath("//span[@ class='taglist']/text()").all();
            Date date = dateformat.parse(lastModifyTime);
            page.putField("lastModifyTime", date);
            page.putField("content", StringUtils.join(page.getHtml().xpath("//div[@ class='main-content']//div[@class='para']/allText()").all(), "<br>"));
            xContentBuilder = jsonBuilder().startObject();
            if (tagList.size() > 0) {
                page.putField("taglist", tagList);
                xContentBuilder = xContentBuilder.field("taglist", page.getResultItems().get("taglist"));
            }
            xContentBuilder = xContentBuilder.field("title", page.getResultItems().get("title"));
            xContentBuilder = xContentBuilder.field("url", page.getUrl().get());
            xContentBuilder = xContentBuilder.field("content", page.getResultItems().get("content"));
            xContentBuilder = xContentBuilder.field("lastModifyTime", page.getResultItems().get("lastModifyTime"));
            String source = xContentBuilder.endObject().string();
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(INDEX_NAME, INDEX_NAME).setSource(source);
            indexRequestBuilder.execute().get();
            System.out.println(page.getResultItems().get("title") + " Index Finish At " + new Date());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            if (lastModifyTime.equals("今天")) {
                page.putField("lastModifyTime",new Date());
                try {
                    xContentBuilder = xContentBuilder.field("lastModifyTime", page.getResultItems().get("lastModifyTime"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }else{
                System.out.println("无法识别的日期类型");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}

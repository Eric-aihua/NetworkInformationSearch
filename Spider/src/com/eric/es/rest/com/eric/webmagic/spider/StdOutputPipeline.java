package com.eric.es.rest.com.eric.webmagic.spider;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by Eric on 2016/1/10.
 */
public class StdOutputPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println(resultItems);
    }
}

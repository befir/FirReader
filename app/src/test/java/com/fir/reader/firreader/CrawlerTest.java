package com.fir.reader.firreader;

import com.fir.reader.firreader.crawlerHelper.LeiGuangMovieHelper;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CrawlerTest {

    @Test
    public void getVideo() {
    }
    @Test
    public void testUrl() throws IOException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque());

        System.in.read();
    }
    @Test
    public void test(){
        new LeiGuangMovieHelper().getChapterByUrl("http://www.leiguang99.com/dianying/jingqiduichang/");
    }
}
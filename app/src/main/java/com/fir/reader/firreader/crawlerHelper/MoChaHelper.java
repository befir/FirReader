package com.fir.reader.firreader.crawlerHelper;

import com.fir.reader.firreader.common.Crawler;
import com.fir.reader.firreader.common.DBHelper;
import com.fir.reader.firreader.dto.VideoDto;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;

public class MoChaHelper {
    private int step = 20000;
    public void initVideoChapter(VideoDto videoDto, long startId,DBHelper dbHelper){
        startId = 231999;//startId>0?startId:199047;
        while(true){
            if(Crawler.getThreadPoolExecutor().getTaskCount()>step){
                try {
                    Thread.sleep(1000L*60*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                initVideo(videoDto,startId,dbHelper);
                startId = startId + step;
            }
        }
    }
    private void initVideo(final VideoDto videoDto, long startId,final DBHelper dbHelper){
        for (long i = startId; i < startId+step; i++) {
            final String url = "https://yzgweb.com/" + i + "/index.m3u8";
            final VideoDto.ChapterDto chapterDto = videoDto.new ChapterDto();
            chapterDto.setId(i);
            chapterDto.setVideoId(videoDto.getId());
            Crawler.execute(new Runnable() {
                @Override
                public void run() {
                    String realUrl = getRealM3U8Resource(url);
                    System.out.println(url);
                    if(testTsIsEffective(getFirstTsUrl(url))){
                        System.out.println("realUrl:"+realUrl);
                        chapterDto.setChapterUrl(realUrl);
                        Crawler.insertVideoChapter(chapterDto,dbHelper);
                    }
                }
            });
        }
    }
    private String getRealM3U8Resource(String m3u8Url){
        Connection.Response response = testUrl(m3u8Url);
        if(response!=null&&response.statusCode()==200 && !StringUtil.isBlank(response.body())){
            String[] content = response.body().split("\n");
            for(String s:content){
                if(!s.startsWith("#")){
                    return m3u8Url.substring(0,m3u8Url.lastIndexOf("/")+1)+s;
                }
            }
        }
        return null;
    }
    private String getFirstTsUrl(String m3u8Url){
        Connection.Response response = testUrl(m3u8Url);
        if(response!=null&&response.statusCode()==200 && !StringUtil.isBlank(response.body())){
            String[] content = response.body().split("\n");
            for(String s:content){
                if(!s.startsWith("#")){
                    return m3u8Url.substring(0,m3u8Url.lastIndexOf("/")+1)+s;
                }
            }
        }
        return null;
    }
    private boolean testTsIsEffective(String tsUrl){
        Connection.Response response = testUrl(tsUrl);
        if(response!=null&&response.statusCode()==200){
            return true;
        }else{
            return false;
        }
    }
    private Connection.Response testUrl(String url){
        Connection.Response response = null;
        try {
            response = Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36"
                                    + " (KHTML, like Gecko) Chrome/42.0.2311.22 Safari/537.36")
                    .timeout(60 * 1000).execute();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return response;
    }
}

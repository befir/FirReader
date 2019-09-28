package com.fir.reader.firreader.crawlerHelper;

import com.fir.reader.firreader.common.Crawler;
import com.fir.reader.firreader.common.DBHelper;
import com.fir.reader.firreader.dto.VideoDto;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LeiGuangTVHelper {
    public void initVideoChapter(final VideoDto videoDto,final DBHelper dbHelper){
        String url = "http://www.leiguang99.com/dianshiju/";
        int pageCount = getPageCount(url);
        int i = 1;
        do{
            List<String> videoDtoList = getVideoList(url);
            for(final String videoUrl:videoDtoList){
                if(Crawler.getThreadPoolExecutor().getTaskCount()>1000){
                    try {
                        Thread.sleep(1000L*60*5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    Crawler.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<String> chapterUrlList = getChapterUrl(videoUrl);
                            for(String chapterUrl:chapterUrlList){
                                final VideoDto.ChapterDto chapterDto = videoDto.new ChapterDto();
                                chapterDto.setVideoId(videoDto.getId());
                                chapterDto.setChapterUrl(chapterUrl);
                                Crawler.insertVideoChapter(chapterDto,dbHelper);
                            }
                        }
                    });
                }
            }
            i++;
            url = "http://www.leiguang99.com/dianshiju/index"+i+".html";
        }while (i<=pageCount);
    }
    public List<String> getChapterUrl(String url){
        Element element = Crawler.getDocumentByGetMethod(url);
        Element videoElement = Crawler.getDocumentByGetMethod("http://www.leiguang99.com"+element.getElementById("2").getElementsByTag("a").first().attr("href"));
        Element script = videoElement.getElementsByClass("player").first().getElementsByTag("script").first();
        String scriptData = script.data();
        List<String> chapterUrlList = new ArrayList<>();
        scriptData = scriptData.substring(scriptData.indexOf("\"")+1,scriptData.lastIndexOf("\""));
        String[] list = scriptData.split("\\$\\$\\$");
        for(String s:list){
            if(s.startsWith("kkm3u8")){
                String[] chapterUrls = s.split("\\$\\$")[1].split("#");
                for(String chapterUrl:chapterUrls){
                    chapterUrlList.add(chapterUrl.split("\\$")[1]);
                }
            }
        }
        return chapterUrlList;
    }

    private int getPageCount(String url){
        Element element = Crawler.getDocumentByGetMethod(url);
        int pageCount = Integer.valueOf(element.getElementsByClass("page mb clearfix").first().getElementsByTag("a").last().text().substring(2));
        return pageCount;
    }
    private static List<String> getVideoList(String url){
        List<String> videoDtoList = new ArrayList<>();
        Element element = Crawler.getDocumentByGetMethod(url);
        Elements elements = element.getElementsByClass("index-tj mb clearfix").first().getElementsByTag("a");
        for(Element el:elements){
            if(el.hasAttr("href")&&el.hasAttr("title")){
                videoDtoList.add(el.attr("href"));
            }
        }
        return videoDtoList;
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

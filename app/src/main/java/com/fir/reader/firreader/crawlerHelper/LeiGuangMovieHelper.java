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

public class LeiGuangMovieHelper {
    private String baseUrl = "http://www.leiguang99.com";
    public void initVideoChapter(final VideoDto videoDto,final DBHelper dbHelper){
        String url = baseUrl+"/dianying/";
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
                            System.out.println(videoUrl);
                            VideoDto.ChapterDto chapterDto = getChapterByUrl(videoUrl);
                            chapterDto.setVideoId(videoDto.getId());
                            Crawler.insertVideoChapter(chapterDto,dbHelper);
                        }
                    });
                }
            }
            i++;
            url = baseUrl+"/dianying/index"+i+".html";
        }while (i<=pageCount);
    }
    public VideoDto.ChapterDto getChapterByUrl(String url){
        Element element = Crawler.getDocumentByGetMethod(url);
        Element videoElement = Crawler.getDocumentByGetMethod(baseUrl+element.getElementById("2").getElementsByTag("a").first().attr("href"));
        String title = videoElement.getElementsByClass("h2").first().getElementsByTag("a").last().text();
        Element script = videoElement.getElementsByClass("player").first().getElementsByTag("script").first();
        String scriptData = script.data();
        String chapterUrl = null;
        scriptData = scriptData.substring(scriptData.indexOf("\"")+1,scriptData.lastIndexOf("\""));
        String[] list = scriptData.split("\\$\\$\\$");
        for(String s:list){
            if(s.startsWith("kkm3u8")){
                chapterUrl = s.split("\\$\\$")[1].split("#")[0].split("\\$")[1];
            }
        }
        VideoDto.ChapterDto chapterDto =new VideoDto().new ChapterDto();
        chapterDto.setTitle(title);
        chapterDto.setChapterUrl(chapterUrl);
        return chapterDto;
    }

    private int getPageCount(String url){
        Element element = Crawler.getDocumentByGetMethod(url);
        int pageCount = Integer.valueOf(element.getElementsByClass("page").first().getElementsByTag("a").last().text().substring(2));
        return pageCount;
    }
    private List<String> getVideoList(String url){
        List<String> videoDtoList = new ArrayList<>();
        Element element = Crawler.getDocumentByGetMethod(url);
        if(element!=null){
            Elements elements = element.getElementsByClass("index-tj").first().getElementsByTag("a");
            for(Element el:elements){
                if(el.hasAttr("href")&&el.hasAttr("title")&&el.hasClass("li-hv")){
                    videoDtoList.add(baseUrl+el.attr("href"));
                }
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

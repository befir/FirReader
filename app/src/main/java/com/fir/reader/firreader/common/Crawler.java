package com.fir.reader.firreader.common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fir.reader.firreader.crawlerHelper.LeiGuangMovieHelper;
import com.fir.reader.firreader.crawlerHelper.MoChaHelper;
import com.fir.reader.firreader.dto.BookDto;
import com.fir.reader.firreader.dto.VideoDto;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private static ThreadPoolExecutor threadPoolExecutor;
    public static BookDto.ChapterDto getChapterContent(BookDto.ChapterDto chapterDto){
        Document document = getDocumentByGetMethod(chapterDto.getChapterUrl());
        chapterDto.setContent(document.getElementById("content").text());
        return chapterDto;
    }

    public static void initVideo(DBHelper dbHelper, VideoDto videoDto){
        long startId = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor videoResult = db.rawQuery("select max(id) from video_chapter where category_id = ?", new String[]{videoDto.getId()+""});
        if(videoResult.moveToFirst()){
            startId = videoResult.getLong(0)+1;
            videoResult.close();
        }
        db.close();
        if("抹茶影视".equals(videoDto.getVideoName())){
            new MoChaHelper().initVideoChapter(videoDto,startId, dbHelper);
        }else if("雷光电影".equals(videoDto.getVideoName())){
            new LeiGuangMovieHelper().initVideoChapter(videoDto, dbHelper);
        }
    }
    public static void insertVideoChapter(VideoDto.ChapterDto chapterDto,DBHelper dbHelper){
        ContentValues video= new ContentValues();
        video.put("id", chapterDto.getId());
        video.put("name",chapterDto.getTitle());
        video.put("url",chapterDto.getChapterUrl());
        video.put("contentAbstract", chapterDto.getContent());
        video.put("category_id", chapterDto.getVideoId());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("video_chapter",null,video);
        db.close();
    }
    public static BookDto getBook(BookDto bookDto){
        Document document = getDocumentByGetMethod(bookDto.getBookUrl());
        Elements elements = document.getElementById("list").getElementsByTag("a");
        List<BookDto.ChapterDto> chapterDtoList = new ArrayList<>();
        int i=1;
        for(Element elemet:elements){
            BookDto.ChapterDto chapterDto = bookDto.new ChapterDto();
            String chapterUrl = elemet.attr("href");
            chapterDto.setChapterUrl(chapterUrl.startsWith("http")?chapterUrl:bookDto.getBookUrl()+chapterUrl);
            chapterDto.setTitle(elemet.attr("title"));
            chapterDto.setPageNo(i++);
            chapterDtoList.add(chapterDto);
        }
        bookDto.setChapterList(chapterDtoList);
        return bookDto;
    }


    public static Document getDocumentByGetMethod(String url){
        Document document = null;
        try {
            document = Jsoup
                    .connect(url)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36"
                                    + " (KHTML, like Gecko) Chrome/42.0.2311.22 Safari/537.36")
                    .timeout(60 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
    public static void execute(Runnable task){
        try{
            getThreadPoolExecutor().execute(task);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    public static void shutdown(){
        System.out.println("Crawler.shutdown");
        if(threadPoolExecutor!=null&&!threadPoolExecutor.isShutdown()){
            threadPoolExecutor.shutdownNow();
        }
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        synchronized(Crawler.class){
            if(threadPoolExecutor==null||threadPoolExecutor.isShutdown()){
                threadPoolExecutor = new ThreadPoolExecutor(0, 2, 20, TimeUnit.MINUTES,
                        new ArrayBlockingQueue<Runnable>(20000));
            }
        }
        return threadPoolExecutor;
    }
}

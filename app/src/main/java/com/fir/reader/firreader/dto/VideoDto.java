package com.fir.reader.firreader.dto;

import java.io.Serializable;
import java.util.List;

public class VideoDto implements Serializable {
    private Long id;
    private String videoName;
    private String author;
    private String contentAbstract;
    private String videoUrl;
    private List<ChapterDto> chapterList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContentAbstract() {
        return contentAbstract;
    }

    public void setContentAbstract(String contentAbstract) {
        this.contentAbstract = contentAbstract;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<ChapterDto> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<ChapterDto> chapterList) {
        this.chapterList = chapterList;
    }

    public class ChapterDto implements Serializable{
        private Long id;
        private Long videoId;
        private int pageNo;
        private String title;
        private String chapterUrl;
        private String content;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getVideoId() {
            return videoId;
        }

        public void setVideoId(Long videoId) {
            this.videoId = videoId;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getChapterUrl() {
            return chapterUrl;
        }

        public void setChapterUrl(String chapterUrl) {
            this.chapterUrl = chapterUrl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

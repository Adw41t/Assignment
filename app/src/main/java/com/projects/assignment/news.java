package com.projects.assignment;

public class news {

    String title;
    String des;
    String url;
    String urlToImg;
    String publishedAt;

    public news(){

    }

    public news(String t,String d,String u,String img,String p){
        title=t;
        des=d;
        url=u;
        urlToImg=img;
        publishedAt=p;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImg() {
        return urlToImg;
    }

    public void setUrlToImg(String urlToImg) {
        this.urlToImg = urlToImg;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}

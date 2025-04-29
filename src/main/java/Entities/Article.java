package Entities;

import java.time.LocalDate;

public class Article {
    private int id;
    private int categoryId;
    private int userId = 1; // Static user id
    private String title;
    private String content;
    private String mediaPath;
    private LocalDate publishedAt;

    public Article() {}


    public Article(int id, int categoryId, int userId, String title, String content, String mediaPath, LocalDate publishedAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mediaPath = mediaPath;
        this.publishedAt = publishedAt;
    }

    public Article( int categoryId,int userId, String title, String content, String mediaPath, LocalDate publishedAt) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mediaPath = mediaPath;
        this.publishedAt = publishedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public LocalDate getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDate publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", mediaPath='" + mediaPath + '\'' +
                ", publishedAt=" + publishedAt +
                '}';
    }
}

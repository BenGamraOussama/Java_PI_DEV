package pii.entities;

import java.time.Instant;

public class Article {

    private Integer id;
    private String title;
    private String content;
    private String mediaPath;
    private Instant publishedAt;
    private Category category;
    private User user;

    // All-args constructor
    public Article(Integer id, String title, String content, String mediaPath, Instant publishedAt, Category category, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.mediaPath = mediaPath;
        this.publishedAt = publishedAt;
        this.category = category;
        this.user = user;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package com.schoolworkhub;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;
    private String subject;
    private Integer price;
    private String status;
    private Long authorId;
    private String authorName;
    private String authorCard;
    private Long executorId;
    private String executorName;
    private String executorCard;
    private Integer executorRating;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorCard() { return authorCard; }
    public void setAuthorCard(String authorCard) { this.authorCard = authorCard; }
    public Long getExecutorId() { return executorId; }
    public void setExecutorId(Long executorId) { this.executorId = executorId; }
    public String getExecutorName() { return executorName; }
    public void setExecutorName(String executorName) { this.executorName = executorName; }
    public String getExecutorCard() { return executorCard; }
    public void setExecutorCard(String executorCard) { this.executorCard = executorCard; }
    public Integer getExecutorRating() { return executorRating; }
    public void setExecutorRating(Integer executorRating) { this.executorRating = executorRating; }
}

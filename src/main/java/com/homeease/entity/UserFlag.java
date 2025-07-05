package com.homeease.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

   
    private String comment;

    
    private LocalDateTime flaggedAt;

    public UserFlag() {}

    public UserFlag(User user, String comment) {
        this.user = user;
        this.comment = comment;
        this.flaggedAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getFlaggedAt() {
        return flaggedAt;
    }

    public void setFlaggedAt(LocalDateTime flaggedAt) {
        this.flaggedAt = flaggedAt;
    }
}

package com.pictalk.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    private String url;
    private LocalDateTime createdAt;
}

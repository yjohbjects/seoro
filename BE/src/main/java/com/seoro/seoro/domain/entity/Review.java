package com.seoro.seoro.domain.entity;

import jakarta.persistence.Entity;

import java.io.Serializable;

@Entity
public class Review implements Serializable {
    private Long reviewUserId;
    private String isbn;
    private String reviewContent;
}

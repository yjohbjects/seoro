package com.seoro.seoro.domain.entity.Place;

import jakarta.persistence.Entity;

import java.io.Serializable;

@Entity
public class PlacePhoto implements Serializable {
    private Long placeId;
    private String photo;
}
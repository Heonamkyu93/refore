package com.refore.our.menu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bugger")
public class MenuEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buggerId;
    private String buggerName;
    private String buggerPrice;
    private String buggerInfo;
}

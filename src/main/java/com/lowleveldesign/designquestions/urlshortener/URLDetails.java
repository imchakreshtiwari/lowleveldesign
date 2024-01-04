package com.lowleveldesign.designquestions.urlshortener;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "urldetails")
@Data
public class URLDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String longURL;

    private String shortURL;

    private Long userId;

    private Long createdAt;

    //If difference between now and created AT is 10 then delete the records
    @Version
    private Long version; // Version field for optimistic locking, automatically manages by JPA
}

package com.lowleveldesign.designquestions.urlshortener;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface URLDetailsRepo extends JpaRepository<URLDetails, Long> {

    public URLDetails findByShortURL(String shortURL);

    public URLDetails findByLongURL(String longURL);

//    @Modifying
//    @Transactional
//    @Query(value = "INSERT INTO short_urls (tiny_url, long_url) VALUES (:tinyUrl, :longUrl) " +
//            "ON CONFLICT (tiny_url) DO NOTHING", nativeQuery = true)
//    boolean insertIfNotExists(@Param("tinyUrl") String tinyUrl, @Param("longUrl") String longUrl);
}

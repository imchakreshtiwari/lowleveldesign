package com.lowleveldesign.designquestions.urlshortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
public class URLDetailsController {

    @Autowired
    private URLShortener urlShortener;

    @PostMapping("/convert")
    public ResponseEntity<String> convertURL(@RequestBody RequestDto requestDto) {

        String shortURL = urlShortener.convertToShortURL(requestDto.getLongUrl());
        return ResponseEntity.ok(shortURL);
    }

    @GetMapping("/get/{shortURL}")
    public ResponseEntity<URLDetails> getURL(@PathVariable("shortURL") String shortURL) {

        //We are using shortURL column for getting record from DB so we can create index on top of that.
        URLDetails urlDetails = urlShortener.extractRecordFromDB(shortURL);
        return ResponseEntity.ok(urlDetails);
    }
}

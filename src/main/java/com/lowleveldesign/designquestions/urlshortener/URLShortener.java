package com.lowleveldesign.designquestions.urlshortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class URLShortener {

    @Autowired
    private URLDetailsRepo urlDetailsRepo;

    AtomicLong counter = new AtomicLong(10000000);

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


//    public static void main(String[] args) {
//        //Convert a long URL into short URL using base62 encoding
//
//        String longURL = "https://techtutort.com/index.php/2023/12/31/java-tutorial/";
//
//        System.out.println(convertToShortURL(longURL));
//        System.out.println(encode(100));
//    }

    //Here randome will give us a number between 0 - 62 everytime to get unique char
    @Transactional
    public String convertToShortURL(String longURL) {

        //To avoid duplicate long urls
        URLDetails urlDetailsFromDBByLongURL = urlDetailsRepo.findByLongURL(longURL);
        if (urlDetailsFromDBByLongURL != null) {
            return urlDetailsFromDBByLongURL.getShortURL();
        }
        //1. Generate short URl
        String shortUrl =  getShortUrl();
        URLDetails urlDetailsFromDB = urlDetailsRepo.findByShortURL(shortUrl);

        if (urlDetailsFromDB != null) {
            shortUrl =  getShortUrl();
            //if still short url is duplicate we can use timestamp to make it unique
        }
        //2. If short url present in DB then generate again
        //this scenario will work with one server, but if there are multiple servers then race condition will occur
        // then it will insert duplicate shrort urls from different different servers at the same time

        //to avoid this we can insert the data in to db using INsert ignore condition SQL will support this


        //3 Store the long URL and short URL ID in a database
        URLDetails urlDetails = new URLDetails();
        urlDetails.setLongURL(longURL);
        urlDetails.setShortURL(shortUrl);
        urlDetails.setCreatedAt(new Date().getTime());

        //In real application it will come from user table
        urlDetails.setUserId(1L);

        try {
            urlDetailsRepo.save(urlDetails);
        } catch (OptimisticLockException e) {
            e.printStackTrace();
        }

        return shortUrl;
    }

    /**
     *  This method takes the long URL and convert it to short url using Base62 approach
     *  The only drawback is it can insert duplicate records for same long url
     *  Other drawback is They can generate same short URL  for different long URL ,
     *  in case of multiple servers cause of RACE condition
     *  Using MD5 algo we can solve that issue, because it will have same first 43 bits for same long URL
     *  but it has problem of more collision
     * @return short url
     */
    private static String getShortUrl() {
        Random random = new Random();
        StringBuilder shortUrlId = new StringBuilder();

        // Generate a random number of sufficient length
        //Here we have 7 as length for short URL
        for (int i = 0; i <= 6; i++) {  // Adjust length as needed
            shortUrlId.append(BASE62_CHARS.charAt(random.nextInt(BASE62_CHARS.length())));
        }

        return shortUrlId.toString();
    }

    /**
     * In this approach get the counter variable value either from DB or some other storage
     * Use this counter variable to generate shortURL
     * Then this will be used to reduce collision, because every time a new number will be there
     * @return short url
     */
    public String getShortUrlUsingCounterApproach() {

        long counterValue = counter.getAndIncrement();
        return encode(counterValue);
    }

    public String generateShortURLUsingMD5(String longURL) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(longURL.getBytes()); // Hash the long URL
            StringBuilder shortURL = new StringBuilder();
            for (byte b : hash) {
                shortURL.append(String.format("%02x", b)); // Convert bytes to hexadecimal string
            }
            return shortURL.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // Handle unexpected algorithm exceptions
        }

//        byte[] first43Bits = Arrays.copyOfRange(hashBytes, 0, 6);  // Copy first 6 bytes
//        first43Bits[5] &= 0x1F;  // Set the last 3 bits to 0
//
//        long uniqueKey = 0;
//        for (int i = 0; i < 6; i++) {
//            uniqueKey = (uniqueKey << 8) | (first43Bits[i] & 0xFF);
//        }
    }

    //Here divide and remainder operations are used
    public String encode(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int)(num % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            num /= 62;
        }
        return sb.reverse().toString();
    }

    //for passing string in encode method , we need to convert internally string into long
    //for that different algoritms like SHA256 can be used

    public static long decode(String str) {
        long num = 0;
        for (int i = 0; i < str.length(); i++) {
            int digit = BASE62_CHARS.indexOf(str.charAt(i));
            num = num * 62 + digit;
        }
        return num;
    }

    public URLDetails extractRecordFromDB(String shortUrl) {
        return urlDetailsRepo.findByShortURL(shortUrl);
    }
}

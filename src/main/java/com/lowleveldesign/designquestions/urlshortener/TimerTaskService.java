package com.lowleveldesign.designquestions.urlshortener;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TimerTaskService {


    //Execute in every 5 sec
    @Scheduled(fixedRate = 5000)
    public void removeUnusedShortUrls() {
        System.out.println("Removing old URLs");
    }


    //delay after last execution
    @Scheduled(fixedDelay = 5000)
  //  @Scheduled(fixedRate = 5000, initialDelay = 4000) first time wait for more time then run at specifc rate
   // @Scheduled(cron = "0 0 10 * * *") using cron expression

    //In properties file com.scheduled.cron=*/10 * * * * *
    //@Scheduled(cron = "${com.scheduled.cron}") read from property file
    public void removeUnusedShortUrls1() {
        System.out.println("Removing old URLs");
    }
}

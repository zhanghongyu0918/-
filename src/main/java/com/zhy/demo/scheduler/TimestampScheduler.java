package com.zhy.demo.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/3/31
 * Time: 15:19
 * Description: 定时demo
 */
@Slf4j
@Component
public class TimestampScheduler {

    @Scheduled(cron = "0 * * * * ?")
    private void process() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("【TimestampScheduler】【time】{}", sdf.format(new Date()));
    }
}

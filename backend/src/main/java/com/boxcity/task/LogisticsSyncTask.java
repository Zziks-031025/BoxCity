package com.boxcity.task;

import com.boxcity.service.LogisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogisticsSyncTask {

    private final LogisticsService logisticsService;

    @Scheduled(fixedRate = 3600000)
    public void syncLogistics() {
        log.info("执行物流状态轮询任务");
        logisticsService.syncOrderLogistics();
        logisticsService.syncRefundLogistics();
    }
}

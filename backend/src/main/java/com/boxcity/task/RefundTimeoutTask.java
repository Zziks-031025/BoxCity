package com.boxcity.task;

import com.boxcity.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundTimeoutTask {

    private final RefundService refundService;

    @Scheduled(fixedRate = 3600000)
    public void handleTimeoutRefunds() {
        log.info("执行售后超时处理任务");
        refundService.handleTimeoutRefunds();
    }
}

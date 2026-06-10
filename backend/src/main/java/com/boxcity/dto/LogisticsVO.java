package com.boxcity.dto;

import lombok.Data;

import java.util.List;

@Data
public class LogisticsVO {

    private String logisticsCompany;

    private String logisticsNo;

    /** 0运输中 1已签收 */
    private Integer logisticsStatus;

    private List<LogisticsTrackVO> tracks;

    @Data
    public static class LogisticsTrackVO {
        private String time;
        private String context;
        private String location;
    }
}

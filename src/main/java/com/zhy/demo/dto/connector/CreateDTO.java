package com.zhy.demo.dto.connector;

import lombok.Data;

/**
 * @author zhy
 */
@Data
public class CreateDTO {
    private String url;
    private String type;
    private Integer start;
    private Integer end;
    private String name;
    private String kafkaTopic;
    private String topics;
    private String batchSize;
    /**
     * mysqlSource
     */
    private String databaseWhitelist;
    private String tableWhitelist;
    private String databaseHistoryKafkaTopic;
    private String snapshotMode;
    private String databaseServerId;
    /**
     * httpSource
     */
    private String pollIntervalMs;
    /**
     * messagebuSource
     */
    private String prefetchCount;
    private String tags;
    /**
     * jdbcSink
     */
    private String tableNameFormat;
    /**
     * elasticSearchSink
     */
    private String maxBufferedRecords;
    private String maxInFlightRequests;
}

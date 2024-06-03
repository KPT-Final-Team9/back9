package com.core.back9.batch.property;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchPropertyConfig {

    @Bean("testBatchProperty")
    public BatchProperty testBatchProperty() {
        return new BatchProperty("testJob", true, "0 0 0 1 1 *"); // 매년 1월 1일
    }

    @Bean("contractBatchProperty")
    public BatchProperty contractBatchProperty() {
        return new BatchProperty("contractJob", true, "0 0 0 1 1 *"); // 매년 1월 1일
    }

    @Bean("facilityScoreBatchProperty")
    public BatchProperty facilityScoreBatchProperty() {
        return new BatchProperty("facilityScoreJob", true, "0 0 0 1 1,4,7,10 ?"); // 1, 4, 7, 10월 1일 0시 (분기별)
    }

    @Bean("managementScoreBatchProperty")
    public BatchProperty managementScoreBatchProperty() {
        return new BatchProperty("managementScoreJob", true, "0 0 0 1 * ?"); // 매월 1일 0시
    }

}

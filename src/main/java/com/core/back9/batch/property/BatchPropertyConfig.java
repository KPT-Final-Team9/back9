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

}

package com.core.back9.batch.job;

import com.core.back9.batch.job.BatchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BatchConfigFactory {

    private final Map<String, BatchConfig> batchConfigs;

    @Autowired
    public BatchConfigFactory(List<BatchConfig> batchConfigs) {
        this.batchConfigs = batchConfigs.stream()
                .collect(Collectors.toMap(BatchConfig::getIdentifier, Function.identity())); // 메모리 절감 차원에서 i->i 대신 Function.identity()사용
    }

    public BatchConfig getBatchConfig(String identifier) {
        return batchConfigs.get(identifier);
    }
}

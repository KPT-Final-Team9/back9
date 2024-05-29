package com.core.back9.batch.property;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BatchProperty {

    private String jobName;

    private boolean isJobEnabled;

    private String cronExpression;

}

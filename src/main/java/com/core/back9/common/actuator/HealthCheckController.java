package com.core.back9.common.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/public-api/health")
@RestController
public class HealthCheckController {

    private final ServerPortProvider serverPortProvider;
    private final Environment environment;

    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> healthStatus = new LinkedHashMap<>();
        int port = serverPortProvider.getApplicationPort();
        String server = port == 8081 ? "back9-dev1"
                : port == 8082 ? "back9-dev2"
                : port == 8080 ? "local"
                : "unknown";

        healthStatus.put("status", "Connected");
        healthStatus.put("port", port);
        healthStatus.put("operation server", server);
        healthStatus.put("현재 시간", LocalDateTime.now());

        // 추가 정보
        healthStatus.put("activeProfile", environment.getActiveProfiles()[0]);
        healthStatus.put("메모리 사용량", getMemoryUsage());
        healthStatus.put("사용 시간", getUptime());
        healthStatus.put("threadCount", getThreadCount());

        return healthStatus;
    }

    private Map<String, Object> getMemoryUsage() {
        Map<String, Object> memoryUsage = new LinkedHashMap<>();
        Runtime runtime = Runtime.getRuntime();
        memoryUsage.put("totalMemory (MB)", (runtime.totalMemory() / (1024 * 1024)));
        memoryUsage.put("freeMemory (MB)", (runtime.freeMemory() / (1024 * 1024)));
        memoryUsage.put("usedMemory (MB)", ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)));
        memoryUsage.put("maxMemory (MB)", (runtime.maxMemory() / (1024 * 1024)));
        return memoryUsage;
    }

    private int getThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

//    private Map<String, Object> getDiskUsage() {
//        Map<String, Object> diskUsage = new LinkedHashMap<>();
//        try {
//            FileStore store = Files.getFileStore(Paths.get("/"));
//            diskUsage.put("totalSpace", (store.getTotalSpace() / (1024L * 1024 * 1024)));
//            diskUsage.put("usableSpace", (store.getUsableSpace() / (1024L * 1024 * 1024)));
//            diskUsage.put("usedSpace", (store.getTotalSpace() - store.getUsableSpace()) / (1024L * 1024 * 1024));
//        } catch (Exception e) {
//            diskUsage.put("error", e.getMessage());
//        }
//        return diskUsage;
//    }

    private String getUptime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Duration uptime = Duration.ofMillis(runtimeMXBean.getUptime());
        return String.format("%d days, %d hours, %d minutes, %d seconds",
                uptime.toDaysPart(), uptime.toHoursPart(), uptime.toMinutesPart(), uptime.toSecondsPart());
    }

}

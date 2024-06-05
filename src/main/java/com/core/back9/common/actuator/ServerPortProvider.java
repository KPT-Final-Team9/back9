package com.core.back9.common.actuator;

import lombok.Getter;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServerPortProvider implements ApplicationListener<WebServerInitializedEvent> {

    private int applicationPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            this.applicationPort = event.getWebServer().getPort();
        }
    }

}

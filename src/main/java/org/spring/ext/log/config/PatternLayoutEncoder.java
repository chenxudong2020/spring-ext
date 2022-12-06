package org.spring.ext.log.config;


import org.springframework.stereotype.Component;

@Component
public class PatternLayoutEncoder extends  ch.qos.logback.classic.encoder.PatternLayoutEncoder {

    @Override
    public void setPattern(String pattern) {
        String defaultPattern="[%X{TRACE_ID}]  %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n";
        super.setPattern(defaultPattern);
    }
}

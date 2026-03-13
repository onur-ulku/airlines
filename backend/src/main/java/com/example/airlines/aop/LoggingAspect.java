package com.example.airlines.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final int MAX_LENGTH = 400;

    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Around("execution(* com.example.airlines.service..*(..)) || " +
            "execution(* com.example.airlines.controller..*(..))")
    public Object logServiceCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        String request = formatRequest(args);
        long start = System.currentTimeMillis();

        log.info(">>> {} request=[{}]", methodName, request);

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            String response = formatResponse(result);
            log.info("<<< {} response=[{}] ({} ms)", methodName, response, duration);
            return result;
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            log.warn("<<< {} failed after {} ms: {}", methodName, duration, t.getMessage());
            throw t;
        }
    }

    private String formatRequest(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String full = Arrays.stream(args)
                .map(this::toLogString)
                .collect(Collectors.joining(", "));
        return truncate(full, MAX_LENGTH);
    }

    private String formatResponse(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof org.springframework.http.ResponseEntity<?> re) {
            String body = re.getBody() != null ? formatResponse(re.getBody()) : "null";
            return re.getStatusCode() + ", body=" + body;
        }
        if (result instanceof Collection<?> c) {
            if (c.isEmpty()) {
                return "[]";
            }
            String content = c.stream().limit(5).map(this::toLogString).collect(Collectors.joining(", "));
            return c.size() > 5 ? "[" + content + ", ...](size=" + c.size() + ")" : "[" + content + "]";
        }
        return truncate(toLogString(result), MAX_LENGTH);
    }

    private String toLogString(Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof Enum) {
            return o.toString();
        }
        try {
            String json = objectMapper.writeValueAsString(o);
            return truncate(json, 200);
        } catch (Exception e) {
            return o.getClass().getSimpleName() + "(" + truncate(o.toString(), 100) + ")";
        }
    }

    private static String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }
}

package com.core.back9.exception;

import com.core.back9.common.dto.DiscordEmbed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "ExceptionLogger")
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler<T> {

    @Value("${discord.webhook.url}")
    private String discordWebhookUrl;

    private final RestTemplate restTemplate;
    private final Environment environment;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse<ApiException>> apiExceptionHandler(ApiException exception, WebRequest webRequest) {
//		sendDiscordNotification(exception.getErrorMessage(), webRequest);
        log.error("[AppException Occurs] errorCode: {}, message: {}",
                exception.getApiErrorCode().getErrorCode(), exception.getErrorMessage());

        return ResponseEntity
                .status(exception.getApiErrorCode().getErrorCode())
                .body(ErrorResponse.error(exception.getApiErrorCode()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<T>> allExceptionHandler(RuntimeException exception, WebRequest webRequest) {
//		sendDiscordNotification(exception.getLocalizedMessage(), webRequest);
        log.error("[RuntimeException Occurs] exception: {} - {}",
                exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(ApiErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<T>> validExceptionHandler(MethodArgumentNotValidException ex, WebRequest webRequest) {
//		sendDiscordNotification(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), webRequest);
        log.error("[MethodArgumentNotValidException Occurs] message: {}",
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.error(ApiErrorCode.INVALID_REQUEST_CONTENT, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse<T>> MisMatchExceptionHandler(TypeMismatchException e) {
        log.error("[MethodArgumentTypeMismatchException Occurs] error: {} getRequiredType: {} getPropertyName: {}",
                e.getMessage(), e.getRequiredType(), e.getPropertyName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.error(ApiErrorCode.TYPE_MISMATCH));
    }

    @Async
    protected void sendDiscordNotification(String message, WebRequest webRequest) {
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch(profile -> profile.contains("local"))) {
            Map<String, Object> discordPayload = new HashMap<>();
            DiscordEmbed embed = DiscordEmbed.builder()
                    .title("[에러 정보]")
                    .description(
                            "### [발생 시각]\n"
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            + "\n"
                            + "### [요청 URL]\n"
                            + createRequestFullPath(webRequest)
                            + "\n"
                            + "### [Client IP]\n"
                            + getClientIp(webRequest)
                            + "\n"
                            + "### [User-Agent]\n"
                            + getUserAgent(webRequest)
                            + "\n"
                            + "### [에러 메시지]\n"
                            + message
                    )
                    .build();

            discordPayload.put("content", "# \uD83D\uDEA8 에러 발생!");
            discordPayload.put("embeds", List.of(embed));
            restTemplate.postForEntity(discordWebhookUrl, discordPayload, String.class);
        }
    }

    private String createRequestFullPath(WebRequest webRequest) {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        String fullPath = request.getMethod() + " " + request.getRequestURL();
        String queryString = request.getQueryString();
        return queryString != null ? fullPath + "?" + queryString : fullPath;
    }

    private String getClientIp(WebRequest webRequest) {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        return request.getRemoteAddr();
    }

    private String getUserAgent(WebRequest webRequest) {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        return request.getHeader("User-Agent");
    }

}

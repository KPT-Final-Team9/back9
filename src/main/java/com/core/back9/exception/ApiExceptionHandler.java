package com.core.back9.exception;

import com.core.back9.common.dto.DiscordEmbed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

	@Value("${discord.webhook.url}")
	private String discordWebhookUrl;

	private final RestTemplate restTemplate;
	private final Environment environment;

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<String> apiExceptionHandler(ApiException exception, WebRequest webRequest) {
//		sendDiscordNotification(exception.getErrorMessage(), webRequest);
		return ResponseEntity
		  .status(exception.getApiErrorCode().getErrorCode())
		  .body(exception.getErrorMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> allExceptionHandler(RuntimeException exception, WebRequest webRequest) {
//		sendDiscordNotification(exception.getLocalizedMessage(), webRequest);
		return ResponseEntity
		  .status(HttpStatus.INTERNAL_SERVER_ERROR)
		  .body(exception.getLocalizedMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> validExceptionHandler(MethodArgumentNotValidException ex, WebRequest webRequest) {
//		sendDiscordNotification(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), webRequest);
		return ResponseEntity
		  .status(HttpStatus.BAD_REQUEST)
		  .body(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
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

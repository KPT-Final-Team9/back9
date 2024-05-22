package com.core.back9.common.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Profile(value = {"local"})
public class MailManager {

	@Value("${spring.mail.username}")
	private String sender;

	private final JavaMailSender javaMailSender;

	public void send(String to, String title, String contents) throws Exception {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		mimeMessage.setFrom(sender);

		mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		mimeMessage.setSubject(title);
		mimeMessage.setText(contents);
		javaMailSender.send(mimeMessage);
	}

}

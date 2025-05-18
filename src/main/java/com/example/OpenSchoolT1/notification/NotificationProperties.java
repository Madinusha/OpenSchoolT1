package com.example.OpenSchoolT1.notification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.notification")
@Getter
@Setter
public class NotificationProperties {
	private String fromEmail;
	private String toEmail;
}
package com.example.OpenSchoolT1.notification;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final JavaMailSender mailSender;

	public void sendTaskUpdateNotification(Long taskId, String newStatus, TaskRequestDTO dto) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("madinamalika19@yandex.ru");
		message.setTo("madinamalika19@yandex.ru");
		message.setSubject("Task Updated: " + taskId);
		message.setText(String.format(
				"Task %d updated:\nStatus: %s\nTitle: %s\nDescription: %s",
				taskId, newStatus, dto.getTitle(), dto.getDescription()
		));

		try {
			mailSender.send(message);
			log.info("Notification sent for task {}", taskId);
		} catch (MailException ex) {
			log.error("Failed to send notification for task {}", taskId, ex);
		}
	}
}
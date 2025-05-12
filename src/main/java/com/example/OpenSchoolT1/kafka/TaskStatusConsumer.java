package com.example.OpenSchoolT1.kafka;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskStatusConsumer {

	private final NotificationService notificationService;

	@KafkaListener(
			topics = "${spring.kafka.topic.task-status-changes}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	public void handleTaskUpdate(ConsumerRecord<String, TaskRequestDTO> record, Acknowledgment acknowledgment) {
		log.info("Received task update: {}", record.value());

		TaskRequestDTO dto = record.value();
		notificationService.sendTaskUpdateNotification(
				Long.parseLong(record.key()),
				String.valueOf(dto.getStatus()),
				dto
		);
		acknowledgment.acknowledge();
	}
}


package com.example.OpenSchoolT1.kafka;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskStatusProducer {

	private final KafkaTemplate<String, TaskRequestDTO> kafkaTemplate;

	@Value("${spring.kafka.topic.task-status-changes}")
	private String topicName;

	public void sendTaskUpdate(Long taskId, TaskRequestDTO dto) {
		kafkaTemplate.send(topicName, taskId.toString(), dto);
	}
}


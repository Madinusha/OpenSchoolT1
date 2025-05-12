package com.example.OpenSchoolT1.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka.topic")
@Getter
@Setter
public class KafkaTopicsProperties {

	private String taskStatusChanges;

	@ConstructorBinding
	public KafkaTopicsProperties(@Value("${spring.kafka.topic.task-status-changes}") String taskStatusChanges) {
		this.taskStatusChanges = taskStatusChanges;
	}
}
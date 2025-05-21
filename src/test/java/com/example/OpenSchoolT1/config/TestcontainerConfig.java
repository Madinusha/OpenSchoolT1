package com.example.OpenSchoolT1.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestcontainerConfig {
	private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("apache/kafka:latest");

	@Container
	public static KafkaContainer kafkaContainer = new KafkaContainer(KAFKA_IMAGE)
			.withExposedPorts(9092);

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
		registry.add("spring.kafka.task-status-changes", () -> "task-status-changes");
	}
}
package com.example.OpenSchoolT1.configuration;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.kafka.TaskDtoDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	@Bean
	public ConsumerFactory<String, TaskRequestDTO> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TaskDtoDeserializer.class);

		config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.OpenSchoolT1.dto");
		config.put(JsonDeserializer.TYPE_MAPPINGS, "TaskRequestDTO:com.example.OpenSchoolT1.dto.TaskRequestDTO");

		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new TaskDtoDeserializer());
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, TaskRequestDTO> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, TaskRequestDTO> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 3L)));

		return factory;
	}
}
package com.example.OpenSchoolT1.kafka;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TaskDtoDeserializer extends JsonDeserializer<TaskRequestDTO> {

	public TaskDtoDeserializer() {
		super(TaskRequestDTO.class);
	}

	@Override
	public TaskRequestDTO deserialize(String topic, Headers headers, byte[] data) {
		try {
			return super.deserialize(topic, headers, data);
		} catch (Exception e) {
			log.error("Failed to deserialize message: {}", new String(data, StandardCharsets.UTF_8));
			throw new SerializationException("Deserialization error", e);
		}
	}
}
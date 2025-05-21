package com.example.OpenSchoolT1.service;

import com.example.OpenSchoolT1.dto.TaskRequestDTO;
import com.example.OpenSchoolT1.dto.TaskResponseDTO;
import com.example.OpenSchoolT1.entity.TaskStatus;
import com.example.OpenSchoolT1.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringTaskServiceTest {

	@Container
	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass");

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRepository taskRepository;

	@BeforeEach
	void setUp() {
		taskRepository.deleteAll();
	}

	@Test
	@DisplayName("Создание задачи должно сохранить её в БД")
	void createTask_shouldSaveToDatabase() {
		TaskRequestDTO dto = getExampleTaskRequestDTO();
		TaskResponseDTO result = taskService.createTask(dto);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertThat(taskRepository.findById(result.getId())).isPresent();
	}

	private TaskRequestDTO getExampleTaskRequestDTO() {
		TaskRequestDTO example = new TaskRequestDTO();
		example.setTitle("Title");
		example.setDescription("Description");
		example.setUserId(1L);
		example.setStatus(TaskStatus.valueOf("NEW"));
		return example;
	}
}

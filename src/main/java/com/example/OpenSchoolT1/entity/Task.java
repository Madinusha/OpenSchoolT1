package com.example.OpenSchoolT1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String description;
	private Long userId;

	@Enumerated(EnumType.STRING)
	private TaskStatus status;
}

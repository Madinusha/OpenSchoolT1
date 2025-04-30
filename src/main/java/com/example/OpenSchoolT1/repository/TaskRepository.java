package com.example.OpenSchoolT1.repository;

import com.example.OpenSchoolT1.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {}
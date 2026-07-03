package com.schoolworkhub;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusOrderByIdDesc(String status);
    List<Task> findByAuthorIdOrderByIdDesc(Long authorId);
    List<Task> findByExecutorIdOrderByIdDesc(Long executorId);
}

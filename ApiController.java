package com.schoolworkhub;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiController(UserRepository userRepository, TaskRepository taskRepository,
                         ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        if (userRepository.existsByUsername(body.get("username"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь существует"));
        }
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(passwordEncoder.encode(body.get("password")));
        user.setEmail(body.get("email"));
        user.setCardDetails(body.get("cardDetails"));
        user.setRole("STUDENT");
        user.setRating(0.0);
        user.setTotalCompleted(0);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Optional<User> opt = userRepository.findByUsername(body.get("username"));
        if (opt.isPresent() && passwordEncoder.matches(body.get("password"), opt.get().getPassword())) {
            User u = opt.get();
            Map<String, Object> resp = new HashMap<>();
            resp.put("userId", u.getId());
            resp.put("username", u.getUsername());
            resp.put("role", u.getRole());
            resp.put("cardDetails", u.getCardDetails());
            resp.put("rating", u.getRating());
            resp.put("totalCompleted", u.getTotalCompleted());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Неверный логин или пароль"));
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getOpenTasks() {
        return ResponseEntity.ok(taskRepository.findByStatusOrderByIdDesc("OPEN"));
    }

    @GetMapping("/tasks/my-authored")
    public ResponseEntity<?> getMyAuthored(@RequestParam Long userId) {
        return ResponseEntity.ok(taskRepository.findByAuthorIdOrderByIdDesc(userId));
    }

    @GetMapping("/tasks/my-executed")
    public ResponseEntity<?> getMyExecuted(@RequestParam Long userId) {
        return ResponseEntity.ok(taskRepository.findByExecutorIdOrderByIdDesc(userId));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tasks/create")
    public ResponseEntity<?> createTask(@RequestBody Map<String, Object> body) {
        Task task = new Task();
        task.setTitle((String) body.get("title"));
        task.setDescription((String) body.get("description"));
        task.setSubject((String) body.get("subject"));
        task.setPrice((Integer) body.get("price"));
        task.setStatus("OPEN");
        task.setAuthorId(Long.valueOf(body.get("authorId").toString()));
        task.setAuthorName((String) body.get("authorName"));
        task.setAuthorCard((String) body.get("authorCard"));
        taskRepository.save(task);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/tasks/{id}/take")
    public ResponseEntity<?> takeTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Task> opt = taskRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Task task = opt.get();
        if (!"OPEN".equals(task.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Задание недоступно"));
        }
        task.setStatus("IN_PROGRESS");
        task.setExecutorId(Long.valueOf(body.get("executorId").toString()));
        task.setExecutorName((String) body.get("executorName"));
        task.setExecutorCard((String) body.get("executorCard"));
        taskRepository.save(task);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/tasks/{id}/confirm")
    public ResponseEntity<?> confirmTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Task> opt = taskRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Task task = opt.get();
        task.setStatus("COMPLETED");
        task.setExecutorRating((Integer) body.get("rating"));
        taskRepository.save(task);

        Optional<User> execOpt = userRepository.findById(task.getExecutorId());
        if (execOpt.isPresent()) {
            User exec = execOpt.get();
            int completed = exec.getTotalCompleted() + 1;
            double newRating = (exec.getRating() * (completed - 1) + (Integer) body.get("rating")) / completed;
            exec.setRating(Math.round(newRating * 10.0) / 10.0);
            exec.setTotalCompleted(completed);
            userRepository.save(exec);
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/tasks/{id}/cancel")
    public ResponseEntity<?> cancelTask(@PathVariable Long id) {
        Optional<Task> opt = taskRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Task task = opt.get();
        task.setStatus("CANCELLED");
        taskRepository.save(task);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/tasks/{id}/report")
    public ResponseEntity<?> reportTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        if (reportRepository.existsByTaskIdAndReporterId(id, Long.valueOf(body.get("reporterId").toString()))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Вы уже жаловались"));
        }
        Report report = new Report();
        report.setTaskId(id);
        report.setReporterId(Long.valueOf(body.get("reporterId").toString()));
        report.setReporterName((String) body.get("reporterName"));
        report.setReason((String) body.get("reason"));
        report.setDescription((String) body.get("description"));
        report.setStatus("PENDING");
        reportRepository.save(report);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/admin/reports")
    public ResponseEntity<?> getReports() {
        return ResponseEntity.ok(reportRepository.findByStatusOrderByIdDesc("PENDING"));
    }

    @PostMapping("/admin/reports/{id}/resolve")
    public ResponseEntity<?> resolveReport(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Report> opt = reportRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Report report = opt.get();
        report.setStatus("RESOLVED");
        report.setAdminComment((String) body.get("comment"));
        reportRepository.save(report);

        if (Boolean.TRUE.equals(body.get("deleteTask"))) {
            taskRepository.deleteById(report.getTaskId());
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/admin/reports/{id}/dismiss")
    public ResponseEntity<?> dismissReport(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Report> opt = reportRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Report report = opt.get();
        report.setStatus("DISMISSED");
        report.setAdminComment(body.get("comment"));
        reportRepository.save(report);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}

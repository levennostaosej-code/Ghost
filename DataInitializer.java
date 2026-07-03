package com.schoolworkhub;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@school.com");
            admin.setRole("ADMIN");
            admin.setCardDetails("0000 0000 0000 0000");
            admin.setRating(5.0);
            admin.setTotalCompleted(0);
            userRepository.save(admin);

            User ayse = new User();
            ayse.setUsername("ayse");
            ayse.setPassword(passwordEncoder.encode("123456"));
            ayse.setEmail("ayse@mail.com");
            ayse.setRole("STUDENT");
            ayse.setCardDetails("1111 2222 3333 4444");
            ayse.setRating(4.5);
            ayse.setTotalCompleted(5);
            userRepository.save(ayse);

            User mehmet = new User();
            mehmet.setUsername("mehmet");
            mehmet.setPassword(passwordEncoder.encode("123456"));
            mehmet.setEmail("mehmet@mail.com");
            mehmet.setRole("STUDENT");
            mehmet.setCardDetails("5555 6666 7777 8888");
            mehmet.setRating(3.0);
            mehmet.setTotalCompleted(2);
            userRepository.save(mehmet);

            Task t1 = new Task();
            t1.setTitle("Помочь с математикой");
            t1.setDescription("Нужно решить 5 задач по алгебре за 8 класс");
            t1.setSubject("Математика");
            t1.setPrice(500);
            t1.setStatus("OPEN");
            t1.setAuthorId(2L);
            t1.setAuthorName("ayse");
            t1.setAuthorCard("1111 2222 3333 4444");
            taskRepository.save(t1);

            Task t2 = new Task();
            t2.setTitle("Написать эссе по литературе");
            t2.setDescription("Тема: Образ героя в романе. 2-3 страницы");
            t2.setSubject("Литература");
            t2.setPrice(300);
            t2.setStatus("OPEN");
            t2.setAuthorId(3L);
            t2.setAuthorName("mehmet");
            t2.setAuthorCard("5555 6666 7777 8888");
            taskRepository.save(t2);

            Task t3 = new Task();
            t3.setTitle("Сделать презентацию по физике");
            t3.setDescription("10 слайдов про законы Ньютона");
            t3.setSubject("Физика");
            t3.setPrice(700);
            t3.setStatus("OPEN");
            t3.setAuthorId(2L);
            t3.setAuthorName("ayse");
            t3.setAuthorCard("1111 2222 3333 4444");
            taskRepository.save(t3);
        }
    }
}

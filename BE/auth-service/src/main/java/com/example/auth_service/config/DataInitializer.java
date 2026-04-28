package com.example.auth_service.config;

import com.example.common.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeDatabase(UserRepository userRepository) {
        return args -> System.out.println(
                "APP_AUTH rows available for authentication: " + userRepository.count()
        );
        /*
            // Add test user if it doesn't exist
            if (userRepository.findByUsername("user").isEmpty()) {
                User testUser = new User();
                testUser.setUsername("user");
                testUser.setPassword(passwordEncoder.encode("1234"));
                userRepository.save(testUser);
                System.out.println(" Test user created: username='user', password='1234'");
            } else {
                System.out.println("ℹ Test user already exists");
            }
        };
        */
    }
}

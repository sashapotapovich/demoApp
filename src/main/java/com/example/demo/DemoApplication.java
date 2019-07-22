package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ImageService;
import com.example.demo.storage.StorageProperties;
import com.example.demo.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DemoApplication extends SpringBootServletInitializer {
    
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, StorageService storageService, PasswordEncoder passwordEncoder) {
        return args1 -> {
/*            userRepository.save(new User("admin@admin.com", passwordEncoder.encode("admin"), "admin", "admin", "ADMIN"));
            userRepository.save(new User("user@user.com", passwordEncoder.encode("user"), "user", "user", "USER"));*/
            storageService.init();
        };
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

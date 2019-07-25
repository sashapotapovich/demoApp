package com.example.demo;

import com.example.demo.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DemoApplication extends SpringBootServletInitializer {
    
/*    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args1 -> {
            userRepository.save(new User("admin@admin.com", passwordEncoder.encode("admin"), "admin", "admin", "ROLE_ADMIN"));
            userRepository.save(new User("user@user.com", passwordEncoder.encode("user"), "user", "user", "ROLE_USER"));
        };
    }*/

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}


package com.hotelbooking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hotelbooking.entity.Admin;
import com.hotelbooking.repository.AdminRepository;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner createAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (adminRepository.findByUsername("admin").isEmpty()) {

                Admin admin = new Admin();

                admin.setUsername("admin");
                admin.setPassword(
                    passwordEncoder.encode("Admin@123")
                );
                admin.setRole("ADMIN");

                adminRepository.save(admin);

                System.out.println("Admin account created!");
            }
        };
    }
}

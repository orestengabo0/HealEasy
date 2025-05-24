package org.healeasy.utils;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.healeasy.entities.User;
import org.healeasy.enums.UserRole;
import org.healeasy.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Utility class to seed the database with fake user data for testing purposes.
 */
@Configuration
@RequiredArgsConstructor
public class UserDataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Default avatar placeholder URL
    private static final String DEFAULT_AVATAR_URL = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png";

    /**
     * CommandLineRunner bean that seeds the database with fake user data.
     * This is disabled by default to avoid accidentally seeding production data.
     * Use the seedUsers() method to manually trigger seeding.
     * 
     * @return CommandLineRunner that seeds the database
     */
    //@Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            seedUsers(100);
        };
    }

    /**
     * Seeds the database with the specified number of fake users.
     * This method can be called from a controller endpoint to manually trigger seeding.
     * 
     * @param count Number of users to generate
     * @return Number of users generated
     */
    public int seedUsers(int count) {
        // Check if we already have users in the database
        long userCount = userRepository.count();
        if (userCount > 100) { // More than just the admin user
            System.out.println("Database already seeded with " + userCount + " users. Skipping seeding.");
            return 0;
        }

        System.out.println("Seeding database with fake user data...");
        List<User> users = generateFakeUsers(count);
        userRepository.saveAll(users);
        System.out.println("Database seeded with " + users.size() + " fake users.");
        return users.size();
    }

    /**
     * Generates a list of fake users with random data.
     * 
     * @param count Number of users to generate
     * @return List of User entities with fake data
     */
    private List<User> generateFakeUsers(int count) {
        List<User> users = new ArrayList<>();
        Faker faker = new Faker(new Locale("en-US"));
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            User user = new User();

            // Generate unique username
            String username = faker.name().username();
            while (userRepository.findByUsername(username) != null) {
                username = faker.name().username();
            }
            user.setUsername(username);

            // Generate unique email
            String email = faker.internet().emailAddress();
            while (userRepository.existsByEmail(email)) {
                email = faker.internet().emailAddress();
            }
            user.setEmail(email);

            // Generate unique phone number
            String phoneNumber = faker.phoneNumber().cellPhone().replaceAll("[^0-9]", "");
            // Ensure phone number is between 9 and 12 digits
            if (phoneNumber.length() > 12) {
                phoneNumber = phoneNumber.substring(0, 12);
            } else if (phoneNumber.length() < 9) {
                phoneNumber = phoneNumber + "0".repeat(9 - phoneNumber.length());
            }

            while (userRepository.existsByPhoneNumber(phoneNumber)) {
                phoneNumber = faker.phoneNumber().cellPhone().replaceAll("[^0-9]", "");
                if (phoneNumber.length() > 12) {
                    phoneNumber = phoneNumber.substring(0, 12);
                } else if (phoneNumber.length() < 9) {
                    phoneNumber = phoneNumber + "0".repeat(9 - phoneNumber.length());
                }
            }
            user.setPhoneNumber(phoneNumber);

            // Set password (same for all test users for simplicity)
            user.setPassword(passwordEncoder.encode("Password@123"));

            // Set profile image URL (50% chance of having a placeholder, 50% chance of being null)
            if (random.nextBoolean()) {
                user.setProfileImageUrl(DEFAULT_AVATAR_URL);
            } else {
                user.setProfileImageUrl(null);
            }

            // Set role (80% PATIENT, 15% DOCTOR, 5% ADMIN)
            int roleRandom = random.nextInt(100);
            if (roleRandom < 80) {
                user.setRole(UserRole.PATIENT);
            } else if (roleRandom < 95) {
                user.setRole(UserRole.DOCTOR);
            } else {
                user.setRole(UserRole.ADMIN);
            }

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            users.add(user);
        }

        return users;
    }
}

package org.healeasy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.healeasy.utils.UserDataSeeder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for seeding test data into the database.
 * These endpoints are secured and should only be accessible to administrators.
 */
@RestController
@RequestMapping("/api/v1/admin/seed")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Data Seeding", description = "Endpoints for generating test data in the database")
public class DataSeedController {

    private final UserDataSeeder userDataSeeder;

    /**
     * Seeds the database with fake user data.
     * 
     * @param count Number of users to generate (default: 100)
     * @return Response with the number of users generated
     */
    @Operation(
        summary = "Generate fake user data",
        description = "Creates the specified number of fake users in the database for testing purposes. " +
                "Users will have random usernames, emails, and phone numbers. " +
                "All users will have the same password: 'Password@123'. " +
                "Profile images will be either null or a placeholder URL. " +
                "Roles will be distributed as: 80% PATIENT, 15% DOCTOR, 5% ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users successfully generated or database already seeded"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not have admin role")
    })
    @PostMapping("/users")
    public ResponseEntity<String> seedUsers(
            @Parameter(description = "Number of users to generate (default: 100)", example = "100")
            @RequestParam(defaultValue = "100") int count) {
        int generatedCount = userDataSeeder.seedUsers(count);

        if (generatedCount > 0) {
            return ResponseEntity.ok("Successfully generated " + generatedCount + " fake users.");
        } else {
            return ResponseEntity.ok("No users were generated. Database may already be seeded.");
        }
    }
}

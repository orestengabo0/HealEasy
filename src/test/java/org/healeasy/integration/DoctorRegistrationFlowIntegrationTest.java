package org.healeasy.integration;

import org.healeasy.DTOs.DoctorDto;
import org.healeasy.DTOs.DoctorRegistrationCompletionRequest;
import org.healeasy.DTOs.DoctorRegistrationRequest;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.InvitationCode;
import org.healeasy.entities.User;
import org.healeasy.enums.DoctorStatus;
import org.healeasy.enums.UserRole;
import org.healeasy.repositories.DoctorRepository;
import org.healeasy.repositories.InvitationCodeRepository;
import org.healeasy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the entire doctor registration workflow.
 * Tests the flow from registration to approval to completion.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DoctorRegistrationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    private MockMultipartFile licenseDocument;
    private MockMultipartFile idDocument;
    private MockMultipartFile profilePhoto;

    @BeforeEach
    public void setup() {
        // Setup mock files for testing
        licenseDocument = new MockMultipartFile(
                "licenseDocument",
                "license.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "license document content".getBytes()
        );

        idDocument = new MockMultipartFile(
                "idDocument",
                "id.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "id document content".getBytes()
        );

        profilePhoto = new MockMultipartFile(
                "profilePhoto",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "profile photo content".getBytes()
        );
    }

    /**
     * Test the entire doctor registration workflow:
     * 1. Doctor registers (creates PENDING_DOCTOR)
     * 2. Admin approves the doctor
     * 3. Doctor validates invitation code
     * 4. Doctor completes registration
     * 5. Verify final state (DOCTOR role, APPROVED status)
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testFullDoctorRegistrationFlow() throws Exception {
        // Step 1: Doctor Registration
        Long doctorId = registerDoctor();
        assertNotNull(doctorId, "Doctor ID should not be null after registration");

        // Verify doctor is in PENDING status
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        assertNotNull(doctor, "Doctor should exist in database");
        assertEquals(DoctorStatus.PENDING, doctor.getStatus(), "Doctor should be in PENDING status");
        
        User user = doctor.getUser();
        assertNotNull(user, "User should exist in database");
        assertEquals(UserRole.PENDING_DOCTOR, user.getRole(), "User should have PENDING_DOCTOR role");

        // Step 2: Admin Approval
        String invitationCode = approveDoctor(doctorId);
        assertNotNull(invitationCode, "Invitation code should not be null after approval");

        // Verify doctor is now in APPROVED status
        doctor = doctorRepository.findById(doctorId).orElse(null);
        assertNotNull(doctor, "Doctor should exist in database");
        assertEquals(DoctorStatus.APPROVED, doctor.getStatus(), "Doctor should be in APPROVED status");

        // Step 3: Validate Invitation Code
        validateInvitationCode(invitationCode, doctorId);

        // Step 4: Complete Registration
        completeRegistration(invitationCode);

        // Step 5: Verify Final State
        doctor = doctorRepository.findById(doctorId).orElse(null);
        assertNotNull(doctor, "Doctor should exist in database");
        assertEquals(DoctorStatus.APPROVED, doctor.getStatus(), "Doctor should be in APPROVED status");
        
        user = doctor.getUser();
        assertNotNull(user, "User should exist in database");
        assertEquals(UserRole.DOCTOR, user.getRole(), "User should have DOCTOR role");

        // Verify invitation code is marked as used
        Optional<InvitationCode> code = invitationCodeRepository.findByCode(invitationCode);
        assertTrue(code.isPresent(), "Invitation code should exist in database");
        assertTrue(code.get().isUsed(), "Invitation code should be marked as used");
    }

    /**
     * Helper method to register a new doctor.
     * 
     * @return The ID of the created doctor
     */
    private Long registerDoctor() throws Exception {
        // Create multipart request
        MvcResult result = mockMvc.perform(multipart("/api/v1/doctors/register")
                .file(licenseDocument)
                .file(idDocument)
                .param("username", "drsmith")
                .param("email", "drsmith@example.com")
                .param("password", "Password@123")
                .param("phoneNumber", "1234567890")
                .param("specialization", "Cardiology")
                .param("licenceNumber", "MED12345")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        // Extract doctor ID from response
        String responseContent = result.getResponse().getContentAsString();
        // In a real test, you would use a JSON parser to extract the ID
        // For simplicity, we'll just extract it using string manipulation
        int idIndex = responseContent.indexOf("\"id\":");
        int commaIndex = responseContent.indexOf(",", idIndex);
        String idStr = responseContent.substring(idIndex + 5, commaIndex).trim();
        return Long.parseLong(idStr);
    }

    /**
     * Helper method to approve a doctor.
     * 
     * @param doctorId The ID of the doctor to approve
     * @return The generated invitation code
     */
    private String approveDoctor(Long doctorId) throws Exception {
        // Approve doctor
        mockMvc.perform(post("/api/v1/admin/doctors/{doctorId}/approve", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        // Generate invitation code
        MvcResult result = mockMvc.perform(post("/api/v1/admin/doctors/{doctorId}/invitation-code", doctorId)
                .param("expirationDays", "7"))
                .andExpect(status().isOk())
                .andReturn();

        // Extract invitation code from response
        return result.getResponse().getContentAsString().replace("\"", "");
    }

    /**
     * Helper method to validate an invitation code.
     * 
     * @param invitationCode The invitation code to validate
     * @param expectedDoctorId The expected doctor ID associated with the code
     */
    private void validateInvitationCode(String invitationCode, Long expectedDoctorId) throws Exception {
        mockMvc.perform(get("/api/v1/doctors/validate-invitation")
                .param("code", invitationCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDoctorId));
    }

    /**
     * Helper method to complete doctor registration.
     * 
     * @param invitationCode The invitation code to use
     */
    private void completeRegistration(String invitationCode) throws Exception {
        mockMvc.perform(multipart("/api/v1/doctors/complete-registration")
                .file(profilePhoto)
                .param("invitationCode", invitationCode)
                .param("password", "NewPassword@123")
                .param("passwordConfirmation", "NewPassword@123")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    /**
     * Test the rejection flow:
     * 1. Doctor registers (creates PENDING_DOCTOR)
     * 2. Admin rejects the doctor
     * 3. Verify final state (PENDING_DOCTOR role, REJECTED status)
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDoctorRejectionFlow() throws Exception {
        // Step 1: Doctor Registration
        Long doctorId = registerDoctor();
        assertNotNull(doctorId, "Doctor ID should not be null after registration");

        // Step 2: Admin Rejection
        mockMvc.perform(post("/api/v1/admin/doctors/{doctorId}/reject", doctorId)
                .param("reason", "Invalid license"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        // Step 3: Verify Final State
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        assertNotNull(doctor, "Doctor should exist in database");
        assertEquals(DoctorStatus.REJECTED, doctor.getStatus(), "Doctor should be in REJECTED status");
        
        User user = doctor.getUser();
        assertNotNull(user, "User should exist in database");
        assertEquals(UserRole.PENDING_DOCTOR, user.getRole(), "User should still have PENDING_DOCTOR role");
    }

    /**
     * Test validation errors during registration.
     */
    @Test
    public void testRegistrationValidationErrors() throws Exception {
        // Test invalid email format
        mockMvc.perform(multipart("/api/v1/doctors/register")
                .file(licenseDocument)
                .file(idDocument)
                .param("username", "drsmith")
                .param("email", "invalid-email")  // Invalid email format
                .param("password", "Password@123")
                .param("phoneNumber", "1234567890")
                .param("specialization", "Cardiology")
                .param("licenceNumber", "MED12345")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        // Test invalid password format
        mockMvc.perform(multipart("/api/v1/doctors/register")
                .file(licenseDocument)
                .file(idDocument)
                .param("username", "drsmith")
                .param("email", "drsmith@example.com")
                .param("password", "weak")  // Invalid password format
                .param("phoneNumber", "1234567890")
                .param("specialization", "Cardiology")
                .param("licenceNumber", "MED12345")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        // Test missing required field
        mockMvc.perform(multipart("/api/v1/doctors/register")
                .file(licenseDocument)
                .file(idDocument)
                .param("username", "drsmith")
                .param("email", "drsmith@example.com")
                .param("password", "Password@123")
                .param("phoneNumber", "1234567890")
                // Missing specialization
                .param("licenceNumber", "MED12345")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}
package org.healeasy.controllers;

import org.healeasy.DTOs.DoctorDto;
import org.healeasy.DTOs.DoctorRegistrationCompletionRequest;
import org.healeasy.DTOs.DoctorRegistrationRequest;
import org.healeasy.Iservices.IDoctorService;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.User;
import org.healeasy.enums.DoctorStatus;
import org.healeasy.enums.UserRole;
import org.healeasy.exceptions.EmailAlreadyExistsException;
import org.healeasy.exceptions.PhoneNumberAlreadyExistsException;
import org.healeasy.mappers.DoctorMapper;
import org.healeasy.services.CloudinaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the DoctorController class.
 * Tests the API endpoints for doctor registration and management.
 */
public class DoctorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDoctorService doctorService;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private CloudinaryServiceImpl cloudinaryService;

    @InjectMocks
    private DoctorController doctorController;

    private Doctor testDoctor;
    private DoctorDto testDoctorDto;
    private User testUser;
    private DoctorRegistrationRequest validRegistrationRequest;
    private MockMultipartFile licenseDocument;
    private MockMultipartFile idDocument;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("drsmith");
        testUser.setEmail("drsmith@example.com");
        testUser.setPhoneNumber("1234567890");
        testUser.setRole(UserRole.PENDING_DOCTOR);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setUser(testUser);
        testDoctor.setSpecialization("Cardiology");
        testDoctor.setLicenceNumber("MED12345");
        testDoctor.setStatus(DoctorStatus.PENDING);
        testDoctor.setCreatedAt(LocalDateTime.now());
        testDoctor.setUpdatedAt(LocalDateTime.now());

        testDoctorDto = new DoctorDto();
        testDoctorDto.setId(1L);
        testDoctorDto.setUsername("drsmith");
        testDoctorDto.setEmail("drsmith@example.com");
        testDoctorDto.setPhoneNumber("1234567890");
        testDoctorDto.setSpecialization("Cardiology");
        testDoctorDto.setLicenceNumber("MED12345");
        testDoctorDto.setStatus(DoctorStatus.PENDING);
        testDoctorDto.setCreatedAt(testDoctor.getCreatedAt());
        testDoctorDto.setUpdatedAt(testDoctor.getUpdatedAt());

        // Setup mock files
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

        // Setup valid registration request
        validRegistrationRequest = new DoctorRegistrationRequest();
        validRegistrationRequest.setUsername("drsmith");
        validRegistrationRequest.setEmail("drsmith@example.com");
        validRegistrationRequest.setPassword("Password@123");
        validRegistrationRequest.setPhoneNumber("1234567890");
        validRegistrationRequest.setSpecialization("Cardiology");
        validRegistrationRequest.setLicenceNumber("MED12345");
        validRegistrationRequest.setLicenseDocument(licenseDocument);
        validRegistrationRequest.setIdDocument(idDocument);

        // Setup mocks
        when(cloudinaryService.getMaxRequestSize()).thenReturn("7MB");
        when(cloudinaryService.extractMBsFromStrSize(anyString())).thenReturn(7 * 1024 * 1024);
        when(doctorService.isLicenseNumberInUse(anyString())).thenReturn(false);
        when(doctorService.registerDoctor(any(DoctorRegistrationRequest.class))).thenReturn(testDoctor);
        when(doctorMapper.toDto(any(Doctor.class))).thenReturn(testDoctorDto);
    }

    /**
     * Test registering a doctor with valid data.
     * Should return 201 Created with the doctor details.
     */
    @Test
    public void testRegisterDoctor_Success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        request.setContent(new byte[1000]); // Small content size

        var response = doctorController.registerDoctor(validRegistrationRequest, request);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.CREATED;
        assert response.getBody() != null;
        assert response.getBody().getId().equals(1L);
        assert response.getBody().getUsername().equals("drsmith");
        assert response.getBody().getStatus() == DoctorStatus.PENDING;

        // Verify service was called
        verify(doctorService, times(1)).isLicenseNumberInUse("MED12345");
        verify(doctorService, times(1)).registerDoctor(any(DoctorRegistrationRequest.class));
    }

    /**
     * Test registering a doctor with a license number that's already in use.
     * Should return 409 Conflict.
     */
    @Test
    public void testRegisterDoctor_DuplicateLicense() throws Exception {
        // Setup
        when(doctorService.isLicenseNumberInUse("MED12345")).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        request.setContent(new byte[1000]); // Small content size

        var response = doctorController.registerDoctor(validRegistrationRequest, request);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.CONFLICT;
        assert response.getBody() == null;

        // Verify service was called
        verify(doctorService, times(1)).isLicenseNumberInUse("MED12345");
        verify(doctorService, never()).registerDoctor(any(DoctorRegistrationRequest.class));
    }

    /**
     * Test registering a doctor with an email that's already in use.
     * Should handle EmailAlreadyExistsException.
     */
    @Test
    public void testRegisterDoctor_DuplicateEmail() throws Exception {
        // Setup
        when(doctorService.registerDoctor(any(DoctorRegistrationRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        request.setContent(new byte[1000]); // Small content size

        var response = doctorController.registerDoctor(validRegistrationRequest, request);

        // Verify the response - controller should return 500 as it doesn't specifically handle this exception
        assert response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        assert response.getBody() == null;

        // Verify service was called
        verify(doctorService, times(1)).isLicenseNumberInUse("MED12345");
        verify(doctorService, times(1)).registerDoctor(any(DoctorRegistrationRequest.class));
    }

    /**
     * Test validating a valid invitation code.
     * Should return 200 OK with the doctor details.
     */
    @Test
    public void testValidateInvitationCode_Valid() throws Exception {
        // Setup
        when(doctorService.validateInvitationCode("ABC12345")).thenReturn(testDoctor);

        var response = doctorController.validateInvitationCode("ABC12345");

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getId().equals(1L);

        // Verify service was called
        verify(doctorService, times(1)).validateInvitationCode("ABC12345");
    }

    /**
     * Test validating an invalid invitation code.
     * Should return 404 Not Found.
     */
    @Test
    public void testValidateInvitationCode_Invalid() throws Exception {
        // Setup
        when(doctorService.validateInvitationCode("INVALID")).thenReturn(null);

        var response = doctorController.validateInvitationCode("INVALID");

        // Verify the response
        assert response.getStatusCode() == HttpStatus.NOT_FOUND;
        assert response.getBody() == null;

        // Verify service was called
        verify(doctorService, times(1)).validateInvitationCode("INVALID");
    }

    /**
     * Test completing registration with valid data.
     * Should return 200 OK with the updated doctor details.
     */
    @Test
    public void testCompleteRegistration_Success() throws Exception {
        // Setup
        DoctorRegistrationCompletionRequest completionRequest = new DoctorRegistrationCompletionRequest();
        completionRequest.setInvitationCode("ABC12345");
        completionRequest.setPassword("NewPassword@123");
        completionRequest.setPasswordConfirmation("NewPassword@123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        request.setContent(new byte[1000]); // Small content size

        // Update test doctor for completion
        testDoctor.getUser().setRole(UserRole.DOCTOR);
        when(doctorService.completeRegistration(eq("ABC12345"), eq("NewPassword@123"), any())).thenReturn(testDoctor);

        var response = doctorController.completeRegistration(completionRequest, request);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getId().equals(1L);

        // Verify service was called
        verify(doctorService, times(1)).completeRegistration(eq("ABC12345"), eq("NewPassword@123"), any());
    }

    /**
     * Test completing registration with password mismatch.
     * Should return 400 Bad Request.
     */
    @Test
    public void testCompleteRegistration_PasswordMismatch() throws Exception {
        // Setup
        DoctorRegistrationCompletionRequest completionRequest = new DoctorRegistrationCompletionRequest();
        completionRequest.setInvitationCode("ABC12345");
        completionRequest.setPassword("NewPassword@123");
        completionRequest.setPasswordConfirmation("DifferentPassword@123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        request.setContent(new byte[1000]); // Small content size

        var response = doctorController.completeRegistration(completionRequest, request);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody() == null;

        // Verify service was not called
        verify(doctorService, never()).completeRegistration(anyString(), anyString(), any());
    }
}

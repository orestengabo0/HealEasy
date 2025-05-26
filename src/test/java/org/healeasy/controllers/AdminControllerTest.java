package org.healeasy.controllers;

import org.healeasy.DTOs.DoctorDto;
import org.healeasy.Iservices.IDoctorService;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.InvitationCode;
import org.healeasy.entities.User;
import org.healeasy.enums.DoctorStatus;
import org.healeasy.enums.UserRole;
import org.healeasy.mappers.DoctorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AdminController class.
 * Tests the API endpoints for admin operations on doctor registrations.
 */
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDoctorService doctorService;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private AdminController adminController;

    private Doctor pendingDoctor;
    private Doctor approvedDoctor;
    private Doctor rejectedDoctor;
    private DoctorDto pendingDoctorDto;
    private DoctorDto approvedDoctorDto;
    private DoctorDto rejectedDoctorDto;
    private User testUser;
    private InvitationCode testInvitationCode;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("drsmith");
        testUser.setEmail("drsmith@example.com");
        testUser.setPhoneNumber("1234567890");
        testUser.setRole(UserRole.PENDING_DOCTOR);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Setup test doctors with different statuses
        pendingDoctor = new Doctor();
        pendingDoctor.setId(1L);
        pendingDoctor.setUser(testUser);
        pendingDoctor.setSpecialization("Cardiology");
        pendingDoctor.setLicenceNumber("MED12345");
        pendingDoctor.setStatus(DoctorStatus.PENDING);
        pendingDoctor.setCreatedAt(LocalDateTime.now());
        pendingDoctor.setUpdatedAt(LocalDateTime.now());

        approvedDoctor = new Doctor();
        approvedDoctor.setId(2L);
        approvedDoctor.setUser(testUser);
        approvedDoctor.setSpecialization("Neurology");
        approvedDoctor.setLicenceNumber("MED67890");
        approvedDoctor.setStatus(DoctorStatus.APPROVED);
        approvedDoctor.setCreatedAt(LocalDateTime.now());
        approvedDoctor.setUpdatedAt(LocalDateTime.now());

        rejectedDoctor = new Doctor();
        rejectedDoctor.setId(3L);
        rejectedDoctor.setUser(testUser);
        rejectedDoctor.setSpecialization("Dermatology");
        rejectedDoctor.setLicenceNumber("MED54321");
        rejectedDoctor.setStatus(DoctorStatus.REJECTED);
        rejectedDoctor.setCreatedAt(LocalDateTime.now());
        rejectedDoctor.setUpdatedAt(LocalDateTime.now());

        // Setup test DTOs
        pendingDoctorDto = new DoctorDto();
        pendingDoctorDto.setId(1L);
        pendingDoctorDto.setUsername("drsmith");
        pendingDoctorDto.setEmail("drsmith@example.com");
        pendingDoctorDto.setPhoneNumber("1234567890");
        pendingDoctorDto.setSpecialization("Cardiology");
        pendingDoctorDto.setLicenceNumber("MED12345");
        pendingDoctorDto.setStatus(DoctorStatus.PENDING);
        pendingDoctorDto.setCreatedAt(pendingDoctor.getCreatedAt());
        pendingDoctorDto.setUpdatedAt(pendingDoctor.getUpdatedAt());

        approvedDoctorDto = new DoctorDto();
        approvedDoctorDto.setId(2L);
        approvedDoctorDto.setUsername("drsmith");
        approvedDoctorDto.setEmail("drsmith@example.com");
        approvedDoctorDto.setPhoneNumber("1234567890");
        approvedDoctorDto.setSpecialization("Neurology");
        approvedDoctorDto.setLicenceNumber("MED67890");
        approvedDoctorDto.setStatus(DoctorStatus.APPROVED);
        approvedDoctorDto.setCreatedAt(approvedDoctor.getCreatedAt());
        approvedDoctorDto.setUpdatedAt(approvedDoctor.getUpdatedAt());

        rejectedDoctorDto = new DoctorDto();
        rejectedDoctorDto.setId(3L);
        rejectedDoctorDto.setUsername("drsmith");
        rejectedDoctorDto.setEmail("drsmith@example.com");
        rejectedDoctorDto.setPhoneNumber("1234567890");
        rejectedDoctorDto.setSpecialization("Dermatology");
        rejectedDoctorDto.setLicenceNumber("MED54321");
        rejectedDoctorDto.setStatus(DoctorStatus.REJECTED);
        rejectedDoctorDto.setCreatedAt(rejectedDoctor.getCreatedAt());
        rejectedDoctorDto.setUpdatedAt(rejectedDoctor.getUpdatedAt());

        // Setup test invitation code
        testInvitationCode = new InvitationCode();
        testInvitationCode.setId(1L);
        testInvitationCode.setCode("ABC12345");
        testInvitationCode.setDoctor(pendingDoctor);
        testInvitationCode.setUsed(false);
        testInvitationCode.setExpirationDate(LocalDateTime.now().plusDays(7));
        testInvitationCode.setCreatedAt(LocalDateTime.now());

        // Setup mocks
        when(doctorMapper.toDto(pendingDoctor)).thenReturn(pendingDoctorDto);
        when(doctorMapper.toDto(approvedDoctor)).thenReturn(approvedDoctorDto);
        when(doctorMapper.toDto(rejectedDoctor)).thenReturn(rejectedDoctorDto);
    }

    /**
     * Test getting doctors by status (PENDING).
     * Should return 200 OK with a list of pending doctors.
     */
    @Test
    public void testGetDoctorsByStatus_Pending() {
        // Setup
        List<Doctor> pendingDoctors = Arrays.asList(pendingDoctor);
        when(doctorService.getDoctorsByStatus("PENDING")).thenReturn(pendingDoctors);

        var response = adminController.getDoctorsByStatus("PENDING");

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().size() == 1;
        assert response.getBody().get(0).getId().equals(1L);
        assert response.getBody().get(0).getStatus() == DoctorStatus.PENDING;

        // Verify service was called
        verify(doctorService, times(1)).getDoctorsByStatus("PENDING");
    }

    /**
     * Test getting doctors by status (APPROVED).
     * Should return 200 OK with a list of approved doctors.
     */
    @Test
    public void testGetDoctorsByStatus_Approved() {
        // Setup
        List<Doctor> approvedDoctors = Arrays.asList(approvedDoctor);
        when(doctorService.getDoctorsByStatus("APPROVED")).thenReturn(approvedDoctors);

        var response = adminController.getDoctorsByStatus("APPROVED");

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().size() == 1;
        assert response.getBody().get(0).getId().equals(2L);
        assert response.getBody().get(0).getStatus() == DoctorStatus.APPROVED;

        // Verify service was called
        verify(doctorService, times(1)).getDoctorsByStatus("APPROVED");
    }

    /**
     * Test getting a doctor by ID.
     * Should return 200 OK with the doctor details.
     */
    @Test
    public void testGetDoctorById_Success() {
        // Setup
        when(doctorService.getDoctorById(1L)).thenReturn(pendingDoctor);

        var response = adminController.getDoctorById(1L);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getId().equals(1L);
        assert response.getBody().getStatus() == DoctorStatus.PENDING;

        // Verify service was called
        verify(doctorService, times(1)).getDoctorById(1L);
    }

    /**
     * Test getting a doctor by ID that doesn't exist.
     * Should return 404 Not Found.
     */
    @Test
    public void testGetDoctorById_NotFound() {
        // Setup
        when(doctorService.getDoctorById(999L)).thenThrow(new RuntimeException("Doctor not found"));

        var response = adminController.getDoctorById(999L);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.NOT_FOUND;
        assert response.getBody() == null;

        // Verify service was called
        verify(doctorService, times(1)).getDoctorById(999L);
    }

    /**
     * Test approving a pending doctor.
     * Should return 200 OK with the updated doctor details.
     */
    @Test
    public void testApproveDoctor_Success() {
        // Setup
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(1L);
        updatedDoctor.setUser(testUser);
        updatedDoctor.setSpecialization("Cardiology");
        updatedDoctor.setLicenceNumber("MED12345");
        updatedDoctor.setStatus(DoctorStatus.APPROVED);
        updatedDoctor.setCreatedAt(pendingDoctor.getCreatedAt());
        updatedDoctor.setUpdatedAt(LocalDateTime.now());

        DoctorDto updatedDoctorDto = new DoctorDto();
        updatedDoctorDto.setId(1L);
        updatedDoctorDto.setUsername("drsmith");
        updatedDoctorDto.setEmail("drsmith@example.com");
        updatedDoctorDto.setPhoneNumber("1234567890");
        updatedDoctorDto.setSpecialization("Cardiology");
        updatedDoctorDto.setLicenceNumber("MED12345");
        updatedDoctorDto.setStatus(DoctorStatus.APPROVED);
        updatedDoctorDto.setCreatedAt(updatedDoctor.getCreatedAt());
        updatedDoctorDto.setUpdatedAt(updatedDoctor.getUpdatedAt());

        when(doctorService.approveDoctor(1L)).thenReturn(updatedDoctor);
        when(doctorMapper.toDto(updatedDoctor)).thenReturn(updatedDoctorDto);

        var response = adminController.approveDoctor(1L);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getId().equals(1L);
        assert response.getBody().getStatus() == DoctorStatus.APPROVED;

        // Verify service was called
        verify(doctorService, times(1)).approveDoctor(1L);
    }

    /**
     * Test approving a doctor that is not in PENDING status.
     * Should return 409 Conflict.
     */
    @Test
    public void testApproveDoctor_NotPending() {
        // Setup
        when(doctorService.approveDoctor(2L)).thenThrow(new IllegalStateException("Doctor is not in PENDING status"));

        var response = adminController.approveDoctor(2L);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.CONFLICT;
        assert response.getBody() == null;

        // Verify service was called
        verify(doctorService, times(1)).approveDoctor(2L);
    }

    /**
     * Test rejecting a pending doctor.
     * Should return 200 OK with the updated doctor details.
     */
    @Test
    public void testRejectDoctor_Success() {
        // Setup
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(1L);
        updatedDoctor.setUser(testUser);
        updatedDoctor.setSpecialization("Cardiology");
        updatedDoctor.setLicenceNumber("MED12345");
        updatedDoctor.setStatus(DoctorStatus.REJECTED);
        updatedDoctor.setCreatedAt(pendingDoctor.getCreatedAt());
        updatedDoctor.setUpdatedAt(LocalDateTime.now());

        DoctorDto updatedDoctorDto = new DoctorDto();
        updatedDoctorDto.setId(1L);
        updatedDoctorDto.setUsername("drsmith");
        updatedDoctorDto.setEmail("drsmith@example.com");
        updatedDoctorDto.setPhoneNumber("1234567890");
        updatedDoctorDto.setSpecialization("Cardiology");
        updatedDoctorDto.setLicenceNumber("MED12345");
        updatedDoctorDto.setStatus(DoctorStatus.REJECTED);
        updatedDoctorDto.setCreatedAt(updatedDoctor.getCreatedAt());
        updatedDoctorDto.setUpdatedAt(updatedDoctor.getUpdatedAt());

        when(doctorService.rejectDoctor(eq(1L), anyString())).thenReturn(updatedDoctor);
        when(doctorMapper.toDto(updatedDoctor)).thenReturn(updatedDoctorDto);

        var response = adminController.rejectDoctor(1L, "Not qualified");

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getId().equals(1L);
        assert response.getBody().getStatus() == DoctorStatus.REJECTED;

        // Verify service was called
        verify(doctorService, times(1)).rejectDoctor(eq(1L), eq("Not qualified"));
    }

    /**
     * Test generating a new invitation code.
     * Should return 200 OK with the invitation code.
     */
    @Test
    public void testGenerateInvitationCode_Success() {
        // Setup
        when(doctorService.generateInvitationCode(eq(1L), anyInt())).thenReturn(testInvitationCode);

        var response = adminController.generateInvitationCode(1L, 7);

        // Verify the response
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().equals("ABC12345");

        // Verify service was called
        verify(doctorService, times(1)).generateInvitationCode(eq(1L), eq(7));
    }
}
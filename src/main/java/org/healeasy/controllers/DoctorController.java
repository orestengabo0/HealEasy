package org.healeasy.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.healeasy.DTOs.DoctorDto;
import org.healeasy.DTOs.DoctorRegistrationCompletionRequest;
import org.healeasy.DTOs.DoctorRegistrationRequest;
import org.healeasy.Iservices.IDoctorService;
import org.healeasy.entities.Doctor;
import org.healeasy.exceptions.FailedToUploadImageException;
import org.healeasy.exceptions.RequestSizeExceededException;
import org.healeasy.mappers.DoctorMapper;
import org.healeasy.services.CloudinaryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller for doctor-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/doctors")
@AllArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final CloudinaryServiceImpl cloudinaryService;

    /**
     * Register a new doctor with pending status.
     * 
     * @param registrationRequest The doctor registration request
     * @param request The HTTP request
     * @return The created doctor
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorDto> registerDoctor(
            @Valid @ModelAttribute DoctorRegistrationRequest registrationRequest,
            HttpServletRequest request) {

        // Check request size
        if (request.getContentLengthLong() > cloudinaryService.extractMBsFromStrSize(
                cloudinaryService.getMaxRequestSize()
        )) {
            throw new RequestSizeExceededException("Request size exceeded limit.");
        }

        // Check if license number is already in use
        if (doctorService.isLicenseNumberInUse(registrationRequest.getLicenceNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        try {
            Doctor doctor = doctorService.registerDoctor(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(doctorMapper.toDto(doctor));
        } catch (FailedToUploadImageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Upload a document for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param documentType The type of document (license or ID)
     * @param file The document file
     * @return The URL of the uploaded document
     */
    @PostMapping(value = "/{doctorId}/documents/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long doctorId,
            @PathVariable String documentType,
            @RequestParam("file") MultipartFile file) {

        try {
            String documentUrl = doctorService.uploadDocument(doctorId, documentType, file);
            return ResponseEntity.ok(documentUrl);
        } catch (FailedToUploadImageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload document: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Get a doctor by ID.
     * 
     * @param doctorId The ID of the doctor
     * @return The doctor with the given ID
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            return ResponseEntity.ok(doctorMapper.toDto(doctor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * Validate an invitation code.
     * 
     * @param code The invitation code to validate
     * @return The doctor associated with the code if valid
     */
    @GetMapping("/validate-invitation")
    public ResponseEntity<DoctorDto> validateInvitationCode(@RequestParam String code) {
        Doctor doctor = doctorService.validateInvitationCode(code);
        if (doctor != null) {
            return ResponseEntity.ok(doctorMapper.toDto(doctor));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Complete a doctor's registration using an invitation code.
     * 
     * @param completionRequest The registration completion request
     * @param request The HTTP request
     * @return The updated doctor
     */
    @PostMapping(value = "/complete-registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorDto> completeRegistration(
            @Valid @ModelAttribute DoctorRegistrationCompletionRequest completionRequest,
            HttpServletRequest request) {

        // Check request size
        if (request.getContentLengthLong() > cloudinaryService.extractMBsFromStrSize(
                cloudinaryService.getMaxRequestSize()
        )) {
            throw new RequestSizeExceededException("Request size exceeded limit.");
        }

        // Validate password confirmation
        if (!completionRequest.getPassword().equals(completionRequest.getPasswordConfirmation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        try {
            Doctor doctor = doctorService.completeRegistration(
                    completionRequest.getInvitationCode(),
                    completionRequest.getPassword(),
                    completionRequest.getProfilePhoto()
            );
            return ResponseEntity.ok(doctorMapper.toDto(doctor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

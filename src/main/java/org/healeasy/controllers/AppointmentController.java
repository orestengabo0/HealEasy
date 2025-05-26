package org.healeasy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.healeasy.DTOs.AppointmentRequestDto;
import org.healeasy.DTOs.AppointmentResponseDto;
import org.healeasy.Iservices.IAppointmentService;
import org.healeasy.entities.Appointment;
import org.healeasy.enums.AppointmentStatus;
import org.healeasy.mappers.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentController(IAppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping
    @Operation(summary = "Schedule a new appointment", description = "Creates a new appointment with Zoom meeting")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> scheduleAppointment(@Valid @RequestBody AppointmentRequestDto requestDto) {
        Appointment appointment = appointmentService.scheduleAppointment(
                requestDto.getDoctorId(),
                requestDto.getPatientId(),
                requestDto.getScheduleTime(),
                requestDto.getDurationMinutes(),
                requestDto.getDescription()
        );
        
        return new ResponseEntity<>(appointmentMapper.toDto(appointment), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieves an appointment by its ID")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointmentMapper.toDto(appointment));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor", description = "Retrieves all appointments for a doctor")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        List<AppointmentResponseDto> responseDtos = appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient", description = "Retrieves all appointments for a patient")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        List<AppointmentResponseDto> responseDtos = appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/doctor/{doctorId}/upcoming")
    @Operation(summary = "Get upcoming appointments for doctor", description = "Retrieves all upcoming appointments for a doctor")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getUpcomingAppointmentsForDoctor(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getUpcomingAppointmentsForDoctor(doctorId);
        List<AppointmentResponseDto> responseDtos = appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/patient/{patientId}/upcoming")
    @Operation(summary = "Get upcoming appointments for patient", description = "Retrieves all upcoming appointments for a patient")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getUpcomingAppointmentsForPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getUpcomingAppointmentsForPatient(patientId);
        List<AppointmentResponseDto> responseDtos = appointments.stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseDtos);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Updates the status of an appointment")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(appointmentMapper.toDto(appointment));
    }

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule appointment", description = "Reschedules an appointment to a new time")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam LocalDateTime newScheduleTime,
            @RequestParam(required = false) Integer newDurationMinutes) {
        Appointment appointment = appointmentService.rescheduleAppointment(id, newScheduleTime, newDurationMinutes);
        return ResponseEntity.ok(appointmentMapper.toDto(appointment));
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancels an appointment")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(appointmentMapper.toDto(appointment));
    }
}
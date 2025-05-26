package org.healeasy.services;

import org.healeasy.Iservices.IZoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomServiceImpl implements IZoomService {

    @Value("${zoom.account-id}")
    private String accountId;

    @Value("${zoom.client-id}")
    private String clientId;

    @Value("${zoom.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private static final String ZOOM_API_BASE_URL = "https://api.zoom.us/v2";

    public ZoomServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Map<String, String> createMeeting(String topic, LocalDateTime startTime, Integer durationMinutes, String description) {
        String url = ZOOM_API_BASE_URL + "/users/me/meetings";
        
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("topic", topic);
        requestBody.put("type", 2); // Scheduled meeting
        requestBody.put("start_time", formatZoomDateTime(startTime));
        requestBody.put("duration", durationMinutes);
        requestBody.put("timezone", "UTC");
        requestBody.put("agenda", description);
        
        // Set meeting settings
        Map<String, Object> settings = new HashMap<>();
        settings.put("host_video", true);
        settings.put("participant_video", true);
        settings.put("join_before_host", true);
        settings.put("mute_upon_entry", false);
        settings.put("auto_recording", "none");
        requestBody.put("settings", settings);
        
        // Make API call
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        
        // Extract and return meeting details
        Map<String, String> meetingDetails = new HashMap<>();
        if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            meetingDetails.put("id", responseBody.get("id").toString());
            meetingDetails.put("join_url", responseBody.get("join_url").toString());
            meetingDetails.put("start_url", responseBody.get("start_url").toString());
            meetingDetails.put("password", responseBody.get("password").toString());
        }
        
        return meetingDetails;
    }

    @Override
    public Map<String, String> getMeeting(String meetingId) {
        String url = ZOOM_API_BASE_URL + "/meetings/" + meetingId;
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
        
        Map<String, String> meetingDetails = new HashMap<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            meetingDetails.put("id", responseBody.get("id").toString());
            meetingDetails.put("topic", responseBody.get("topic").toString());
            meetingDetails.put("join_url", responseBody.get("join_url").toString());
            meetingDetails.put("start_url", responseBody.get("start_url").toString());
            meetingDetails.put("password", responseBody.get("password").toString());
        }
        
        return meetingDetails;
    }

    @Override
    public Map<String, String> updateMeeting(String meetingId, String topic, LocalDateTime startTime, Integer durationMinutes, String description) {
        String url = ZOOM_API_BASE_URL + "/meetings/" + meetingId;
        
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("topic", topic);
        requestBody.put("start_time", formatZoomDateTime(startTime));
        requestBody.put("duration", durationMinutes);
        requestBody.put("agenda", description);
        
        // Make API call
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, createHeaders());
        restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);
        
        // Return updated meeting details
        return getMeeting(meetingId);
    }

    @Override
    public boolean deleteMeeting(String meetingId) {
        String url = ZOOM_API_BASE_URL + "/meetings/" + meetingId;
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
        
        return response.getStatusCode() == HttpStatus.NO_CONTENT;
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create authorization header with JWT token
        String token = generateJwtToken();
        headers.setBearerAuth(token);
        
        return headers;
    }
    
    private String generateJwtToken() {
        // For simplicity, we're using Server-to-Server OAuth
        // In a production environment, you might want to use a JWT library
        String authString = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + authString);
        
        HttpEntity<String> request = new HttpEntity<>("grant_type=account_credentials&account_id=" + accountId, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            "https://zoom.us/oauth/token", 
            HttpMethod.POST, 
            request, 
            Map.class
        );
        
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            return responseBody.get("access_token").toString();
        }
        
        throw new RuntimeException("Failed to generate Zoom JWT token");
    }
    
    private String formatZoomDateTime(LocalDateTime dateTime) {
        return dateTime.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
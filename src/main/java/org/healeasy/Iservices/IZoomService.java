package org.healeasy.Iservices;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service interface for Zoom meeting operations
 */
public interface IZoomService {
    
    /**
     * Create a Zoom meeting
     * 
     * @param topic Meeting topic
     * @param startTime Meeting start time
     * @param durationMinutes Meeting duration in minutes
     * @param description Meeting description
     * @return Map containing meeting details (id, join_url, start_url, password)
     */
    Map<String, String> createMeeting(String topic, LocalDateTime startTime, Integer durationMinutes, String description);
    
    /**
     * Get details of a Zoom meeting
     * 
     * @param meetingId Zoom meeting ID
     * @return Map containing meeting details
     */
    Map<String, String> getMeeting(String meetingId);
    
    /**
     * Update a Zoom meeting
     * 
     * @param meetingId Zoom meeting ID
     * @param topic Meeting topic
     * @param startTime Meeting start time
     * @param durationMinutes Meeting duration in minutes
     * @param description Meeting description
     * @return Map containing updated meeting details
     */
    Map<String, String> updateMeeting(String meetingId, String topic, LocalDateTime startTime, Integer durationMinutes, String description);
    
    /**
     * Delete a Zoom meeting
     * 
     * @param meetingId Zoom meeting ID
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteMeeting(String meetingId);
}
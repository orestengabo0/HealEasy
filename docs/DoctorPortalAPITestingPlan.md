# Testing Plan for Doctor Portal API

This document outlines a comprehensive testing plan for the Doctor Portal API, focusing on the doctor recruitment workflow.

## 1. API Endpoints to Test

### Doctor Controller Endpoints
1. `POST /api/v1/doctors/register` - Doctor pre-registration
2. `POST /api/v1/doctors/{doctorId}/documents/{documentType}` - Upload documents
3. `GET /api/v1/doctors/{doctorId}` - Get doctor by ID
4. `GET /api/v1/doctors/validate-invitation` - Validate invitation code
5. `POST /api/v1/doctors/complete-registration` - Complete registration

### Admin Controller Endpoints
1. `GET /api/v1/admin/doctors` - Get doctors by status
2. `GET /api/v1/admin/doctors/{doctorId}` - Get doctor by ID
3. `POST /api/v1/admin/doctors/{doctorId}/approve` - Approve doctor
4. `POST /api/v1/admin/doctors/{doctorId}/reject` - Reject doctor
5. `POST /api/v1/admin/doctors/{doctorId}/invitation-code` - Generate invitation code

## 2. Testing Tools and Environment

### Recommended Tools
1. **Postman** - For manual API testing and creating collections
2. **JUnit** with **Spring Boot Test** - For automated unit and integration tests
3. **MockMvc** - For testing Spring MVC controllers
4. **Mockito** - For mocking dependencies in unit tests
5. **H2 Database** - For in-memory database testing
6. **Testcontainers** - For integration tests with real database instances

### Testing Environment
- Development environment with test database
- Test users with different roles (ADMIN, DOCTOR, PENDING_DOCTOR)
- Sample files for document uploads (PDF, JPG, etc.)

## 3. Test Data Requirements

### Doctor Registration
- Valid and invalid usernames, emails, passwords, phone numbers
- Valid and invalid specializations and license numbers
- Sample documents for license and ID (PDF, JPG, PNG)

### Admin Operations
- List of doctor IDs with different statuses (PENDING, APPROVED, REJECTED)
- Valid and invalid reasons for rejection

### Registration Completion
- Valid and invalid invitation codes
- Valid and invalid passwords
- Sample profile photos

## 4. Test Cases

### 4.1 Doctor Pre-Registration (POST /api/v1/doctors/register)

#### Happy Path Tests
1. **Valid Registration** - Register with all valid fields and documents
   - Expected: 201 Created with doctor details
   - Verify: Doctor created with PENDING status, User created with PENDING_DOCTOR role

2. **Valid Registration Without Documents** - Register with valid fields but no documents
   - Expected: 201 Created with doctor details
   - Verify: Doctor created with PENDING status, document URLs are null

#### Error Path Tests
1. **Duplicate Email** - Register with an email that already exists
   - Expected: 409 Conflict or appropriate error response

2. **Duplicate Phone Number** - Register with a phone number that already exists
   - Expected: 409 Conflict or appropriate error response

3. **Duplicate License Number** - Register with a license number that already exists
   - Expected: 409 Conflict or appropriate error response

4. **Invalid Username** - Register with username that doesn't meet requirements
   - Expected: 400 Bad Request with validation error

5. **Invalid Email** - Register with invalid email format
   - Expected: 400 Bad Request with validation error

6. **Invalid Password** - Register with password that doesn't meet requirements
   - Expected: 400 Bad Request with validation error

7. **Invalid Phone Number** - Register with phone number that doesn't meet requirements
   - Expected: 400 Bad Request with validation error

8. **Missing Required Fields** - Register with missing required fields
   - Expected: 400 Bad Request with validation errors

9. **Large Document Files** - Register with document files exceeding size limit
   - Expected: 413 Request Entity Too Large or appropriate error

### 4.2 Document Upload (POST /api/v1/doctors/{doctorId}/documents/{documentType})

#### Happy Path Tests
1. **Upload License Document** - Upload valid license document
   - Expected: 200 OK with document URL
   - Verify: Doctor's licenseDocumentUrl is updated

2. **Upload ID Document** - Upload valid ID document
   - Expected: 200 OK with document URL
   - Verify: Doctor's idDocumentUrl is updated

#### Error Path Tests
1. **Invalid Doctor ID** - Upload document for non-existent doctor
   - Expected: 404 Not Found

2. **Invalid Document Type** - Upload with invalid document type
   - Expected: 400 Bad Request

3. **Large File** - Upload file exceeding size limit
   - Expected: 413 Request Entity Too Large or appropriate error

4. **Unsupported File Type** - Upload file with unsupported format
   - Expected: 400 Bad Request or appropriate error

### 4.3 Admin Approval (POST /api/v1/admin/doctors/{doctorId}/approve)

#### Happy Path Tests
1. **Approve Pending Doctor** - Approve doctor with PENDING status
   - Expected: 200 OK with updated doctor details
   - Verify: Doctor status changed to APPROVED, invitation code generated, email sent

#### Error Path Tests
1. **Approve Non-Pending Doctor** - Approve doctor with non-PENDING status
   - Expected: 409 Conflict

2. **Invalid Doctor ID** - Approve non-existent doctor
   - Expected: 404 Not Found

3. **Unauthorized Access** - Non-admin user tries to approve
   - Expected: 403 Forbidden

### 4.4 Invitation Code Validation (GET /api/v1/doctors/validate-invitation)

#### Happy Path Tests
1. **Valid Invitation Code** - Validate a valid, non-expired, unused code
   - Expected: 200 OK with doctor details

#### Error Path Tests
1. **Invalid Code** - Validate non-existent code
   - Expected: 404 Not Found

2. **Expired Code** - Validate expired code
   - Expected: 404 Not Found or appropriate error

3. **Used Code** - Validate already used code
   - Expected: 404 Not Found or appropriate error

### 4.5 Registration Completion (POST /api/v1/doctors/complete-registration)

#### Happy Path Tests
1. **Complete Registration** - Complete with valid code, password, and profile photo
   - Expected: 200 OK with updated doctor details
   - Verify: User role changed to DOCTOR, invitation code marked as used

2. **Complete Without Photo** - Complete with valid code and password, no photo
   - Expected: 200 OK with updated doctor details
   - Verify: Default profile image remains

#### Error Path Tests
1. **Invalid Code** - Complete with invalid invitation code
   - Expected: 400 Bad Request

2. **Expired Code** - Complete with expired invitation code
   - Expected: 400 Bad Request or appropriate error

3. **Used Code** - Complete with already used invitation code
   - Expected: 400 Bad Request or appropriate error

4. **Password Mismatch** - Complete with non-matching passwords
   - Expected: 400 Bad Request

5. **Invalid Password** - Complete with password that doesn't meet requirements
   - Expected: 400 Bad Request with validation error

6. **Large Profile Photo** - Complete with profile photo exceeding size limit
   - Expected: 413 Request Entity Too Large or appropriate error

## 5. Integration Testing

### Test Scenarios
1. **Full Registration Flow** - Test the entire flow from registration to approval to completion
   - Register a new doctor
   - Admin approves the doctor
   - Validate the invitation code
   - Complete registration
   - Verify final doctor status and user role

2. **Document Upload Flow** - Test document upload during and after registration
   - Register without documents
   - Upload documents separately
   - Verify document URLs

3. **Rejection Flow** - Test the rejection process
   - Register a new doctor
   - Admin rejects the doctor
   - Verify doctor status

### Database Integration
- Test that all entities are properly saved to the database
- Test relationships between User, Doctor, and InvitationCode entities
- Test database constraints and validations

## 6. Example API Requests

### Doctor Registration
```
POST /api/v1/doctors/register
Content-Type: multipart/form-data

username: drsmith
email: drsmith@example.com
password: Password@123
phoneNumber: 1234567890
specialization: Cardiology
licenceNumber: MED12345
licenseDocument: [file upload]
idDocument: [file upload]
```

### Admin Approval
```
POST /api/v1/admin/doctors/1/approve
Authorization: Bearer [admin_token]
```

### Validate Invitation Code
```
GET /api/v1/doctors/validate-invitation?code=ABC12345
```

### Complete Registration
```
POST /api/v1/doctors/complete-registration
Content-Type: multipart/form-data

invitationCode: ABC12345
password: NewPassword@123
passwordConfirmation: NewPassword@123
profilePhoto: [file upload]
```

## 7. Automated Testing Approach

### Unit Tests
- Test individual service methods with mocked dependencies
- Test validation logic in DTOs
- Test controller methods using MockMvc

### Integration Tests
- Test the entire workflow with real or in-memory database
- Test email sending functionality
- Test file upload functionality

### Example Test Class Structure
```java
@SpringBootTest
@AutoConfigureMockMvc
public class DoctorRegistrationFlowTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private InvitationCodeRepository invitationCodeRepository;
    
    @Test
    public void testFullRegistrationFlow() {
        // 1. Register doctor
        // 2. Admin approves
        // 3. Validate invitation code
        // 4. Complete registration
        // 5. Verify final state
    }
}
```

## 8. Security Testing

- Test authentication requirements for each endpoint
- Test authorization (role-based access control)
- Test input validation and sanitization
- Test for common security vulnerabilities (SQL injection, XSS, etc.)

## 9. Performance Testing

- Test with large file uploads
- Test with concurrent requests
- Test database query performance
- Test email sending performance

## 10. Monitoring and Logging

- Verify that appropriate logs are generated for each operation
- Verify that errors are properly logged
- Verify that sensitive information is not logged

## 11. Conclusion

This testing plan provides a comprehensive approach to testing the Doctor Portal API. By following this plan, you can ensure that the doctor recruitment workflow functions correctly, handles errors appropriately, and provides a good user experience.
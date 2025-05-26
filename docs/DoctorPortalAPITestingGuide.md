# Doctor Portal API Testing Guide

This guide provides a comprehensive approach to testing the Doctor Portal API for the doctor recruitment workflow. It includes different testing approaches, tools, and examples to ensure the API functions correctly.

## 1. Overview of the Doctor Recruitment Workflow

The doctor recruitment workflow consists of the following steps:

1. **Doctor Pre-Registration**
   - Doctor submits basic info, professional details, and supporting documents
   - System creates a PENDING_DOCTOR record

2. **Admin Review & Approval**
   - Admin reviews the doctor's application
   - Admin approves or rejects the application
   - If approved, system generates an invitation code and sends it to the doctor's email

3. **Doctor Completes Registration**
   - Doctor validates the invitation code
   - Doctor sets a password and uploads a profile photo
   - System activates the doctor's account (changes role from PENDING_DOCTOR to DOCTOR)

## 2. API Endpoints to Test

### Doctor Controller Endpoints
- `POST /api/v1/doctors/register` - Doctor pre-registration
- `POST /api/v1/doctors/{doctorId}/documents/{documentType}` - Upload documents
- `GET /api/v1/doctors/{doctorId}` - Get doctor by ID
- `GET /api/v1/doctors/validate-invitation` - Validate invitation code
- `POST /api/v1/doctors/complete-registration` - Complete registration

### Admin Controller Endpoints
- `GET /api/v1/admin/doctors` - Get doctors by status
- `GET /api/v1/admin/doctors/{doctorId}` - Get doctor by ID
- `POST /api/v1/admin/doctors/{doctorId}/approve` - Approve doctor
- `POST /api/v1/admin/doctors/{doctorId}/reject` - Reject doctor
- `POST /api/v1/admin/doctors/{doctorId}/invitation-code` - Generate invitation code

## 3. Testing Approaches

### 3.1 Unit Testing

Unit tests focus on testing individual components in isolation. For the Doctor Portal API, we should unit test:

- **Controllers**: Test each endpoint with mocked service layer
- **Services**: Test business logic with mocked repositories
- **Repositories**: Test database operations with an in-memory database
- **DTOs**: Test validation logic

Example unit tests have been created for:
- `DoctorControllerTest.java` - Tests the doctor-side endpoints
- `AdminControllerTest.java` - Tests the admin-side endpoints

### 3.2 Integration Testing

Integration tests focus on testing the interaction between components. For the Doctor Portal API, we should test:

- **Full Workflow**: Test the entire doctor recruitment workflow from registration to activation
- **Database Integration**: Test that entities are properly saved and relationships are maintained
- **Email Service Integration**: Test that emails are sent correctly

Example integration tests have been created in:
- `DoctorRegistrationFlowIntegrationTest.java` - Tests the entire doctor recruitment workflow

### 3.3 Manual Testing with Postman

Manual testing with Postman allows for interactive testing of the API. Create a Postman collection with requests for each endpoint and test scenarios.

## 4. Testing Tools

### 4.1 JUnit and Spring Boot Test

JUnit is the standard testing framework for Java applications. Spring Boot Test provides additional testing support for Spring Boot applications.

```java
@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testRegisterDoctor() {
        // Test code
    }
}
```

### 4.2 MockMvc

MockMvc allows for testing Spring MVC controllers without starting a full HTTP server.

```java
mockMvc.perform(post("/api/v1/doctors/register")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .param("username", "drsmith")
        // Other parameters
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
```

### 4.3 Mockito

Mockito is used for mocking dependencies in unit tests.

```java
@Mock
private IDoctorService doctorService;

@Test
public void testApproveDoctor() {
    when(doctorService.approveDoctor(1L)).thenReturn(approvedDoctor);
    // Test code
    verify(doctorService, times(1)).approveDoctor(1L);
}
```

### 4.4 Postman

Postman is a popular tool for manual API testing. Create a collection with requests for each endpoint and test scenarios.

## 5. Test Data

### 5.1 Test Users

Create test users with different roles:
- Admin user for testing admin endpoints
- Doctor user for testing doctor endpoints
- Pending doctor user for testing the registration flow

### 5.2 Test Files

Create test files for document uploads:
- License document (PDF)
- ID document (JPG/PNG)
- Profile photo (JPG/PNG)

### 5.3 Test Database

Use an in-memory database (H2) for testing to avoid affecting the production database.

## 6. Test Cases

### 6.1 Happy Path Tests

Test the normal flow of the doctor recruitment workflow:
1. Doctor registers successfully
2. Admin approves the doctor
3. Doctor validates the invitation code
4. Doctor completes registration
5. Doctor's account is activated

### 6.2 Error Path Tests

Test error scenarios:
1. Doctor registration with invalid data
2. Doctor registration with duplicate email/phone/license
3. Admin approval of non-existent doctor
4. Admin approval of already approved doctor
5. Validation of invalid invitation code
6. Registration completion with invalid invitation code
7. Registration completion with password mismatch

## 7. Example Test Implementations

### 7.1 Unit Test for DoctorController

See `DoctorControllerTest.java` for a complete example of unit testing the doctor-side endpoints.

Key points:
- Mock the service layer
- Test both happy path and error scenarios
- Verify that the service methods are called with the correct parameters
- Verify the response status and body

### 7.2 Unit Test for AdminController

See `AdminControllerTest.java` for a complete example of unit testing the admin-side endpoints.

Key points:
- Mock the service layer
- Test both happy path and error scenarios
- Verify that the service methods are called with the correct parameters
- Verify the response status and body

### 7.3 Integration Test for the Full Workflow

See `DoctorRegistrationFlowIntegrationTest.java` for a complete example of integration testing the entire doctor recruitment workflow.

Key points:
- Use `@SpringBootTest` to load the application context
- Use `@Transactional` to ensure each test runs in its own transaction
- Test the entire workflow from registration to activation
- Verify the database state at each step
- Test both happy path and error scenarios

## 8. Continuous Integration

Integrate the tests into your CI/CD pipeline to ensure that the API continues to function correctly as changes are made.

### 8.1 GitHub Actions

Create a GitHub Actions workflow to run the tests on every push and pull request.

```yaml
name: API Tests

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    - name: Build and test
      run: mvn test
```

### 8.2 Test Reports

Generate test reports to track test coverage and results.

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 9. Best Practices

1. **Test Isolation**: Each test should be independent and not rely on the state from other tests.
2. **Test Coverage**: Aim for high test coverage, especially for critical paths.
3. **Test Data**: Use realistic test data that covers a variety of scenarios.
4. **Test Performance**: Ensure that tests run quickly to facilitate rapid development.
5. **Test Maintenance**: Keep tests up to date as the API evolves.

## 10. Conclusion

Testing the Doctor Portal API requires a combination of unit tests, integration tests, and manual testing. By following the approaches and examples in this guide, you can ensure that the API functions correctly and continues to do so as changes are made.

For more detailed information, refer to:
- `DoctorPortalAPITestingPlan.md` - Comprehensive testing plan
- `DoctorControllerTest.java` - Unit tests for doctor-side endpoints
- `AdminControllerTest.java` - Unit tests for admin-side endpoints
- `DoctorRegistrationFlowIntegrationTest.java` - Integration tests for the full workflow
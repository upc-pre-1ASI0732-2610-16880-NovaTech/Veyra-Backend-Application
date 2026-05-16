package com.novaperutech.veyra.platform;

import com.novaperutech.veyra.platform.payments.infrastructure.persistence.stripe.StripeServiceImpl;
import com.novaperutech.veyra.platform.profiles.infrastructure.storage.cloudinary.CloudinaryStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CoreIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeServiceImpl stripeServiceImpl;

    @MockitoBean
    private CloudinaryStorageService cloudinaryStorageService;

    private void mockCloudinaryUpload() {
        when(cloudinaryStorageService.upload(any(byte[].class), anyString()))
                .thenReturn(Map.of(
                        "url", "https://test.cloudinary.com/profile.jpg",
                        "publicId", "test-profile-public-id"
                ));
    }

    // =========================
    // AUTHENTICATION TESTS
    // =========================

    @Test
    void testSuccessfulSignUp() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);

        String requestBody = """
            {
              "username": "testuser_%s",
              "password": "Password123!",
              "roles": ["ROLE_USER"]
            }
        """.formatted(unique);

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username").value("testuser_" + unique));
    }

    @Test
    void testSuccessfulSignIn() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String username = "loginuser_" + unique;

        String signUpBody = """
            {
              "username": "%s",
              "password": "Password123!",
              "roles": ["ROLE_USER"]
            }
        """.formatted(username);

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpBody))
                .andExpect(status().isCreated());

        String signInBody = """
            {
              "username": "%s",
              "password": "Password123!"
            }
        """.formatted(username);

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/residents/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // =========================
    // BUSINESS FLOW TESTS
    // =========================

    @Test
    void testSuccessfulCreateNursingHome() throws Exception {
        Long administratorId = createAdministrator();

        mockCloudinaryUpload();

        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String ruc = generateRuc();

        String requestBody = """
            {
              "businessName": "Casa de Reposo Veyra %s",
              "emailAddress": "home%s@veyra.com",
              "phoneNumber": "999888777",
              "street": "Av. Principal",
              "number": "101",
              "city": "Lima",
              "postalCode": "15001",
              "country": "Peru",
              "photoBase64": "aGVsbG8=",
              "ruc": "%s"
            }
        """.formatted(unique, unique, ruc);

        mockMvc.perform(post("/api/v1/administrators/{administratorId}/nursing-homes", administratorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.administratorId").value(administratorId));
    }

    @Test
    void testSuccessfulCreateResident() throws Exception {
        String token = createAccessToken();

        Long administratorId = createAdministrator();
        Long nursingHomeId = createNursingHome(administratorId);

        mockCloudinaryUpload();

        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String dni = generateDni();

        String requestBody = """
            {
              "dni": "%s",
              "firstName": "Rosa",
              "lastName": "Fernandez",
              "birthDate": "1942-05-20",
              "age": 83,
              "emailAddress": "rosa%s@veyra.com",
              "street": "Calle Bienestar",
              "number": "202",
              "city": "Lima",
              "postalCode": "15003",
              "country": "Peru",
              "photoBase64": "aGVsbG8=",
              "phoneNumber": "955444333",
              "legalRepresentativeFirstName": "Ana",
              "legalRepresentativeLastName": "Fernandez",
              "legalRepresentativePhoneNumber": "944333222",
              "emergencyContactFirstName": "Luis",
              "emergencyContactLastName": "Fernandez",
              "emergencyContactPhoneNumber": "933222111"
            }
        """.formatted(dni, unique);

        mockMvc.perform(post("/api/v1/nursing-homes/{nursingHomeId}/residents", nursingHomeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.legalRepresentativeFirstName").value("Ana"))
                .andExpect(jsonPath("$.emergencyContactFirstName").value("Luis"));
    }

    @Test
    void testSuccessfulCreateMedicationForResident() throws Exception {
        String token = createAccessToken();

        Long administratorId = createAdministrator();
        Long nursingHomeId = createNursingHome(administratorId);
        Long residentId = createResident(nursingHomeId, token);

        String requestBody = """
            {
              "name": "Paracetamol",
              "description": "Medicamento para control de dolor leve",
              "amount": 30,
              "expirationDate": "2027-12-31",
              "drugPresentation": "TABLET",
              "dosage": "500mg cada 8 horas"
            }
        """;

        mockMvc.perform(post("/api/v1/residents/{residentId}/medications", residentId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.residentId").value(residentId))
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.amount").value(30));
    }

    // =========================
    // HELPER METHODS
    // =========================

    private String createAccessToken() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String username = "authuser_" + unique;

        String signUpBody = """
            {
              "username": "%s",
              "password": "Password123!",
              "roles": ["ROLE_USER"]
            }
        """.formatted(username);

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpBody))
                .andExpect(status().isCreated());

        String signInBody = """
            {
              "username": "%s",
              "password": "Password123!"
            }
        """.formatted(username);

        String response = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractString(response, "token");
    }

    private Long createAdministrator() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);

        String requestBody = """
            {
              "username": "admin_%s",
              "password": "Password123!"
            }
        """.formatted(unique);

        String response = mockMvc.perform(post("/api/v1/administrators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractLong(response, "id");
    }

    private Long createNursingHome(Long administratorId) throws Exception {
        mockCloudinaryUpload();

        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String ruc = generateRuc();

        String requestBody = """
            {
              "businessName": "Veyra Nursing Home %s",
              "emailAddress": "nursing%s@veyra.com",
              "phoneNumber": "999888777",
              "street": "Av. Los Cuidadores",
              "number": "123",
              "city": "Lima",
              "postalCode": "15001",
              "country": "Peru",
              "photoBase64": "aGVsbG8=",
              "ruc": "%s"
            }
        """.formatted(unique, unique, ruc);

        String response = mockMvc.perform(post("/api/v1/administrators/{administratorId}/nursing-homes", administratorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.administratorId").value(administratorId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractLong(response, "id");
    }

    private Long createResident(Long nursingHomeId, String token) throws Exception {
        mockCloudinaryUpload();

        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String dni = generateDni();

        String requestBody = """
            {
              "dni": "%s",
              "firstName": "Carlos",
              "lastName": "Ramirez",
              "birthDate": "1945-08-10",
              "age": 80,
              "emailAddress": "resident%s@veyra.com",
              "street": "Calle Salud",
              "number": "456",
              "city": "Lima",
              "postalCode": "15002",
              "country": "Peru",
              "photoBase64": "aGVsbG8=",
              "phoneNumber": "988777666",
              "legalRepresentativeFirstName": "Lucia",
              "legalRepresentativeLastName": "Ramirez",
              "legalRepresentativePhoneNumber": "977666555",
              "emergencyContactFirstName": "Mario",
              "emergencyContactLastName": "Ramirez",
              "emergencyContactPhoneNumber": "966555444"
            }
        """.formatted(dni, unique);

        String response = mockMvc.perform(post("/api/v1/nursing-homes/{nursingHomeId}/residents", nursingHomeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractLong(response, "id");
    }

    private String generateRuc() {
        return "20" + String.valueOf(System.currentTimeMillis()).substring(4, 13);
    }

    private String generateDni() {
        String value = String.valueOf(System.nanoTime()).replaceAll("\\D", "");
        if (value.length() >= 8) {
            return value.substring(0, 8);
        }
        return String.format("%08d", Long.parseLong(value));
    }

    private Long extractLong(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":";
        int start = json.indexOf(pattern);

        if (start == -1) {
            throw new IllegalArgumentException("Field not found in JSON: " + fieldName + ". JSON: " + json);
        }

        start += pattern.length();
        int end = start;

        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }

        return Long.parseLong(json.substring(start, end));
    }

    private String extractString(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":\"";
        int start = json.indexOf(pattern);

        if (start == -1) {
            throw new IllegalArgumentException("Field not found in JSON: " + fieldName + ". JSON: " + json);
        }

        start += pattern.length();
        int end = json.indexOf("\"", start);

        if (end == -1) {
            throw new IllegalArgumentException("Invalid JSON string value for field: " + fieldName + ". JSON: " + json);
        }

        return json.substring(start, end);
    }
}

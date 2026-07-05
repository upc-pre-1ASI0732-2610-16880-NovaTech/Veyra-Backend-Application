package com.novaperutech.veyra.platform.shared.infrastructure.dev;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dev/seed")
@Profile("dev")
public class DevSeedController {

    private final JdbcTemplate jdbc;

    public DevSeedController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping
    public Map<String, Object> seed(@RequestParam(defaultValue = "1") Long userId) {
        jdbc.update("INSERT INTO business_profile (id, business_name, ruc, email_address, phone_number, street, number, city, postal_code, country, photo_public_id, photo_url, created_at, updated_at) VALUES (1, 'Casa Serena Dev', '20123456789', 'info@casaserena.com', '+51987654321', 'Av. Principal', '123', 'Lima', '15001', 'Peru', 'dev_bp', 'https://placeholder.com/bp.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        jdbc.update("INSERT INTO administrator (id, user_id, created_at, updated_at) VALUES (1, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", userId);
        jdbc.update("INSERT INTO nursing_home (id, administrator_id, business_profile_id, created_at, updated_at) VALUES (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        jdbc.update("INSERT INTO resident (id, nursing_home_id, person_profile_id, resident_status, emergency_firstname, emergency_lastname, emergency_phone_number, legal_representative_firstname, legal_representative_lastname, legal_representative_phone_number, created_at, updated_at) VALUES (1, 1, 2, 'ACTIVE', 'Luis', 'García', '+51976543210', 'María', 'García', '+51987654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        jdbc.update("INSERT INTO staff (id, nursing_home_id, person_profile_id, first_name, last_name, phone_number, staff_status, created_at, updated_at) VALUES (1, 1, 3, 'Ana', 'Martínez', '+51912345678', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        return Map.of("nursingHomeId", 1, "residentId", 1, "staffId", 1, "message", "Test data seeded");
    }
}

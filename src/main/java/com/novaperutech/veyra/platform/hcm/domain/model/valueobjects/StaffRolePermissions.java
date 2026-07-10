package com.novaperutech.veyra.platform.hcm.domain.model.valueobjects;

import java.util.List;
import java.util.Map;

/**
 * Maps each {@link StaffRole} to the set of permissions it grants within the platform.
 * Used to inform the caller which permissions were assigned when a role is set on a staff member's contract.
 */
public class StaffRolePermissions {

    private static final Map<StaffRole, List<String>> PERMISSIONS_BY_ROLE = Map.of(
            StaffRole.DOCTOR, List.of("VIEW_RESIDENTS", "MANAGE_MEDICAL_RECORDS", "PRESCRIBE_MEDICATION"),
            StaffRole.NURSE, List.of("VIEW_RESIDENTS", "ADMINISTER_MEDICATION", "UPDATE_VITAL_SIGNS"),
            StaffRole.CAREGIVER, List.of("VIEW_RESIDENTS", "LOG_DAILY_CARE"),
            StaffRole.COOK, List.of("VIEW_DIETARY_RESTRICTIONS"),
            StaffRole.ADMINISTRATIVE, List.of("MANAGE_STAFF", "MANAGE_BILLING", "VIEW_REPORTS")
    );

    private StaffRolePermissions() {}

    public static List<String> permissionsFor(StaffRole staffRole) {
        return PERMISSIONS_BY_ROLE.getOrDefault(staffRole, List.of());
    }
}

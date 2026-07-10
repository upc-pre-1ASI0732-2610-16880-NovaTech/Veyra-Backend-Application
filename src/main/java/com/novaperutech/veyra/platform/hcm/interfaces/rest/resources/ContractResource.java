package com.novaperutech.veyra.platform.hcm.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record ContractResource(Long id,Long staffMemberId,LocalDate startDate, LocalDate endDate, String typeOfContract,
                               String staffRole, String workShift, String status, List<String> permissions) {
}

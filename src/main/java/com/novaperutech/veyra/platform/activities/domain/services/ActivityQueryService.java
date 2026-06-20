package com.novaperutech.veyra.platform.activities.domain.services;

import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivitiesByDateAndNursingHomeQuery;
import com.novaperutech.veyra.platform.activities.domain.model.queries.GetActivityByIdQuery;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityView;

import java.util.List;
import java.util.Optional;

public interface ActivityQueryService {
    List<ActivityView> handle(GetActivitiesByDateAndNursingHomeQuery query);
    Optional<ActivityView> handle(GetActivityByIdQuery query);
}
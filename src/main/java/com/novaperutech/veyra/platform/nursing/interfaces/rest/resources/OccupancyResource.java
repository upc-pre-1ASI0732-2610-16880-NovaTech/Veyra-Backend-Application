package com.novaperutech.veyra.platform.nursing.interfaces.rest.resources;

public record OccupancyResource(Integer totalCapacity, Integer occupiedSlots, Integer availableSlots, Double occupancyRate) {
}

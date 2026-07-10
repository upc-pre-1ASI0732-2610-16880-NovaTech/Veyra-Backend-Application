package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.entities.Room;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.OccupancyResource;

import java.util.List;

public class OccupancyResourceAssembler {
    public static OccupancyResource fromRooms(List<Room> rooms) {
        int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
        int occupiedSlots = rooms.stream().mapToInt(Room::getOccupiedSlots).sum();
        int availableSlots = totalCapacity - occupiedSlots;
        double occupancyRate = totalCapacity == 0 ? 0.0 : (occupiedSlots * 100.0) / totalCapacity;
        return new OccupancyResource(totalCapacity, occupiedSlots, availableSlots, Math.round(occupancyRate * 100.0) / 100.0);
    }
}

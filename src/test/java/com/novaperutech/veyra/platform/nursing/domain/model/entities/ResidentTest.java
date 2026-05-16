package com.novaperutech.veyra.platform.nursing.domain.model.entities;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.NursingHome;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Resident;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.RoomStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResidentTest {

  @Test
  void shouldCreateResidentCorrectly() {

    Resident resident = new Resident(
      1L,
      "Carlos",
      "Perez",
      "999999999",
      "Maria",
      "Perez",
      "888888888"
    );

    assertNotNull(resident);
  }

  @Test
  void shouldOccupyRoomWhenResidentAssigned() {

    NursingHome nursingHome = new NursingHome();

    Room room = new Room(
      nursingHome,
      2,
      "Shared",
      "A-101"
    );

    Resident resident = new Resident(
      1L,
      "Carlos",
      "Perez",
      "999999999",
      "Maria",
      "Perez",
      "888888888"
    );

    resident.activate();

    resident.assignToRoom(room);

    assertEquals(room, resident.getRoom());

    assertEquals(1,
      room.getOccupiedSlots());

    assertEquals(RoomStatus.PARTIALLY_OCCUPIED,
      room.getRoomStatus());
  }

  @Test
  void shouldReleaseRoomWhenResidentLeaves() {

    NursingHome nursingHome = new NursingHome();

    Room room = new Room(
      nursingHome,
      2,
      "Shared",
      "A-101"
    );

    Resident resident = new Resident(
      1L,
      "Carlos",
      "Perez",
      "999999999",
      "Maria",
      "Perez",
      "888888888"
    );

    resident.activate();

    resident.assignToRoom(room);

    resident.leaveRoom();

    assertNull(resident.getRoom());

    assertEquals(0,
      room.getOccupiedSlots());

    assertEquals(RoomStatus.AVAILABLE,
      room.getRoomStatus());
  }

  @Test
  void shouldThrowExceptionWhenRoomIsFull() {

    NursingHome nursingHome = new NursingHome();

    Room room = new Room(
      nursingHome,
      1,
      "Private",
      "A-102"
    );

    Resident resident1 = new Resident(
      1L,
      "Carlos",
      "Perez",
      "999999999",
      "Maria",
      "Perez",
      "888888888"
    );

    Resident resident2 = new Resident(
      2L,
      "Ana",
      "Lopez",
      "777777777",
      "Luis",
      "Lopez",
      "666666666"
    );

    resident1.activate();
    resident2.activate();

    resident1.assignToRoom(room);

    assertThrows(IllegalStateException.class,
      () -> resident2.assignToRoom(room));
  }
}

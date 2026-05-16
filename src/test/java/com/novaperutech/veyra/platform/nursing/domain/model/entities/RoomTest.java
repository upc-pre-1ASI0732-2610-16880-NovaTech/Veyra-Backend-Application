package com.novaperutech.veyra.platform.nursing.domain.model.entities;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.NursingHome;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.RoomStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

  @Test
  void shouldOccupyRoomSlot() {

    NursingHome nursingHome = new NursingHome();

    Room room = new Room(
      nursingHome,
      2,
      "Shared",
      "A-101"
    );

    room.occupySlot();

    assertEquals(1, room.getOccupiedSlots());

    assertEquals(RoomStatus.PARTIALLY_OCCUPIED,
      room.getRoomStatus());
  }

  @Test
  void shouldFillRoomCompletely() {

    NursingHome nursingHome = new NursingHome();

    Room room = new Room(
      nursingHome,
      1,
      "Private",
      "A-102"
    );

    room.occupySlot();

    assertEquals(RoomStatus.OCCUPIED,
      room.getRoomStatus());

    assertFalse(room.hasAvailableSlots());
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

    room.occupySlot();

    assertThrows(IllegalStateException.class,
      room::occupySlot);
  }
}

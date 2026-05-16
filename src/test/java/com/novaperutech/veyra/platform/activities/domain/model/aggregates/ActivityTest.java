package com.novaperutech.veyra.platform.activities.domain.model.aggregates;

import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.domain.model.valueobjects.ActivityStatus;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

  @Test
  void shouldCreateActivityCorrectly() {

    CreateActivityCommand command = new CreateActivityCommand(
      "Music Therapy",
      LocalDate.now(),
      LocalTime.of(10, 0),
      LocalTime.of(11, 0),
      "Recreation",
      1L,
      1L,
      1L
    );

    Activity activity = new Activity(command);

    assertNotNull(activity);

    assertEquals("Music Therapy", activity.getName());

    assertEquals(ActivityStatus.PENDING,
      activity.getStatus());
  }

  @Test
  void shouldCompleteActivity() {

    CreateActivityCommand command = new CreateActivityCommand(
      "Reading Session",
      LocalDate.now(),
      LocalTime.of(9, 0),
      LocalTime.of(10, 0),
      "Education",
      1L,
      1L,
      1L
    );

    Activity activity = new Activity(command);

    activity.complete();

    assertEquals(ActivityStatus.COMPLETED,
      activity.getStatus());
  }

  @Test
  void shouldCancelActivity() {

    CreateActivityCommand command = new CreateActivityCommand(
      "Exercise",
      LocalDate.now(),
      LocalTime.of(8, 0),
      LocalTime.of(9, 0),
      "Health",
      1L,
      1L,
      1L
    );

    Activity activity = new Activity(command);

    activity.cancel();

    assertEquals(ActivityStatus.CANCELLED,
      activity.getStatus());
  }

  @Test
  void shouldThrowExceptionWhenCancellingCompletedActivity() {

    CreateActivityCommand command = new CreateActivityCommand(
      "Yoga",
      LocalDate.now(),
      LocalTime.of(7, 0),
      LocalTime.of(8, 0),
      "Wellness",
      1L,
      1L,
      1L
    );

    Activity activity = new Activity(command);

    activity.complete();

    assertThrows(IllegalStateException.class,
      activity::cancel);
  }
}

```gherkin
Feature: Health Metric Registration

  Scenario: Staff registers a resident's daily vital signs successfully
    Given the staff member is on the resident's profile page
    And the resident has an active status in the system
    When the staff member enters the daily vital signs including blood pressure and temperature
    And clicks the "Save Metrics" button
    Then the system should save the new health record in the database
    And the system should display a "Health metrics updated successfully" message

  Scenario: Attempting to save metrics with missing required data
    Given the staff member is on the resident's profile page
    When the staff member leaves the "blood pressure" field empty
    And clicks the "Save Metrics" button
    Then the system should prevent the submission
    And the system should display a "Blood pressure is required" validation error

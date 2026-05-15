```gherkin
Feature: Health Metric Registration

  Scenario: Administrator successfully registers a medication intake
    Given the administrator is on the medication management module
    And the medication has available stock
    When the administrator registers a new dose intake for the resident
    Then the system should decrease the medication stock by one unit
    And the system should save the date and time of the intake
    And the system should display a "Medication registered successfully" message

  Scenario: Attempting to register a medication intake with no stock
    Given the administrator is on the medication management module
    And the selected medication has zero stock
    When the administrator attempts to register a new dose intake
    Then the system should prevent the registration
    And the system should display an "Out of stock" warning message

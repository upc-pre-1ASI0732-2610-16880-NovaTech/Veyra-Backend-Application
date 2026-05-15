Feature: Family Portal Activity Viewing

  Scenario: Family member checks daily activities
    Given the family member is authenticated in the family portal
    And their account is linked to an active resident
    When the family member navigates to the "Daily Logs" section
    Then the system should display a chronological list of the resident's activities for the current day

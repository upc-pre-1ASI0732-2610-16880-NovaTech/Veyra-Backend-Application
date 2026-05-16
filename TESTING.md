# Testing Strategy

## Objective

The test suite validates the backend by bounded context, keeps the project runnable with Java 21 and Maven, and prevents real calls to external services during automated execution. The suite combines smoke, unit, controller, and repository tests with a structure that matches the real DDD modules in `src/main/java/com/novaperutech/veyra/platform/`.

## Test Folder Structure

```text
src/test/java/com/novaperutech/veyra/platform/
├── activities/
│   └── unit/
├── analytics/
│   └── unit/
├── hcm/
│   └── unit/
├── health/
│   └── unit/
├── iam/
│   ├── unit/
│   └── integration/
├── nursing/
│   └── unit/
├── payments/
│   └── unit/
├── profiles/
│   ├── unit/
│   └── integration/
├── shared/
│   └── unit/
├── tracking/
│   └── unit/
└── VeyraPlatformApplicationTests.java
```

## Test Types Implemented

- Smoke test
- Unit tests
- Integration tests
- Controller tests with `MockMvc`
- Repository tests with `@DataJpaTest`

## Tools

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- H2 Database
- Maven

## How To Run

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## External Service Isolation

- Stripe is mocked in the smoke test and in payment unit tests.
- Cloudinary is exercised only through Mockito-based unit tests with no network access.
- JWT uses a dummy secret in `src/test/resources/application-test.properties`.
- OpenAPI and infrastructure properties use dummy test values.
- H2 is used as the in-memory relational database for persistence-oriented tests.
- No test performs real calls to Stripe, Cloudinary, or external APIs.

## Coverage Summary

| Bounded Context | Test Class | Type | Validated Goal |
|---|---|---|---|
| platform | `VeyraPlatformApplicationTests` | Smoke | Load full Spring context with `test` profile |
| activities | `ActivitiesServiceUnitTest` | Unit | Create activity and reject missing resident |
| analytics | `AnalyticsServiceUnitTest` | Unit | Query metrics and reject invalid year input |
| hcm | `HcmServiceUnitTest` | Unit | Query staff and fail when staff is missing |
| health | `HealthServiceUnitTest` | Unit | Query allergies and evaluate resident allergy existence |
| iam | `IamAuthenticationUnitTest` | Unit | Authenticate users, reject invalid credentials, create users |
| iam | `IamAuthenticationControllerIntegrationTest` | Integration / Controller | Validate authentication HTTP endpoints with `MockMvc` |
| nursing | `NursingServiceUnitTest` | Unit | Query residents and active residents by nursing home |
| payments | `PaymentCommandServiceUnitTest` | Unit | Process payments and handle mocked Stripe failure |
| profiles | `ProfilesServiceUnitTest` | Unit | Create profiles and handle missing or invalid profile updates |
| profiles | `ProfilesStorageUnitTest` | Unit | Upload profile images with mocked Cloudinary behavior |
| profiles | `ProfilesRepositoryIntegrationTest` | Integration / Repository | Persist and query `PersonProfile` with H2 |
| shared | `SharedNamingStrategyUnitTest` | Unit | Validate shared JPA naming strategy behavior |
| tracking | `TrackingServiceUnitTest` | Unit | Query devices and unassigned device lists |

## Notes

- `iam`, `profiles`, and `payments` contain the heaviest business and infrastructure touchpoints, so they received deeper coverage.
- `activities`, `analytics`, `hcm`, `health`, `nursing`, `shared`, and `tracking` each have at least one representative automated test aligned to real code in that bounded context.
- Repository integration was added where it provides useful value with H2 and no external dependencies.

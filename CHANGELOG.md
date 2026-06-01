# Changelog

All notable changes to this project are documented in this file.

## [Unreleased]

### Added
- PBKDF2 password hashing with constant-time comparison for credential storage.
- CORS filter with configurable origin via `CORS_ORIGIN` environment variable.
- Global exception mapper to prevent stack trace leakage in production.
- Structured logging (java.util.logging) across all resource classes and filters.
- Expanded JWT tests: expired tokens, invalid tokens, tampered tokens, issued-at claims.
- Expanded JPA tests: status transitions, timestamp validation, mark-as-read, notification types.
- Professional English README with architecture diagram and accurate API reference.
- Java 25 LTS upgrade documentation under `.github/java-upgrade/20260511115348/`.
- Contribution and security guidance.

### Changed
- JWT secret no longer has a fallback default; application fails to start if `JWT_SECRET` is not set.
- Production `persistence.xml` schema generation changed from `create` to `none`.
- Entity constraints: added `@Column(nullable = false)` and length limits on required fields.
- Upgraded the runtime from Java 11 to Java 25.
- Updated Maven compiler support and Jakarta EE dependencies.
- Updated PostgreSQL JDBC to the latest patched release to address known CVEs.
- Updated the CI workflow to use Java 25.

### Fixed
- Resolved PostgreSQL JDBC CVE-2024-1597.
- Removed hardcoded plaintext passwords (now PBKDF2-hashed at startup).
- Removed weak default JWT secret from source code and `.env.example`.
- Corrected README: removed non-existent endpoints, Docker references, and wrong credentials.
- Validated the project with local build and test execution.

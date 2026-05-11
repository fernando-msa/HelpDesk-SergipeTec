# Changelog

All notable changes to this project are documented in this file.

## [Unreleased]

### Added
- Structured project README.
- Java 25 LTS upgrade documentation under `.github/java-upgrade/20260511115348/`.
- Contribution and security guidance.

### Changed
- Upgraded the runtime from Java 11 to Java 25.
- Updated Maven compiler support and Jakarta EE dependencies.
- Updated PostgreSQL JDBC to the latest patched release to address known CVEs.
- Updated the CI workflow to use Java 25.

### Fixed
- Resolved PostgreSQL JDBC CVE-2024-1597.
- Validated the project with local build and test execution.

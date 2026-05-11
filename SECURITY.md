# Security Policy

## Supported Versions

This project is maintained on the current `main` branch and the Java 25 LTS upgrade branch history.

## Reporting a Vulnerability

If you discover a security issue, please report it privately instead of opening a public issue.

Include the following details:

- A description of the issue
- Impacted component or endpoint
- Steps to reproduce
- Affected versions or configurations
- Suggested remediation, if known

## Security Practices

- Keep secrets in environment variables, not in source control.
- Use strong JWT secrets with sufficient entropy.
- Prefer trusted PostgreSQL connections with TLS verification.
- Review dependency updates for CVEs before merging.
- Re-run the test suite after security-related changes.

## Dependency Notes

The project currently uses:

- Java 25 LTS
- Maven 3.9.15
- Jakarta EE 10.0.0
- PostgreSQL JDBC 42.7.11

If you upgrade dependencies, validate both functionality and known vulnerability status.

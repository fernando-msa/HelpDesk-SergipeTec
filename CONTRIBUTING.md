# Contributing Guide

Obrigado por considerar contribuir com o HelpDesk-SergipeTec.

## Getting Started

1. Fork the repository.
2. Create a feature branch from `main`.
3. Make focused changes with tests when applicable.
4. Run the test suite locally.
5. Open a pull request with a clear summary.

## Development Rules

- Keep changes small and focused.
- Preserve existing API behavior unless a change is explicitly requested.
- Update tests when you modify business logic.
- Prefer readable, well-named code over clever shortcuts.
- Do not commit secrets or environment-specific credentials.

## Local Validation

Use the project build commands before submitting changes:

```bash
mvn clean test
```

If you change only documentation, still ensure the repository remains clean and coherent.

## Pull Request Checklist

- [ ] Change has a clear purpose.
- [ ] Tests pass locally.
- [ ] Documentation updated when needed.
- [ ] No unrelated files are modified.
- [ ] Security-sensitive changes are explained.

## Reporting Issues

When opening an issue, include:

- What you expected to happen
- What actually happened
- Steps to reproduce
- Relevant logs or screenshots
- Your environment details

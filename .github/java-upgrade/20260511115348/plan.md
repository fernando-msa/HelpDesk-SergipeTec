# Upgrade Plan: HelpDesk-SergipeTec (20260511115348)

- **Generated**: 11 May 2026, 11:53:48
- **HEAD Branch**: main
- **HEAD Commit ID**: N/A (git available but commit ID not captured at plan generation)

## Available Tools

**JDKs**
- JDK 25.0.3: C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin (target JDK, used by steps 1 and 3-5)
- JDK 11: not available (baseline will be skipped)

**Build Tools**
- Maven: **<TO_BE_INSTALLED>** (required for build; Java 25 requires Maven 4.0+)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260511115348
- Run tests before and after the upgrade: true

## Upgrade Goals

- Java: 11 → 25 (Latest LTS)

## Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
| --- | --- | --- | --- |
| Java | 11 | 25 | User requested |
| Maven | (not installed) | 4.0.0 | Maven 3.x does not support Java 25; Maven 4.0+ required |
| maven-compiler-plugin | (implicit 3.x) | 3.11.0 | Versions before 3.11 cannot compile Java 25 bytecode |
| maven-war-plugin | 3.3.2 | 3.3.2 | Compatible with Java 25 |
| maven-surefire-plugin | 3.1.2 | 3.1.2 | Compatible with Java 25 |
| jakarta.jakartaee-api | 9.1.0 | 9.1.0 | Compatible; Jakarta EE 9 supports Java 11-25 |
| io.jsonwebtoken (jjwt) | 0.11.5 | 0.11.5 | Compatible with Java 25 |
| org.postgresql | 42.5.4 | 42.5.4 | Compatible with Java 25 |
| org.hibernate.orm | 6.2.7.Final | 6.2.7.Final | Compatible with Java 25 |
| com.h2database | 2.2.220 | 2.2.220 | Compatible with Java 25 |
| org.junit.jupiter | 5.10.0 | 5.10.0 | Compatible with Java 25 |

## Derived Upgrades

- **Maven**: 4.0.0+ (required to support Java 25 compilation and build)
- **maven-compiler-plugin**: 3.11.0+ (required to compile Java 25 bytecode)

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Install Maven 4.0+ (required for Java 25 support); JDK 25 is already available
  - **Changes to Make**: Install Maven 4.0.0 via appmod-install-maven tool
  - **Verification**: `mvn -version` should show Maven 4.0.0+, `java -version` should show Java 25

- Step 2: Setup Baseline
  - **Rationale**: (Skipped) Base JDK 11 is not available; baseline compilation and tests cannot be performed
  - **Changes to Make**: N/A
  - **Verification**: N/A

- Step 3: Upgrade maven-compiler-plugin to 3.11.0
  - **Rationale**: Java 25 requires maven-compiler-plugin 3.11+; older versions cannot compile Java 25 bytecode
  - **Changes to Make**: Add explicit `<maven-compiler-plugin>` version 3.11.0+ to pom.xml build plugins section
  - **Verification**: `mvn clean test-compile -q` succeeds with Java 25

- Step 4: Upgrade Java compiler target from 11 to 25
  - **Rationale**: Update project to compile and target Java 25 bytecode; leverage modern language features and security improvements
  - **Changes to Make**: Update `maven.compiler.source` and `maven.compiler.target` from 11 to 25 in pom.xml properties
  - **Verification**: `mvn clean test-compile -q` succeeds, bytecode version is 25

- Step 5: Final Validation
  - **Rationale**: Verify all goals met, resolve any TODOs, achieve 100% test pass rate with Java 25
  - **Changes to Make**: Fix any compilation or test failures; clean rebuild and full test suite execution
  - **Verification**: `mvn clean test -q` succeeds with Java 25, test pass rate ≥ baseline or 100% (baseline not available)

## Key Challenges

None identified. Code does not use:
- Internal/sun.* packages
- Reflection into java.base with setAccessible
- Removed APIs (Thread.stop(), Class.newInstance(), etc.)
- SecurityManager (deprecated 17, disabled 21+)
- JAXB or other removed JDK modules

All dependencies are compatible with Java 25.

# AGENTS.md

## Cursor Cloud specific instructions

This is a single, self-contained **Java 11 / Maven** console application (Transport Fleet Management demo). There are no external services (no database, web server, or API) — the whole product is one JVM process whose entrypoint is `com.transport.Main` (`src/main/java/com/transport/Main.java`).

- Build tool: **Maven** (`pom.xml`). Java toolchain in this VM is JDK 21, which compiles the project's Java 11 target fine.
- Standard commands (see `README.md`):
  - Compile: `mvn clean compile`
  - Run (dev): `mvn exec:java -Dexec.mainClass="com.transport.Main"` — prints a demo scenario to stdout and exits (no server/watch mode).
  - Package a jar: `mvn package` → `target/fleet-management-1.0.0.jar`
- Tests/lint: `mvn test` reports **"No tests to run"** — there is no `src/test` directory and no test classes yet, even though JUnit is declared in `pom.xml`. There is no separate linter configured; compilation is the effective static check.

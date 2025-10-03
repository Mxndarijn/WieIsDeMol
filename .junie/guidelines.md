Project Guidelines (Advanced)

This document captures build, testing, and development notes specific to this repository. It assumes familiarity with Java, Gradle, and the PaperMC plugin ecosystem.

1) Build and Configuration

- Java Toolchain
  - Target: Java 21 (see build.gradle: targetJavaVersion = 21). Ensure your local JDK or Gradle toolchain provides Java 21.
  - If you use IntelliJ IDEA, set Project SDK to 21 and enable Gradle toolchain auto-provisioning if needed.

- Gradle and Shadow JAR
  - Plugin: com.gradleup.shadow (version 8.3.3).
  - Build command (produces plugin JAR without classifier):
    - Windows PowerShell: gradle shadowJar
    - With wrapper (if added later): .\gradlew shadowJar
  - Output artifact name is configured to omit version and classifier:
    - build\libs\wieisdemol.jar
  - Do not shade/spawn Bukkit/Paper APIs into the final JAR. All Paper/Spigot-like APIs are compileOnly and must be provided by the server at runtime. Keep that separation.

- Repositories and Dependencies
  - Paper API: io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT (compileOnly)
  - PlaceholderAPI: me.clip:placeholderapi:2.11.6 (compileOnly)
  - Authlib, ProtocolLib, SuperVanish are compileOnly; ensure the target server has compatible versions installed if features relying on them are used.
  - libs and libsImpl directories:
    - libs: JARs used as compileOnly, provided by runtime.
    - libsImpl: JARs used as implementation (bundled into the final Shadow JAR). Place only libraries safe to relocate and distribute here.
  - Do not attempt to relocate or include Paper API or server-provided plugins into the shaded JAR.

- Resource Processing
  - processResources expands plugin.yml with the project version (version property). If you add tokens in plugin.yml, keep them under filesMatching('plugin.yml') and update props accordingly.

- Local Run/Deployment
  - After gradle shadowJar, copy build\libs\wieisdemol.jar into your server's plugins directory.
  - Restart or reload the server as appropriate (restart recommended over reload for Paper).

2) Testing

This project does not ship with tests by default. Below are the conventions that work with this build to add tests when needed.

- Enable JUnit 5 (Jupiter)
  - Add the following to build.gradle dependencies block:
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.3'
  - And ensure the test task uses the JUnit Platform:
    test {
        useJUnitPlatform()
    }

- Directory Layout
  - Place tests under src\test\java using the same package conventions as src\main\java.

- Running Tests
  - Windows PowerShell: gradle test
  - With wrapper (if present): .\gradlew test
  - In IntelliJ IDEA: Right-click the test or package and Run Tests. Ensure Gradle is the test runner if that is your project preference.

- Example Test
  - Create src\test\java\nl\mxndarijn\ExampleTest.java with:
    package nl.mxndarijn;

    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.assertTrue;

    class ExampleTest {
        @Test
        void sanity() {
            assertTrue(true);
        }
    }
  - We verified in this environment that adding the JUnit 5 dependency and a minimal test compiles successfully; remove temporary test files if they are only for demonstration.

- Integration vs. Unit Tests in a Paper Plugin
  - Business logic that does not depend on Bukkit/Paper types can be unit-tested directly.
  - Code depending on Bukkit/Paper often requires a test harness or a mock framework; consider abstracting logic away from Bukkit classes, or use a lightweight integration test strategy with a headless server if necessary (not configured here by default).

3) Additional Development Notes

- Paper API and Async Work
  - Some code paths schedule asynchronous tasks (e.g., MxHeadManager refresh job). Ensure any Bukkit API interactions from async code are thread-safe. Avoid accessing non-thread-safe Bukkit objects from async threads.
  - MxHeadManager periodically refreshes up to 40 player skull textures last refreshed over two days ago, ordering by least recently refreshed. If you modify refresh criteria, keep rate-limiting and exception handling intact to avoid hot loops.

- Head Data Storage
  - Head metadata is read/written via ConfigFiles.HEAD_DATA (Bukkit FileConfiguration). MxHeadSection encapsulates access and persistence. When adding new fields, both serialization and migration concerns must be handled carefully.

- Resources and I/O
  - The project uses commons-io and json-simple where appropriate. For network calls (e.g., to fetch textures), observe timeouts and error logging via nl.mxndarijn.api.logger.

- Logging
  - Use Logger.logMessage(LogLevel, Prefix, message) – prefixes like Prefix.MXHEAD_MANAGER are already defined for consistent log lines.

- Code Style
  - Java 21 syntax is acceptable. Keep nullability in mind; project includes org.jetbrains:annotations for @NotNull/@Nullable, etc.
  - Prefer Optional for nullable flows as used throughout MxHeadManager/MxHeadSection.
  - Avoid Lombok; it is commented out in the build file. If you decide to reintroduce it, do so consistently and justify the build-time dependency.

- Plugin Metadata
  - plugin.yml is filtered with the project version. If you add custom placeholders, update processResources accordingly.

- Troubleshooting Build
  - If Gradle is not on PATH, use the Gradle Wrapper (add via gradle wrapper) and run .\gradlew shadowJar.
  - Ensure JDK 21 is selected; Gradle will attempt to use a toolchain if configured, but mismatches can still occur in local IDEs.

Housekeeping

- Temporary Files
  - Do not commit temporary tests or local config. If you add example tests to validate the setup, remove them before submitting unless they are intended to remain.
- Artifacts
  - Only the Shadow JAR (build\libs\wieisdemol.jar) should be deployed to servers. Do not publish intermediate artifacts.

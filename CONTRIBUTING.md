# Development guidance

Keep pull requests cohesive, small enough to review, and limited to their roadmap
scope. PR 1 establishes the Android foundation only.

- Never commit credentials, tokens, signing keys, keystores, passwords, or other
  secrets. Keep local SDK configuration and environment files untracked.
- Never commit generated APKs, AABs, build outputs, caches, or IDE state. The
  standard binary `gradle/wrapper/gradle-wrapper.jar` is intentionally committed.
- Prefer platform APIs. Justify every additional dependency and permission.
- Avoid unrelated formatting, cleanup, and refactors. Do not modify synced
  `sources/` material as part of unrelated development.
- Privacy and security behavior requires reproducible evidence and traceability
  to stable requirements. Intended behavior alone does not justify a claim.
- Run the README build, lint, and unit-test commands. Add focused tests with
  relevant behavior changes; do not manufacture production abstractions for tests.
  Do not hide lint errors in a baseline.
- Maintain the [requirements scaffold](docs/requirements.md) as requirements
  evolve. PR 2 owns the comprehensive register, threat model, and engine assessment.

Use JDK 17 and the committed wrapper. To regenerate it with trusted Gradle 9.6.0:

```sh
gradle wrapper --gradle-version 9.6.0 --distribution-type bin --gradle-distribution-sha256-sum bbaeb2fef8710818cf0e261201dab964c572f92b942812df0c3620d62a529a01
```

Validate the binary JAR against the
[official Gradle checksum reference](https://gradle.org/release-checksums/):
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`.
Do not reconstruct or encode the JAR as text. CI pins official GitHub actions
to immutable SHAs with their release tags in comments; update them deliberately.

No open-source license has been selected by this PR.

# Releasing

## Requirements
- Sonatype Central account and ownership of the `com.nhaarman.acorn` namespace.
- Central Publishing Token (username/password).
- PGP signing key and a secret key ring file (secring.gpg).

## Create secring.gpg
1) Find your key ID:
- `gpg --list-secret-keys --keyid-format LONG`

2) Export the secret key ring file (binary):
- macOS/Linux:
  - `gpg --export-secret-keys YOUR_KEY_ID > ~/.gnupg/secring.gpg`
  - `chmod 600 ~/.gnupg/secring.gpg`
- Windows (PowerShell):
  - `gpg --export-secret-keys YOUR_KEY_ID | Set-Content -Encoding Byte "$env:APPDATA\gnupg\secring.gpg"`

Keep `secring.gpg` private (never commit it). Do NOT paste your private key into gradle.properties.

## Configure Gradle (~/.gradle/gradle.properties)
```properties
# Central Publishing Portal
centralPortalUsername=YOUR_PUBLISHING_TOKEN_USERNAME
centralPortalPassword=YOUR_PUBLISHING_TOKEN_PASSWORD

# PGP signing (secret key ring file)
signing.keyId=YOUR_KEY_ID
signing.password=YOUR_KEY_PASSPHRASE
signing.secretKeyRingFile=/absolute/path/to/secring.gpg
```

## Versioning
- You must pass the version at build time. Use `-PversionName=1.2.3` (or `-Pversion=1.2.3`).
- Android modules can optionally specify a version code with `-PversionCode=123`.
- Snapshot builds must end with `-SNAPSHOT`, e.g. `-PversionName=1.2.4-SNAPSHOT`.

## Publish a release (Central Publishing Portal)
1) Choose a release version (no `-SNAPSHOT`) and ensure POM metadata is correct (configured in `.ops/publishing.gradle`).
2) Run: `./gradlew publish -PversionName=1.2.3 -PcentralPortal=true`.
3) Monitor https://central.sonatype.com/ → Publishing Activity until processing completes (artifacts appear on search.maven.org shortly after).

## Publish a snapshot (OSSRH snapshots repository)
1) Use a snapshot version: `-PversionName=1.2.4-SNAPSHOT`.
2) Run: `./gradlew publish -PversionName=1.2.4-SNAPSHOT` (do NOT pass `-PcentralPortal=true`).
3) Artifacts will be uploaded to `s01.oss.sonatype.org` snapshots and are not listed on search.maven.org.

## Troubleshooting
- Missing required project property: If you see `Missing required project property: versionName (or version)`, pass a version via `-PversionName=1.2.3` (or `-Pversion=1.2.3`).
- Central Portal rejects SNAPSHOT: Central Publishing Portal does not accept `-SNAPSHOT` versions. Use a release version and `-PcentralPortal=true`, or publish snapshots without `-PcentralPortal`.
- 401/403 during upload: check token credentials and ensure Central Publishing is enabled (`-PcentralPortal=true`).
- Missing signatures: ensure `signing.keyId`, `signing.password`, and `signing.secretKeyRingFile` are set as above.
- Validation failures: open the item in Publishing Activity for details.

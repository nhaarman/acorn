# Releasing

## Publishing (Central Portal)
1) Prepare the release
- Ensure version is a non‑SNAPSHOT (e.g., 1.2.3) and that POM metadata is correct (configured in .ops/publishing.gradle).
- Update changelog and tag the commit if desired: `git tag -a 1.2.3 -m 1.2.3 && git push --tags`.

2) Publish from your machine
- Run: `./gradlew publish`
- The build uploads to Central using your Publishing Token and signs all artifacts.

3) Monitor validation and availability
- Go to https://central.sonatype.com/ → Publishing Activity to see validation results.
- Once processing completes, artifacts will be visible on Central and then searchable on search.maven.org (indexing can take a few minutes).

## Verify before releasing (recommended)
- Local dry run: `./gradlew publishToMavenLocal -x test -x check` and inspect `~/.m2/repository/` to confirm artifacts and `.asc` signatures.
- Sanity check: build a small sample project depending on the new version from Maven Central once it appears.

## Documentation website
- Deploy the docs: `./gradlew orchidDeploy`
  - Force version name: `./gradlew orchidDeploy -PversionName=1.0.0`

## Troubleshooting
- 401/403 during upload: check Central token (username/password), token scope, and that centralPortal=true is set.
- Rejected by Central validations: open the item in Publishing Activity for details (common issues: missing javadoc/sources, invalid POM fields). Android modules ship an empty javadoc jar via our Gradle config.
- Missing signatures: ensure signingKey/signingPassword are configured; Gradle must sign every published artifact.
- Wrong coordinates/ownership: ensure your Central namespace claim for `com.nhaarman.acorn` is verified and matches your module group IDs.

## Setting up

Prerequisites
- Sonatype Central account: https://central.sonatype.com/
- Namespace ownership: Claim `com.nhaarman.acorn` and complete the ownership verification (usually a DNS TXT record for the parent domain).
- Publishing Token: Create a token in the Central Portal.
- PGP signing key: Maven Central requires signatures for all artifacts.
  - Generate: `gpg --full-generate-key` (RSA 4096 or Ed25519)
  - Export private key (ASCII‑armored) for Gradle: `gpg --armor --export-secret-keys <KEYID>`
  - Optional: upload your public key to `keys.openpgp.org` or `keyserver.ubuntu.com` so others can verify signatures.

Local Gradle configuration (~/.gradle/gradle.properties)
```properties
# Enable Central Publishing Portal
centralPortal=true
centralPortalUsername=YOUR_PUBLISHING_TOKEN_USERNAME
centralPortalPassword=YOUR_PUBLISHING_TOKEN_PASSWORD
# Optional endpoint override (usually not needed)
# centralPortalUrl=https://central.sonatype.com/api/v1/publisher/deploy/maven2

# PGP signing (required)
signingKey=-----BEGIN PGP PRIVATE KEY BLOCK-----\n... your multi-line key with \n newlines ...\n-----END PGP PRIVATE KEY BLOCK-----
signingPassword=YOUR_KEY_PASSPHRASE
```

Environment variable alternatives (optional)
- CENTRAL_PORTAL=true
- CENTRAL_PORTAL_USERNAME / CENTRAL_PORTAL_PASSWORD
- CENTRAL_PORTAL_URL
- SIGNING_KEY / SIGNING_PASSWORD

Notes
- Central Portal does not use staging; validations run server‑side after upload.
- Central Portal does not accept versions ending with -SNAPSHOT. For preview builds, use publishToMavenLocal or a private repository.

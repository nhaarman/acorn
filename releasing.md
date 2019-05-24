# Releasing

To push a release to Maven Central:

 - Tag the commit as a release: `git tag -a x.x.x -m x.x.x`
 - Push the tags: `git push --tags`
 - Execute `./gradlew publish`
 - Head over to `https://oss.sonatype.org/#stagingRepositories` and close and release the repository.

 - Execute `./gradlew orchidDeploy` to deploy the documentation website;
   - `./gradlew orchidDeploy -PversionName=1.0.0` to force the version name.

# Releasing

To push a release to Maven Central:

 - Tag the commit as a release: `git tag -a x.x.x -m x.x.x`
 - Push the tags: `git push --tags`
 - Execute `./gradlew publish`
 - Head over to `https://oss.sonatype.org/#stagingRepositories` and close and release the repository.
Properties are used to configure some project parameters that could be different in different environments (for example local and CI).

If you do not know what properties are and where to set them, see [this page](https://docs.gradle.org/current/userguide/build_environment.html).

Our plugins declare following properties:

* Publication controls:

  * `publishing.enabled` - default - `false`, enables tasks for publishing
  * `publishing.<publicationName>.enabled` - default - `true`, enables publication with specific name

* Publication credentials (<u>Don't put into project sources!</u>):

  * `publishing.<publicationName>.user` - username for publication with specific name
  * `publishing.<publicationName>.password` - password for publication with specific name

* Jar signing credentials (required for maven-central; <u>don't put into project sources!</u>):

  * `publishing.signing.id` - PGP id
  * `publishing.signing.key` - PGP key
  * `publishing.signing.passPhrase` - PGP passphrase


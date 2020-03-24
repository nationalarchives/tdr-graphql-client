# GraphQL Client

This is a simple graphQL client which can be used by other TDR projects to communicate with the consignment API and any other future graphQL APIs.

[GraphQLService](src/main/scala/uk/gov/nationalarchives/tdr/GraphQLService.scala) contains a single method to get a client.
[GraphQLClient](src/main/scala/uk/gov/nationalarchives/tdr/GraphQLClient.scala) contains the logic for the client. It takes generated case classes as arguments which are found in the [generated graphql](https://github.com/nationalarchives/tdr-generated-graphql) project.

## Local development

To publish the library locally, run:

```
sbt +package +publishLocal
```

The `+` signs mean that those commands are run for each Scala version specified in this project's build.sbt file.

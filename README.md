# Good First Issue Provider

A web services that provides a list of good first issues for configurable given GitHub repositories.
The issues are provided by an REST API and in a simple UI.

## How to run

The project is a Spring Boot based webservice. To run it, you need to have Java 17 installed.

To run the project, you can use the following command:

```shell
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

## How to use

The application provides a simple UI to search for good first issues. You can access it at `http://localhost:8080`.

All repositories are fetched from the GitHub API.
The application uses the GitHub API to fetch the issues for the given repositories.
Repositories that should be fetched can be configured in the `application.yml` file.
The following is an example configuration with 2 repositories:

```yaml
spring:
  hacktoberfest:
    issues:
      repositories:
        - org: OpenElements
          repo: hedera-enterprise
        - org: OpenElements
          repo: hedera-solo-action
          
```

Next to the UI, the application provides a REST API to fetch the good first issues.
The API is available at `http://localhost:8080/api/good-first-issues`.
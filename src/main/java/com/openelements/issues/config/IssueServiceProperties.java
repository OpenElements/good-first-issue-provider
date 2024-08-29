package com.openelements.issues.config;

import java.util.List;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "hacktoberfest"
)
public class IssueServiceProperties {

    private List<RepositoryProperty> repositories;

    public List<RepositoryProperty> getRepositories() {
        return repositories;
    }

    public void setRepositories(final List<RepositoryProperty> repositories) {
        this.repositories = Objects.requireNonNull(repositories, "repositories must not be null");
    }
}

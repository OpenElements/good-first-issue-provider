package com.openelements.issues;

import java.util.List;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "spring.hacktoberfest.issues"
)
public class IssueServiceProperties {

    private List<Repository> repositories;

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(final List<Repository> repositories) {
        this.repositories = Objects.requireNonNull(repositories, "repositories must not be null");
    }
}

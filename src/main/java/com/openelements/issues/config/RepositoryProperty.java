package com.openelements.issues.config;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record RepositoryProperty(@NonNull String org, @NonNull String repo) {

    public RepositoryProperty {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");

        if (org.isBlank()) {
            throw new IllegalArgumentException("org is required");
        }
        if (repo.isBlank()) {
            throw new IllegalArgumentException("repo is required");
        }
    }
}

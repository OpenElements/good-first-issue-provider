package com.openelements.issues.config;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record RepositoryProperty(@NonNull String org, @NonNull String repo, @Nullable List<String> excludeIdentifiers) {

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

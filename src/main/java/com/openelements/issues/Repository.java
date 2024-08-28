package com.openelements.issues;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record Repository(@NonNull String org, @NonNull String repo) {

    public Repository {
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
    }
}

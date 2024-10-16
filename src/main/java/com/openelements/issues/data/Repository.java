package com.openelements.issues.data;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record Repository(@NonNull String org, @NonNull String name, @NonNull String imageUrl, @NonNull List<String> languages) {



    public Repository(@NonNull String org, @NonNull String name, @NonNull String imageUrl, @NonNull List<String> languages) {
        this.org = Objects.requireNonNull(org, "org must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.imageUrl = Objects.requireNonNull(imageUrl, "imageUrl must not be null");
        Objects.requireNonNull(languages, "languages must not be null");
        this.languages = List.copyOf(languages);
        if (org.isBlank()) {
            throw new IllegalArgumentException("org is required");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }
}

package com.openelements.issues.data;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record Issue(@NonNull String title, @NonNull String link, @NonNull String org, @NonNull String repo, @NonNull String imageUrl, @NonNull String identifier, boolean isAssigned, boolean isClosed, @NonNull List<String> labels, @NonNull List<String> languageTags) {

    public Issue {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(link, "link must not be null");
        Objects.requireNonNull(org, "org must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        Objects.requireNonNull(imageUrl, "imageUrl must not be null");
        Objects.requireNonNull(identifier, "identifier must not be null");
        Objects.requireNonNull(labels, "labels must not be null");
        Objects.requireNonNull(languageTags, "languageTags must not be null");
    }
}

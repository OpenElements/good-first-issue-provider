package com.openelements.issues.data;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record Issue(@NonNull String title, @NonNull String link, @NonNull String org, @NonNull String repo, @NonNull String imageUrl, @NonNull String identifier, boolean isAssigned, boolean isClosed, @NonNull List<String> labels, @NonNull List<String> languageTags) {

    public Issue(@NonNull String title, @NonNull String link, @NonNull String org, @NonNull String repo, @NonNull String imageUrl, @NonNull String identifier, boolean isAssigned, boolean isClosed, @NonNull List<String> labels, @NonNull List<String> languageTags) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.link = Objects.requireNonNull(link, "link must not be null");
        this.org = Objects.requireNonNull(org, "org must not be null");
        this.repo = Objects.requireNonNull(repo, "repo must not be null");
        this.imageUrl = Objects.requireNonNull(imageUrl, "imageUrl must not be null");
        this.identifier = Objects.requireNonNull(identifier, "identifier must not be null");
        this.isAssigned = isAssigned;
        this.isClosed = isClosed;
        Objects.requireNonNull(labels, "labels must not be null");
        this.labels = List.copyOf(labels);
        Objects.requireNonNull(languageTags, "languageTags must not be null");
        this.languageTags = List.copyOf(languageTags);

        if (title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (link.isBlank()) {
            throw new IllegalArgumentException("link is required");
        }
        if (org.isBlank()) {
            throw new IllegalArgumentException("org is required");
        }
        if (repo.isBlank()) {
            throw new IllegalArgumentException("repo is required");
        }
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier is required");
        }
    }
}

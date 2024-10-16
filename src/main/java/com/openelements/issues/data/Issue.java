package com.openelements.issues.data;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record Issue(@NonNull String title, @NonNull String identifier, @NonNull Repository repository, @NonNull String link,
                    boolean isAssigned, boolean isClosed, @NonNull List<String> labels) {

    public Issue(@NonNull String title, @NonNull String identifier, @NonNull Repository repository, @NonNull String link,
            boolean isAssigned, boolean isClosed, @NonNull List<String> labels) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.link = Objects.requireNonNull(link, "link must not be null");
        this.repository = Objects.requireNonNull(repository, "org must not be null");
        this.identifier = Objects.requireNonNull(identifier, "identifier must not be null");
        this.isAssigned = isAssigned;
        this.isClosed = isClosed;
        Objects.requireNonNull(labels, "labels must not be null");
        this.labels = List.copyOf(labels);

        if (title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (link.isBlank()) {
            throw new IllegalArgumentException("link is required");
        }
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier is required");
        }
    }
}

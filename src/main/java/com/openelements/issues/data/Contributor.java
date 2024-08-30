package com.openelements.issues.data;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record Contributor(@NonNull String userName, @NonNull String avatarUrl) {

    public Contributor {
        Objects.requireNonNull(userName, "userName is required");
        Objects.requireNonNull(avatarUrl, "avatarUrl is required");

        if (userName.isBlank()) {
            throw new IllegalArgumentException("userName is required");
        }
        if (avatarUrl.isBlank()) {
            throw new IllegalArgumentException("avatarUrl is required");
        }
    }
}

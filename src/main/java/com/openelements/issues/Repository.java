package com.openelements.issues;

import java.util.List;
import org.jspecify.annotations.NonNull;

public record Repository(@NonNull String org, @NonNull String name, @NonNull String imageUrl, @NonNull List<String> languages) {
}

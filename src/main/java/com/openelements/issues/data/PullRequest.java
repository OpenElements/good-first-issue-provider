package com.openelements.issues.data;

import java.util.List;
import org.jspecify.annotations.NonNull;

public record PullRequest(@NonNull String title, @NonNull String identifier, @NonNull Repository repository, @NonNull String link,
                          boolean isMerged, @NonNull List<String> labels) {
}

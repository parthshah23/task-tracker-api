package com.example.tasktracker.models;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public record Task(
    @NotNull Long id,
    @NotNull Integer taskNumber,
    @NotNull String title,
    @NotNull String description,
    @NotNull String status
) {

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Task task &&
            Objects.equals(title, task.title) &&
            Objects.equals(description, task.description));
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }
}

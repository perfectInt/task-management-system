package io.sultanov.taskmanagementsystem.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    PENDING("Pending"),
    IN_PROCESS("In process"),
    COMPLETED("Completed");

    private final String value;
}

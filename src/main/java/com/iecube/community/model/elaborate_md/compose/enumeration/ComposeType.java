package com.iecube.community.model.elaborate_md.compose.enumeration;

import lombok.Getter;

@Getter
public enum ComposeType {
    TEXT(0),
    QUESTION(1),
    INTERACTION(2);

    private final int value;

    private ComposeType(final int value) {
        this.value = value;
    }
}

package com.iecube.community.model.elaborate_md.block.enumeration;

import lombok.Getter;

@Getter
public enum BlockType {
    TEXT(0),
    QUESTION(1),
    INTERACTION(2);

    private final int value;

    private BlockType(final int value) {
        this.value = value;
    }
}

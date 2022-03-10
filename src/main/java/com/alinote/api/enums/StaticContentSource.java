package com.alinote.api.enums;

import java.util.*;

public enum StaticContentSource {
    REG_TOU(1), REG_PP(2);

    private Integer value;

    StaticContentSource(Integer value) {
        this.value = value;
    }

    public static Optional<StaticContentSource> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        s -> s.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}

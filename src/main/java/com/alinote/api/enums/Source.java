package com.alinote.api.enums;

import java.util.*;

public enum Source {
    REG(1), FIND_ID(2), FIND_PASS(3), PRFL_CHNG_NO(4);

    private Integer value;

    Source(Integer value) {
        this.value = value;
    }

    public static Optional<Source> valueOf(Integer value) {

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
package com.alinote.api.enums;

import java.util.*;

public enum ActiveStatus {

    ACTIVE(1),
    INACTIVE(2),
    ARCHIEVE(3);

    private final int value;

    ActiveStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static Optional<ActiveStatus> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        nt -> nt.value == value)
                .findFirst();
    }
}

package com.alinote.api.converter.core;

import java.util.*;

public abstract class DTOConverter<I, O> {

    public abstract O convert(I input);

    public List<O> convert(Collection<I> groups) {
        List<O> result = new ArrayList<>();
        for (I each : groups) {
            result.add((O) convert(each));
        }
        return result;
    }
}

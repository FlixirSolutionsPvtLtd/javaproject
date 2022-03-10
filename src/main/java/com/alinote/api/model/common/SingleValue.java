package com.alinote.api.model.common;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class SingleValue<T> {

    private T term;
}

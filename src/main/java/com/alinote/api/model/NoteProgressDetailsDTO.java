package com.alinote.api.model;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class NoteProgressDetailsDTO {

    private float rate;

    public NoteProgressDetailsDTO(float rate) {
        this.rate = rate;
    }
}

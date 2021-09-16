package dev.jzisc.personal.studysrs.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
public class ErrorBody {

    private int statusCode;
    private String message;

}

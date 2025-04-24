package org.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Privilege {
    private Long id;
    private String name;
}

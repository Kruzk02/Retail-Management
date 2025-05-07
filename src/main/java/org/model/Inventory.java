package org.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Inventory {

    private Long id;
    private Product product;
    private Location location;
    private Integer quantity;
    private LocalDateTime updatedAt;
}

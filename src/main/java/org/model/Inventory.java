package org.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Inventory {

    private Long id;
    private Collection<Product> products;
    private Collection<Location> locations;
    private Integer quantity;
    private LocalDateTime updatedAt;
}

package org.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Collection<Category> categories;
    private LocalDateTime created_at;

}

package org.dto;

import java.math.BigDecimal;
import java.util.Collection;

public record ProductRequest(
    String name,
    String description,
    BigDecimal price,
    Collection<String> categories
) { }

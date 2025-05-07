package org.service;

import org.model.Location;

public interface LocationService {
    Location save(String name);
    Location findById(Long id);
    Location findByName(String name);
    int deleteById(Long id);
}

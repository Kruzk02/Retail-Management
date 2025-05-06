package org.dao;

import org.model.Location;

public interface LocationDao {
    Boolean isLocationExist(Long id, String name);
    Location save(Location location);
    Location findById(Long inventoryId);
    Location findByName(String name);
    int deleteById(Long id);
}

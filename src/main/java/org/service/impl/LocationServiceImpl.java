package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.LocationDao;
import org.exception.DataAlreadyExistsException;
import org.exception.DataNotFoundException;
import org.model.Location;
import org.service.LocationService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {

    private LocationDao locationDao;

    @Override
    public Location save(String name) {
        Boolean isExists = locationDao.isLocationExist(null, name);
        if (isExists) {
            throw new DataAlreadyExistsException(name + " Already exists");
        }

        return locationDao.save(Location.builder().name(name).build());
    }

    @Override
    public Location findById(Long id) {
        return locationDao.findById(id);
    }

    @Override
    public Location findByName(String name) {
        return locationDao.findByName(name);
    }

    @Override
    public int deleteById(Long id) {
        Boolean isExists = locationDao.isLocationExist(id, null);
        if (!isExists) {
            throw new DataNotFoundException("Location not found with a id: " + id);
        }

        return locationDao.deleteById(id);
    }
}

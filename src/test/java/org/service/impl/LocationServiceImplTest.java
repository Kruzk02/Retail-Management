package org.service.impl;

import org.dao.LocationDao;
import org.exception.DataAlreadyExistsException;
import org.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Location;
import org.service.LocationService;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocationServiceImplTest {

    private LocationDao locationDao;
    private LocationService locationService;

    private Location location;
    @BeforeEach
    void setUp() {
        locationDao = Mockito.mock(LocationDao.class);
        locationService = new LocationServiceImpl(locationDao);

        location = Location.builder()
                .id(1L)
                .name("name")
                .build();
    }

    @Test
    void testSave_success() {
        when(locationDao.isLocationExist(null, "name")).thenReturn(false);
        when(locationService.save("name")).thenReturn(location);

        Location savedLocation = locationService.save("name");

        assertNotNull(savedLocation);
        assertEquals(location, savedLocation);
        assertEquals(location.getName(), savedLocation.getName());
        verify(locationDao).save(any(Location.class));
    }

    @Test
    void testSave_failed() {
        when(locationDao.isLocationExist(null, "name")).thenReturn(true);

        DataAlreadyExistsException exception = assertThrows(DataAlreadyExistsException.class, () -> locationService.save("name"));
        assertTrue(exception.getMessage().contains("Already exists"));
    }

    @Test
    void testFindByName_success() {
        when(locationService.findByName("name")).thenReturn(location);

        Location result = locationService.findByName("name");
        assertNotNull(result);
        assertEquals(location, result);
        verify(locationDao).findByName("name");
    }

    @Test
    void testFindByName_notFound() {
        when(locationService.findByName("no")).thenThrow(new EmptyResultDataAccessException(1));

        Location result = locationService.findByName("name");
        assertNull(result);
    }

    @Test
    void testFindById_success() {
        when(locationService.findById(1L)).thenReturn(location);

        Location result = locationService.findById(1L);
        assertNotNull(result);
        assertEquals(location, result);
        verify(locationDao).findById(1L);
    }

    @Test
    void testFindById_notFound() {
        when(locationService.findById(2L)).thenThrow(new EmptyResultDataAccessException(1));

        Location result = locationService.findById(1L);
        assertNull(result);
    }

    @Test
    void testDeleteById_success() {
        when(locationDao.isLocationExist(1L, null)).thenReturn(true);
        when(locationService.deleteById(1L)).thenReturn(1);

        int result = locationService.deleteById(1L);
        assertEquals(1, result);
        verify(locationDao).deleteById(1L);
    }

    @Test
    void testDeleteById_failed() {
        when(locationDao.isLocationExist(0L, null)).thenThrow(new EmptyResultDataAccessException(1));

        Exception exception = assertThrows(DataNotFoundException.class, () -> locationService.deleteById(1L));
        assertTrue(exception.getMessage().contains("Location not found with a id"));
    }
}
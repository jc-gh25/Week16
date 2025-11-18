package dog.rescue.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dog.rescue.controller.model.LocationData;
import dog.rescue.dao.LocationDao;
import dog.rescue.entity.Location;

@Service
public class RescueService {
	
	@Autowired
	private LocationDao locationDao;

//	@Transactional(readOnly = false)
//	public LocationData saveLocation(LocationData locationData) {
//		Location location = locationData.toLocation();
//		Location dbLocation = locationDao.save(location);
//		return new LocationData(dbLocation);
//	}
	
	@Transactional(readOnly = false)
	public LocationData saveLocation(LocationData locationData) {
	    Long locationId = locationData.getLocationId();
	    Location location;
	    
	    if (locationId != null) {
	        // UPDATE: Find existing location
	        location = locationDao.findById(locationId)
	            .orElseThrow(() -> new NoSuchElementException(
	                "Location with ID=" + locationId + " not found"));
	        
	        // Update the existing entity's fields
	        location.setBusinessName(locationData.getBusinessName());
	        location.setStreetAddress(locationData.getStreetAddress());
	        location.setCity(locationData.getCity());
	        location.setState(locationData.getState());
	        location.setZip(locationData.getZip());
	        location.setPhone(locationData.getPhone());
	    } else {
	        // CREATE: New location
	        location = locationData.toLocation();
	    }
	    
	    Location dbLocation = locationDao.save(location);
	    return new LocationData(dbLocation);
	}

	@Transactional(readOnly = true)
	public LocationData retrieveLocationById(Long locationId) {
		Location location = findLocationById(locationId);
		return new LocationData(location);
	}

	private Location findLocationById(Long locationId) {
		return locationDao.findById(locationId).orElseThrow(() -> new NoSuchElementException(
				"Location with ID " + locationId + " was not found."));
	}

	@Transactional(readOnly = true)
	public List<LocationData> retrieveAllLocations() {
		List<Location> locationEntities = locationDao.findAll();
		List<LocationData> locationDtos = new LinkedList<>();
		
		locationEntities.sort((loc1, loc2) -> 
			loc1.getBusinessName().compareTo(loc2.getBusinessName()));
		
		for(Location location : locationEntities) {
			LocationData locationData = new LocationData(location);
			locationDtos.add(locationData);
		}
		return locationDtos;
	}
	
	@Transactional(readOnly = false)
	public void deleteLocation(Long locationId) {
		Location location = findLocationById(locationId);
		locationDao.delete(location);
	}
	

}

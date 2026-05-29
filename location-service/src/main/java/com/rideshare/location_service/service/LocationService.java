package com.rideshare.location_service.service;

import org.jspecify.annotations.Nullable;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.location_service.dto.DriverLocationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {
     
	private final RedisTemplate<String, String> redisTemplate;
	
	//redis key for all driver locations
	//this key
	  private static final String DRIVER_LOCATION_KEY = "driver_locations";
      
	  /**
	   * update driver location in redis
	   * called every 3 sec by drivers phone
	   * maps to redis GEOADD command to store driver location in a geospatial index
	   */
	  
	  
	  public void updateDriverLocation(DriverLocationRequest request) {
		
		log.info("Updating location for driver: {}, lat: {}, lon: {}", request.getDriverId(), request.getLatitude(), request.getLongitude());
	
		//Important:longitude comes first in Point constructor -GeoSpatial data in Redis is stored as (longitude, latitude)
		Point driverPoint= new Point(request.getLongitude(), request.getLatitude());
		
		//this method adds the driver's location to the Redis geospatial index under the key "driver_locations" with the driver's ID as the member name.
		redisTemplate.opsForGeo().add(
				DRIVER_LOCATION_KEY, 
				driverPoint, 
				request.getDriverId()
				);
		
		log.info("Driver location updated in Redis for driver: {}", request.getDriverId());
	  }

	  public @Nullable Object findNearbyDrivers(double latitude, double longitude, double radiusInKm) {
		// TODO Auto-generated method stub
		return null;
	  }

}

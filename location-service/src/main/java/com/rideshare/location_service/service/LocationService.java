package com.rideshare.location_service.service;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.location_service.dto.DriverLocationRequest;
import com.rideshare.location_service.dto.NearByDriverResponse;

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

	  /**
	   * find nearby drivers within a certain radius of a given location
	   * called by matching service on ride request to find nearby drivers
	   * maps to redis GEORADIUS command to query the geospatial index for drivers within a specified radius of the given location.
	   */
	  
	  public List<NearByDriverResponse> findNearbyDrivers(
			  double latitude, 
			  double longitude, 
			  double radiusInKm
			  ) 
	  {
		  log.info("Finding nearby drivers for location: lat {}, lon {}, radius {} km", latitude, longitude, radiusInKm);
		  //the Circle class represents a circular area defined by a center point and a radius. The Point class represents a geographical point defined by its longitude and latitude. 
		  //The Distance class represents a distance value along with its unit of measurement (in this case, kilometers).
		  Circle searchArea = new Circle(new Point(longitude, latitude), new Distance(radiusInKm,Metrics.KILOMETERS)); // Convert radius to radians (Earth's radius in km)
		  
		  GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = redisTemplate.opsForGeo().radius(DRIVER_LOCATION_KEY, 
				  searchArea,
				  RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
				  .includeCoordinates()
				  .includeDistance()
				  .sortAscending()
				  .limit(10)
				  );
		  
		  List<NearByDriverResponse> nearbyDrivers = new ArrayList<>();
		  
		  if(geoResults != null) {
			  geoResults.forEach(result -> {
				 RedisGeoCommands.GeoLocation<String> location = result.getContent();
				 nearbyDrivers.add(new NearByDriverResponse(
						 location.getName(), // driverId
						 location.getPoint().getY(), // latitude
						 location.getPoint().getX(), // longitude
						 result.getDistance().getValue() // distance in km
						 ));
			  });
		  }
		  
		  log.info("Found {} nearby drivers", nearbyDrivers.size());
		  
		  return nearbyDrivers;
	  
	 
}
	  
	 /**
	  * remove driver when they go offline or log out
	  * maps to redis ZREM command to remove the driver's location from the geospatial index.
	  * 
	  */
	  
	  public void removeDriver(String driverId) {
		  log.info("Removing location for driver: {}", driverId);
		  redisTemplate.opsForGeo().remove(DRIVER_LOCATION_KEY, driverId);
		  log.info("Driver location removed from Redis for driver: {}", driverId);
	  }
	  
}

package com.rideshare.location_service.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.location_service.dto.DriverLocationRequest;
import com.rideshare.location_service.dto.NearByDriverResponse;
import com.rideshare.location_service.service.LocationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
@RestController
@RequestMapping("/api/v1/location")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

	private final LocationService locationService;
	 
	//driver phone calls this API every 3 seconds to update his location
	@PostMapping("/drivers/update")
	public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest request) {
		log.info("Received location update for driver: {}", request.getDriverId());
		locationService.updateDriverLocation(request);
		return ResponseEntity.ok("Driver Location updated successfully");
	}
	
	//matching service calls this when ride is requested
	// This method can be implemented to return nearby drivers based on the rider's location
	@GetMapping("/drivers/nearby")
	public ResponseEntity<List<NearByDriverResponse>> getNearbyDrivers(
			@RequestParam double latitude ,
			@RequestParam double longitude,
			@RequestParam (defaultValue = "5") double radiusInKm) {
		
		return ResponseEntity.ok(locationService.findNearbyDrivers(latitude, longitude, radiusInKm));
	}
	
	@DeleteMapping("/drivers/{driverId}")
	public ResponseEntity<String> removeDriver(@PathVariable String driverId) {
		log.info("Removing driver: {}", driverId);
		locationService.removeDriver(driverId);
		return ResponseEntity.ok("Driver removed successfully");
	}
	
	
}

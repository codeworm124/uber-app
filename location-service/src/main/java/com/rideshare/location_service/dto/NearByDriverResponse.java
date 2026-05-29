package com.rideshare.location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearByDriverResponse {//send data to matching service

	private String driverId;
	private double latitude;
	private double longitude;
	private double distanceInKm; // Optional: Distance from the rider, can be calculated in the matching service
	
}

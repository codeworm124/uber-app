package com.rideshare.rideservice.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private String id;
	
	//who accepted the ride(null until a driver accepts the ride)
	@Column(nullable = true)    
	private String driverId;
	
	//who requested the ride
	@Column(nullable = false)
	private String riderId;
	@Column(nullable = false)
	private String pickupLatitude;
	@Column(nullable = false)
	private String pickupLongitude;
	@Column(nullable = false)
	private String pickupAddress;
	@Column(nullable = false)
	private double dropoffLatitude;
	@Column(nullable = false)
	private double dropoffLongitude;
	@Column(nullable = false)
	private String dropAddress;
	
	//tracks the status of the ride
	@Enumerated(EnumType.STRING)
	private String Ridestatus; 	// e.g., "REQUESTED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    
	//fare Details
	private double estimatedFare;
	private double actualFare;
	
	//Timestamps
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	private LocalDateTime startedAt;
	private LocalDateTime completedAt;
	
	


}

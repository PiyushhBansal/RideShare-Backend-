package com.rideshare.service;

import com.rideshare.dto.RideRequest;
import com.rideshare.dto.RideResponse;
import com.rideshare.exception.BadRequestException;
import com.rideshare.exception.ResourceNotFoundException;
import com.rideshare.exception.UnauthorizedException;
import com.rideshare.model.Ride;
import com.rideshare.model.RideStatus;
import com.rideshare.model.User;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideResponse createRide(RideRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Ride ride = Ride.builder()
                .userId(user.getId())
                .pickupLocation(request.getPickupLocation())
                .dropLocation(request.getDropLocation())
                .status(RideStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .build();

        Ride savedRide = rideRepository.save(ride);
        return mapToRideResponse(savedRide, "Ride created successfully");
    }

    public List<RideResponse> getUserRides() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return rideRepository.findByUserId(user.getId())
                .stream()
                .map(ride -> mapToRideResponse(ride, null))
                .collect(Collectors.toList());
    }

    public List<RideResponse> getPendingRides() {
        return rideRepository.findByStatus(RideStatus.REQUESTED)
                .stream()
                .map(ride -> mapToRideResponse(ride, null))
                .collect(Collectors.toList());
    }

    public RideResponse acceptRide(String rideId) {
        String username = getCurrentUsername();
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new BadRequestException("Ride is not available for acceptance");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus(RideStatus.ACCEPTED);

        Ride updatedRide = rideRepository.save(ride);
        return mapToRideResponse(updatedRide, "Ride accepted successfully");
    }

    public RideResponse completeRide(String rideId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", rideId));

        // Check if user is either the passenger or the driver of this ride
        boolean isPassenger = ride.getUserId().equals(user.getId());
        boolean isDriver = ride.getDriverId() != null && ride.getDriverId().equals(user.getId());

        if (!isPassenger && !isDriver) {
            throw new UnauthorizedException("You are not authorized to complete this ride");
        }

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BadRequestException("Ride must be in ACCEPTED status to complete");
        }

        ride.setStatus(RideStatus.COMPLETED);
        Ride updatedRide = rideRepository.save(ride);
        return mapToRideResponse(updatedRide, "Ride completed successfully");
    }

    public List<RideResponse> getDriverRides() {
        String username = getCurrentUsername();
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return rideRepository.findByDriverId(driver.getId())
                .stream()
                .map(ride -> mapToRideResponse(ride, null))
                .collect(Collectors.toList());
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private RideResponse mapToRideResponse(Ride ride, String message) {
        return RideResponse.builder()
                .id(ride.getId())
                .userId(ride.getUserId())
                .driverId(ride.getDriverId())
                .pickupLocation(ride.getPickupLocation())
                .dropLocation(ride.getDropLocation())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt())
                .message(message)
                .build();
    }
}

package com.rideshare.repository;

import com.rideshare.model.Ride;
import com.rideshare.model.RideStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByUserId(String userId);

    List<Ride> findByDriverId(String driverId);

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByUserIdAndStatus(String userId, RideStatus status);

    List<Ride> findByDriverIdAndStatus(String driverId, RideStatus status);
}

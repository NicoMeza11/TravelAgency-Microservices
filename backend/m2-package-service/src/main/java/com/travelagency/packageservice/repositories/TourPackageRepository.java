package com.travelagency.packageservice.repositories;
import com.travelagency.packageservice.entities.TourPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackageEntity, Long> {
}

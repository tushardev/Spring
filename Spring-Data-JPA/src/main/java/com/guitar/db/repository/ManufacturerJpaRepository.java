package com.guitar.db.repository;

import com.guitar.db.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ManufacturerJpaRepository extends JpaRepository<Manufacturer, Long> {

    List<Manufacturer> findByFoundedDateBefore(Date date);

    List<Manufacturer> findByFoundedDateAfter(Date date);

    List<Manufacturer> getAllThatSellAcoustics(String modelTypeName);
}

/*** Here we have implemented method for Named Native Query with matching method name instead of @Query annotation */
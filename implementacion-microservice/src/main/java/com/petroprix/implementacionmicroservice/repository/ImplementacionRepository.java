package com.petroprix.implementacionmicroservice.repository;


import com.petroprix.implementacionmicroservice.entity.ImplementacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImplementacionRepository extends JpaRepository<ImplementacionEntity, Long> {
}

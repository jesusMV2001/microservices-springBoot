package com.petroprix.implementacionmicroservice.repository;

import com.petroprix.implementacionmicroservice.entity.RegistroCambiosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroCambiosRepository extends JpaRepository<RegistroCambiosEntity,Long> {
}

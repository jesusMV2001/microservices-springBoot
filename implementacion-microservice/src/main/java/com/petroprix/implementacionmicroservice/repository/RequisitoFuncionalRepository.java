package com.petroprix.implementacionmicroservice.repository;

import com.petroprix.implementacionmicroservice.entity.RequisitoFuncionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitoFuncionalRepository extends JpaRepository<RequisitoFuncionalEntity,Long> {
}

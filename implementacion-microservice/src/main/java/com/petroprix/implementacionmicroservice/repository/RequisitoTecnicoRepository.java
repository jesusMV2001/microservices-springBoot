package com.petroprix.implementacionmicroservice.repository;

import com.petroprix.implementacionmicroservice.entity.RequisitoTecnicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitoTecnicoRepository extends JpaRepository<RequisitoTecnicoEntity,Long> {
}

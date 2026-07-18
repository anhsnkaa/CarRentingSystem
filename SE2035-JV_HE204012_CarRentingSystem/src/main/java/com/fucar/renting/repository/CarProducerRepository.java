package com.fucar.renting.repository;

import com.fucar.renting.entity.CarProducer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarProducerRepository extends JpaRepository<CarProducer, Integer> {
}
package com.fucar.renting.service;

import com.fucar.renting.dto.CarProducerRequest;
import com.fucar.renting.entity.CarProducer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarProducerService {

    Page<CarProducer> findAll(Integer page, Integer size);

    List<CarProducer> findAllAsList();

    CarProducer findById(Integer id);

    CarProducer create(CarProducerRequest request);

    CarProducer update(Integer id, CarProducerRequest request);

    void delete(Integer id);
}
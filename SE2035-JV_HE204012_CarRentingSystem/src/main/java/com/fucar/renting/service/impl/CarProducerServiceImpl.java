package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CarProducerRequest;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.repository.CarProducerRepository;
import com.fucar.renting.service.CarProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarProducerServiceImpl implements CarProducerService {

    private final CarProducerRepository carProducerRepository;

    @Override
    public Page<CarProducer> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return carProducerRepository.findAll(pageable);
    }

    @Override
    public List<CarProducer> findAllAsList() {
        return carProducerRepository.findAll(Sort.by(Sort.Direction.ASC, "producerName"));
    }

    @Override
    public CarProducer findById(Integer id) {
        return carProducerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public CarProducer create(CarProducerRequest request) {
        CarProducer p = CarProducer.builder()
                .producerName(request.getProducerName())
                .address(request.getAddress())
                .country(request.getCountry())
                .build();
        return carProducerRepository.save(p);
    }

    @Override
    @Transactional
    public CarProducer update(Integer id, CarProducerRequest request) {
        CarProducer p = carProducerRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Producer not found: " + id));
        p.setProducerName(request.getProducerName());
        p.setAddress(request.getAddress());
        p.setCountry(request.getCountry());
        return carProducerRepository.save(p);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        carProducerRepository.deleteById(id);
    }
}
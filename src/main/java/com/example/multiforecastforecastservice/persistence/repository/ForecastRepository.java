package com.example.multiforecastforecastservice.persistence.repository;


import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForecastRepository extends MongoRepository<ForecastEntity, String> {
}

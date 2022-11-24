package com.example.multiforecastforecastservice.persistence.repository;


import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ForecastRepository extends MongoRepository<ForecastEntity, String> {

    Optional<ForecastEntity> findFirstByUserIdAndSourceOrderByCreatedDesc(Long userId, Source source);
}

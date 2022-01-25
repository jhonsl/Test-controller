package com.sofka.TestControladores.repository;

import com.sofka.TestControladores.model.Widget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetRepository extends MongoRepository<Widget, Long> {
}
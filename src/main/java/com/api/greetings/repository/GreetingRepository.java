package com.api.greetings.repository;

import com.api.greetings.entity.Greeting;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface GreetingRepository extends CrudRepository<Greeting, Long> {
    List<Greeting> findByText(String name);

    Greeting findById(long id);

    List<Greeting> findByErstelltAm(String erstelltAm);

    List<Greeting> findByErstelltVon(String erstelltVon);

}

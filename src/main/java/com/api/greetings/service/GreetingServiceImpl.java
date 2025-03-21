package com.api.greetings.service;

import com.api.greetings.entity.Greeting;
import com.api.greetings.repository.GreetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class GreetingServiceImpl implements GreetingService {

    @Autowired
    private GreetingRepository greetingRepository;

    @Override
    public Greeting saveGreeting(Greeting greeting) {
        return greetingRepository.save(greeting);
    }

    @Override
    public List<Greeting> fetchGreetingList() {
        return (List<Greeting>) greetingRepository.findAll();
    }

    @Override
    public Greeting updateGreeting(Greeting greeting, Long id) {
        Greeting greetingInDB = greetingRepository.findById(id).get();

        if (Objects.nonNull(greeting.getText()) && !"".equalsIgnoreCase(greeting.getText())) {
            greetingInDB.setText(greeting.getText());
        }
        if (Objects.nonNull(greeting.getErstelltAm()) && !"".equalsIgnoreCase(greeting.getErstelltAm())) {
            greetingInDB.setErstelltAm(greeting.getErstelltAm());
        }
        if (Objects.nonNull(greeting.getErstelltVon()) && !"".equalsIgnoreCase(greeting.getErstelltVon())) {
            greetingInDB.setErstelltVon(greeting.getErstelltVon());
        }
        if (Objects.nonNull(greeting.getGeaendertAm()) && !"".equalsIgnoreCase(greeting.getGeaendertAm())) {
            greetingInDB.setGeaendertAm(greeting.getGeaendertAm());
        } else {
            greetingInDB.setGeaendertAm(LocalDate.now().toString());
        }
        if (Objects.nonNull(greeting.getGeaendertVon()) && !"".equalsIgnoreCase(greeting.getGeaendertVon())) {
            greetingInDB.setGeaendertVon(greeting.getGeaendertVon());
        }
        return greetingRepository.save(greetingInDB);
    }

    @Override
    public void deleteGreetingById(Long id) {
        greetingRepository.deleteById(id);
    }
}

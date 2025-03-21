package com.api.greetings.service;

import com.api.greetings.entity.Greeting;

import java.util.List;

public interface GreetingService {

    Greeting saveGreeting(Greeting greeting);

    List<Greeting> fetchGreetingList();

    Greeting updateGreeting(Greeting greeting, Long id);

    void deleteGreetingById(Long id);
}

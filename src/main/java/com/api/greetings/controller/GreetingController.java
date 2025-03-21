package com.api.greetings.controller;


import com.api.greetings.entity.Greeting;
import com.api.greetings.repository.GreetingRepository;
import com.api.greetings.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController // @Controller + @ResponseBody
public class GreetingController {
    public static final String template = "Hello, %s!";
    public static final Logger LOG = LoggerFactory.getLogger(GreetingController.class);

    @Autowired
    private GreetingService greetingService;
    @Autowired
    private GreetingRepository greetingRepository;


    @GetMapping("greeting") // @RequestMapping(method=GET) / @PostMapping / @PutMapping / @DeleteMapping
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name, GreetingRepository repository) {
        String erstelltAm = LocalDate.now().toString();
        String erstelltVon = "timsauer";
        String geaendertAm = LocalDate.now().toString();
        String geaendertVon = "timsauer";
        Greeting greeting = Greeting.builder()
                .text(String.format(template, name))
                .erstelltAm(erstelltAm)
                .erstelltVon(erstelltVon)
                .geaendertAm(geaendertAm)
                .geaendertVon(geaendertVon)
                .build();

        return greetingService.saveGreeting(greeting);
    }

    @GetMapping("greetings")
    public List<Greeting> greetings(@RequestParam(value="text", defaultValue = "") String name,
                                @RequestParam(value="erstelltAm", defaultValue = "") String erstelltAm,
                                @RequestParam(value="id", defaultValue="") String id) {
        List<Greeting> response = new ArrayList<Greeting>();

        if (!(name.isEmpty() || erstelltAm.isEmpty() || id.isEmpty())) {
            for (Greeting greeting : greetingService.fetchGreetingList()) {
                if (greeting.getText().equals(name) && greeting.getErstelltAm().equals(erstelltAm) && greeting.getId() == Long.parseLong(id)) {
                    response.add(greeting);
                }
            }
        }
        else if (!(name.isEmpty() || erstelltAm.isEmpty())) {
            for (Greeting greeting : greetingService.fetchGreetingList()) {
                if (greeting.getText().equals(name) && greeting.getErstelltAm().equals(erstelltAm)) {
                    response.add(greeting);
                }
            }
        }
        else if (!(name.isEmpty() || id.isEmpty())) {
            for (Greeting greeting : greetingService.fetchGreetingList()) {
                if (greeting.getText().equals(name) && greeting.getId() == Long.parseLong(id)) {
                    response.add(greeting);
                }
            }
        }
        else if (!(erstelltAm.isEmpty() || id.isEmpty())) {
            for (Greeting greeting : greetingService.fetchGreetingList()) {
                if (greeting.getErstelltAm().equals(erstelltAm) && greeting.getId() == Long.parseLong(id)) {
                    response.add(greeting);
                }
            }
        }
        else if (!name.isEmpty()) {
            response = greetingRepository.findByText(name);
        }
        else if (!erstelltAm.isEmpty()) {
            response = greetingRepository.findByErstelltAm(erstelltAm);
        }
        else if (!id.isEmpty()) {
            response.add(greetingRepository.findById(Long.parseLong(id)));
        }
        else {
            response = greetingService.fetchGreetingList();
        }
        return response;
    }

    @GetMapping("greetings/{id}")
    public Greeting get_by_id(@PathVariable("id") String id) {
        if(!greetingRepository.existsById(Long.parseLong(id))) {
            LOG.error("Greeting with id " + id + " not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Greeting with id " + id + " not found");
        } else
            return greetingRepository.findById(Long.parseLong(id));
    }

    @PostMapping(
            value ="/greetings",
            consumes = "application/json",
            produces = "application/json")
    public Greeting postGreeting(@RequestBody Greeting greeting) {
        return greetingService.saveGreeting(greeting);
    }

    @PutMapping("greetings/{id}")
    public Greeting editGreeting(@PathVariable("id") String id, @RequestBody Greeting greeting) {
        return greetingService.updateGreeting(greeting, Long.parseLong(id));
    }

    @DeleteMapping("greetings/{id}")
    public String deleteGreetingById(@PathVariable("id") String id) {
        if (!greetingRepository.existsById(Long.parseLong(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Greeting with id " + id + " not found");
        }
        greetingService.deleteGreetingById(Long.parseLong(id));
        return "Success";
    }

}

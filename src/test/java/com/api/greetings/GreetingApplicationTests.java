package com.api.greetings;

import com.api.greetings.controller.GreetingController;
import com.api.greetings.entity.Greeting;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GreetingController greetingController;

	@Autowired
	private MockMvc mvc;

	private String getBaseUrl() {
		return "http://localhost:" + port;
	}

	private ResponseEntity<Greeting> saveNewGreeting(String name) {
		ResponseEntity<Greeting> response = restTemplate.getForEntity(getBaseUrl() + "/greeting?name=" + name, Greeting.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("Hello, " + name + "!", response.getBody().getText());
		return response;
	}

	@Test
	void contextLoads() throws Exception {
		assertThat(greetingController).isNotNull();
	}

	@Test
	void whenGetRequestIsSentToGreetingController_thenItShouldReturnNewGreetingAndSaveIt() throws Exception {
		ResponseEntity<Greeting> response = restTemplate.getForEntity(getBaseUrl() + "/greeting", Greeting.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("Hello, World!", response.getBody().getText());

		ResponseEntity<Greeting> savedResult = restTemplate.getForEntity(getBaseUrl() + "/greetings/" + response.getBody().getId(), Greeting.class);

		Assertions.assertEquals(response.getBody(), savedResult.getBody());
	}

	@Test
	void whenGetRequestIsSentToGreetingControllerWithName_thenItShouldReturnNewGreetingAndSaveIt() throws Exception {
		ResponseEntity<Greeting> response = restTemplate.getForEntity(getBaseUrl() + "/greeting?name=Tim", Greeting.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("Hello, Tim!", response.getBody().getText());

		ResponseEntity<Greeting> savedResult = restTemplate.getForEntity(getBaseUrl() + "/greetings/" + response.getBody().getId(), Greeting.class);

		Assertions.assertEquals(response.getBody(), savedResult.getBody());
	}

	@Test
	void whenGetRequestIsSentToGreetingControllerWithArguments_thenItShouldReturnNewGreetingWithArguments() throws Exception {
		ResponseEntity<Greeting> savedGreeting = saveNewGreeting("Tim");
		ObjectMapper objMapper = new ObjectMapper();

		// Abfrage der Greetings mit bestimmten Argumenten -> Nur Name
		ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/greetings?text=Hello, Tim!", List.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		List<String> jsonList = new ArrayList<>();
		for (Object obj : response.getBody()) {
			jsonList.add(objMapper.writeValueAsString(obj));
		}
		Assertions.assertTrue(jsonList.contains(objMapper.writeValueAsString(savedGreeting.getBody())));

		// Abfrage der Greetings mit bestimmten Argumenten -> All
		ResponseEntity<List> response2 = restTemplate.getForEntity(getBaseUrl() +
				"/greetings?text=Hello, Tim!&erstelltAm=" + savedGreeting.getBody().getErstelltAm()
						+ "&id=" + savedGreeting.getBody().getId().toString(),
				List.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());

		List<String> jsonList2 = new ArrayList<>();
		for (Object obj : response2.getBody()) {
			jsonList2.add(objMapper.writeValueAsString(obj));
		}
		Assertions.assertTrue(jsonList2.contains(objMapper.writeValueAsString(savedGreeting.getBody())));
	}

	@Test
	void whenGetRequestIsSentToGreetingControllerWithId_thenItShouldReturnGreetingWithExactId() throws Exception {
		ResponseEntity<Greeting> response = restTemplate.getForEntity(getBaseUrl() + "/greetings/1", Greeting.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(1, response.getBody().getId());
	}

	@Test
	void whenGetRequestIsSentToGreetingControllerWithIllegalId_thenItShouldReturnNotFound() throws Exception {
		ResponseEntity<Greeting> response = restTemplate.getForEntity(getBaseUrl() + "/greetings/-1", Greeting.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void whenPostRequestIsSentToGreetingController_thenItShouldReturnGreetingAndSaveIt() throws Exception {
		Object testObj = new Object() {
			public final String text = "text";
			public final String erstelltAm = LocalDate.now().toString();
			public final String erstelltVon = "erstelltVon";
			public final String geaendertAm = LocalDate.now().toString();
			public final String geaendertVon = "geaendertVon";
		};
		ObjectMapper objMapper = new ObjectMapper();
		String json = objMapper.writeValueAsString(testObj);

		MvcResult result = mvc.perform(post("/greetings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.characterEncoding("utf-8"))
				.andExpectAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.text").value("text"),
						jsonPath("$.erstelltAm").value(LocalDate.now().toString()),
						jsonPath("$.erstelltVon").value("erstelltVon"),
						jsonPath("$.geaendertAm").value(LocalDate.now().toString()),
						jsonPath("$.geaendertVon").value("geaendertVon")
				)
				.andReturn();

		Greeting greeting = objMapper.readValue(result.getResponse().getContentAsString(), Greeting.class);

		MvcResult savedResult = mvc.perform(get("/greetings/"+greeting.getId()))
				.andExpect(status().isOk())
				.andReturn();

		Assertions.assertEquals(result.getResponse().getContentAsString(), savedResult.getResponse().getContentAsString());
	}

	@Test
	void whenPutRequestIsSentToGreetingControllerWithId_thenItShouldReturnUpdatedGreetingWithExactIdAndSaveIt() throws Exception {
		ResponseEntity<Greeting> response = restTemplate.getForEntity(getBaseUrl() + "/greetings/1", Greeting.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Object testObj = new Object() {
			public final String text = "updated";
			public final String erstelltAm = response.getBody().getErstelltAm();
			public final String erstelltVon = "updated_erstelltVon";
			public final String geaendertAm = LocalDate.now().toString();
			public final String geaendertVon = "updated_geaendertVon";
		};
		ObjectMapper objMapper = new ObjectMapper();
		String json = objMapper.writeValueAsString(testObj);

		MvcResult result = mvc.perform(put("/greetings/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.characterEncoding("utf-8"))
				.andExpectAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.text").value("updated"),
						jsonPath("$.erstelltAm").value(response.getBody().getErstelltAm()),
						jsonPath("$.erstelltVon").value("updated_erstelltVon"),
						jsonPath("$.geaendertAm").value(LocalDate.now().toString()),
						jsonPath("$.geaendertVon").value("updated_geaendertVon")
				)
				.andReturn();

		MvcResult savedResult = mvc.perform(get("/greetings/1"))
				.andExpect(status().isOk())
				.andReturn();

		Assertions.assertEquals(result.getResponse().getContentAsString(), savedResult.getResponse().getContentAsString());
	}

	@Test
	void whenDeleteRequestIsSentToGreetingControllerWithId_thenItShouldDeleteGreetingWithExactIdAndReturnSuccess() throws Exception {
		MvcResult msg = mvc.perform(delete("/greetings/2"))
				.andExpect(status().isOk())
				.andReturn();
		Assertions.assertEquals("Success", msg.getResponse().getContentAsString());

		mvc.perform(get("/greetings/2"))
				.andExpect(status().isNotFound());
	}

}

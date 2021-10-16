package com.pangaea.publisher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pangaea.publisher.controllers.PublisherOpsController;
import com.pangaea.publisher.models.NotificationMessage;
import com.pangaea.publisher.services.PublisherSubscriberService;

@RunWith(SpringRunner.class)
@WebMvcTest(PublisherOpsController.class)
public class PublisherOpsControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PublisherSubscriberService pubSubService;

	@Test
	public void testIfBadRequestIsReturnedWhenNoRequestBodyIsPassed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/publish/topic")).andExpect(status().isBadRequest());
	}

	@Test
	public void testIfUnsupportedMediaTypeIsReturnsWhenContentTypeIsNotCorrect() throws Exception {
		NotificationMessage nMessage = NotificationMessage.builder().data(null).notificationUrl("http://localhost/test")
				.build();
		mockMvc.perform(MockMvcRequestBuilders.post("/publish/topic").content(objectMapper.writeValueAsString(nMessage))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void testIfOkIsReturnedWhenRequestBodyIsPassed() throws Exception {
		NotificationMessage nMessage = NotificationMessage.builder().data(null).notificationUrl("http://localhost/test")
				.build();
		mockMvc.perform(MockMvcRequestBuilders.post("/publish/topic").content(objectMapper.writeValueAsString(nMessage))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	public void testIfBadRequestIsReturnedWhenNoRequestBodyIsPassed_subscribe() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/subscribe/topic")).andExpect(status().isBadRequest());
	}

	@Test
	public void testIfUnsupportedMediaTypeIsReturnsWhenContentTypeIsNotCorrect_subscribe() throws Exception {
		NotificationMessage nMessage = NotificationMessage.builder().data(null).notificationUrl("http://localhost/test")
				.build();
		mockMvc.perform(MockMvcRequestBuilders.post("/subscribe/topic").content(objectMapper.writeValueAsString(nMessage))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void testIfOkIsReturnedWhenRequestBodyIsPassed_subscribe() throws Exception {
		NotificationMessage nMessage = NotificationMessage.builder().data(null).notificationUrl("http://localhost/test")
				.build();
		mockMvc.perform(MockMvcRequestBuilders.post("/subscribe/topic").content(objectMapper.writeValueAsString(nMessage))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}

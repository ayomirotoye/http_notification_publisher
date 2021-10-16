package com.pangaea.publisher.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pangaea.publisher.models.NotificationMessage;
import com.pangaea.publisher.models.SubscriptionUrl;
import com.pangaea.publisher.services.Publisher;
import com.pangaea.publisher.services.PublisherImpl;
import com.pangaea.publisher.services.PublisherSubscriberService;
import com.pangaea.publisher.services.SubscriberImpl;

@RestController
public class PublisherOpsController {

	PublisherSubscriberService pubSubService;

	@Autowired
	public PublisherOpsController(PublisherSubscriberService pubSubService) {
		this.pubSubService = pubSubService;
	}

	@PostMapping("/publish/{topic}")
	public ResponseEntity<?> doPublish(@PathVariable String topic, @RequestBody NotificationMessage nMessage)
			throws JsonProcessingException {
		Publisher publisher = new PublisherImpl();

		nMessage.setTopic(topic);
		Boolean isSuccessfullyPublished = publisher.publish(nMessage, pubSubService);
		return isSuccessfullyPublished != null && isSuccessfullyPublished ? new ResponseEntity<>(HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@PostMapping("/subscribe/{topicId}")
	public ResponseEntity<?> doSubscribe(@PathVariable String topicId, @RequestBody SubscriptionUrl subscriptionUrl) {
		SubscriberImpl subscriberImpl = new SubscriberImpl();

		subscriberImpl.addSubscriber(topicId, subscriptionUrl, pubSubService);
		subscriptionUrl.setTopic(topicId);

		return new ResponseEntity<>(subscriptionUrl, HttpStatus.OK);

	}
}

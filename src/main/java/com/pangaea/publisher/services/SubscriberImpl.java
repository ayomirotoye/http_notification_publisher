package com.pangaea.publisher.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pangaea.publisher.models.NotificationMessage;
import com.pangaea.publisher.models.Subscriber;
import com.pangaea.publisher.models.SubscriptionUrl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SubscriberImpl extends Subscriber {
	private ObjectMapper objectMapper;
	
	@Override
	public void unSubscribe(String topic, PublisherSubscriberService publisherService) {
		publisherService.removeSubscriber(topic, this);
		
	}

	@Override
	public void getMessagesForTopicSubscriber(String topic, PublisherSubscriberService publisherService) {
		publisherService.getMessagesForTopicSubscriber(topic, this);
	}

	@Override
	public void addSubscriber(String topic, SubscriptionUrl subscriptionUrl, PublisherSubscriberService publisherService) {
		publisherService.addSubscriber(topic, subscriptionUrl, this);
		
	}
	
	public void printMessages() throws JsonProcessingException {
		for (NotificationMessage message : getSubscriberMsgs()) {
			if(objectMapper == null) {
				objectMapper = new ObjectMapper();
			}
			log.info(objectMapper.writeValueAsString(message));
		}
	}


}

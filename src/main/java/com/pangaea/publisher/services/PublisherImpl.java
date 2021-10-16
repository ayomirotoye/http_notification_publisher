package com.pangaea.publisher.services;

import com.pangaea.publisher.models.NotificationMessage;

public class PublisherImpl implements Publisher{
	public Boolean publish(NotificationMessage nMessage, PublisherSubscriberService publisherService) {		
		return publisherService.addMessageToQueue(nMessage);
	}
}

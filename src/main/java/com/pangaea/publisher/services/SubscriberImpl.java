package com.pangaea.publisher.services;

import com.pangaea.publisher.models.Subscriber;
import com.pangaea.publisher.models.SubscriptionUrl;

public class SubscriberImpl extends Subscriber {
	
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

}

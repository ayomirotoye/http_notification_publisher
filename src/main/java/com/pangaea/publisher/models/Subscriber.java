package com.pangaea.publisher.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pangaea.publisher.services.PublisherSubscriberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public abstract class Subscriber {
	

	protected List<NotificationMessage> subscriberMsgs = new ArrayList<NotificationMessage>();
	private String notificationUrl;
	private HashMap<String, String> topicNotificationUrl= new HashMap<>();
	
	

	public List<NotificationMessage> getSubscriberMsgs() {
		return subscriberMsgs;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	public void setSubscriberMsgs(List<NotificationMessage> subMessages) {
		this.subscriberMsgs = subMessages;
	}

	public abstract void unSubscribe(String topic, PublisherSubscriberService publisherService);

	public abstract void getMessagesForTopicSubscriber(String topic, PublisherSubscriberService publisherService);

	public abstract void addSubscriber(String topicId, SubscriptionUrl subscriptionUrl,
			PublisherSubscriberService publisherService);

	public HashMap<String, String> getTopicNotificationUrl() {
		return topicNotificationUrl;
	}

	public void setTopicNotificationUrl(HashMap<String, String> topicNotificationUrl) {
		this.topicNotificationUrl = topicNotificationUrl;
	}

}

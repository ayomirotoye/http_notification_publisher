package com.pangaea.publisher.services;

import com.pangaea.publisher.models.NotificationMessage;

public interface Publisher {
	Boolean publish(NotificationMessage nMessage, PublisherSubscriberService publisherService);
}

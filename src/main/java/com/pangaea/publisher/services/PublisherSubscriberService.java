package com.pangaea.publisher.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pangaea.publisher.configs.RestConnector;
import com.pangaea.publisher.globals.InternalServerErrorException;
import com.pangaea.publisher.models.NotificationMessage;
import com.pangaea.publisher.models.Subscriber;
import com.pangaea.publisher.models.SubscriptionUrl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class PublisherSubscriberService {

	@Autowired
	RestConnector restConnector;

	@Autowired
	ObjectMapper objectMapper;

	Queue<NotificationMessage> messagesQueue = new LinkedList<NotificationMessage>();
	Map<String, Set<Subscriber>> subscribersTopicMap = new HashMap<String, Set<Subscriber>>();

	public void addSubscriber(String topic, SubscriptionUrl subsciptionUrl, Subscriber subscriber) {
		Set<Subscriber> subscribers = null;
		if (subscribersTopicMap.containsKey(topic)) {
			subscribers = subscribersTopicMap.get(topic);
		} else {
			subscribers = new HashSet<Subscriber>();
		}
		HashMap<String, String> topicNotifMapping = new HashMap<>();
		topicNotifMapping.put(topic, subsciptionUrl.getUrl());
		subscriber.setTopicNotificationUrl(topicNotifMapping);
		subscribers.add(subscriber);
		subscribersTopicMap.put(topic, subscribers);
	}

	public void removeSubscriber(String topic, Subscriber subscriber) {
		if (subscribersTopicMap.containsKey(topic)) {
			Set<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.remove(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		}
	}

	public void getMessagesForTopicSubscriber(String topic, Subscriber subscriber) {
		if (messagesQueue.isEmpty()) {
			log.info("================= NO MESSAGE TO PUBLISH YET =============");
		} else {
			while (!messagesQueue.isEmpty()) {
				NotificationMessage message = messagesQueue.remove();
				if (message.getTopic().equalsIgnoreCase(topic)) {
					Set<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
					for (Subscriber _subscriber : subscribersOfTopic) {
						if (_subscriber.equals(subscriber)) {
							List<NotificationMessage> subscriberMessages = subscriber.getSubscriberMsgs();
							subscriberMessages.add(message);
							subscriber.setSubscriberMsgs(subscriberMessages);
						}
					}
				}
			}
		}

	}

	public Boolean addMessageToQueue(NotificationMessage message) {
		messagesQueue.add(message);
		try {
			return broadcast();
		} catch (InternalServerErrorException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean broadcast() throws InternalServerErrorException {
		if (messagesQueue.isEmpty()) {
			log.info("NO MESSAGES TO BROADCAST YET");
			return true;
		} else {
			while (!messagesQueue.isEmpty()) {
				NotificationMessage message = messagesQueue.remove();
				String topic = message.getTopic();

				Set<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);

				if (subscribersOfTopic != null && !subscribersOfTopic.isEmpty()) {
					for (Subscriber subscriber : subscribersOfTopic) {
						List<NotificationMessage> subscriberMessages = subscriber.getSubscriberMsgs();
						subscriberMessages.add(message);
						subscriber.setSubscriberMsgs(subscriberMessages);
						return pushToSubscribers_(subscriberMessages, subscriber);
					}
				} else {
					log.info("NO SUBSCRIBERS TO BROADCAST TO ");
					return true;
				}
			}
		}
		return null;
	}

	private Boolean pushToSubscribers_(List<NotificationMessage> listMessages, Subscriber subscriber)
			throws InternalServerErrorException {
		List<CompletableFuture<ResponseEntity<String>>> notifRes = listMessages.stream().map(x -> {
			String requestBody;
			try {
				requestBody = objectMapper.writeValueAsString(x.getData());

				String topic = x.getTopic();
				String urlToCall = subscriber.getTopicNotificationUrl().get(topic);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				CompletableFuture<ResponseEntity<String>> res = CompletableFuture.supplyAsync(() -> {
					try {
						return restConnector.exchange(requestBody, urlToCall, headers, HttpMethod.POST);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
						return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
					}
				});

				return res;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}).collect(Collectors.toList());
		CompletableFuture<Void> allFutures = CompletableFuture
				.allOf(notifRes.toArray(new CompletableFuture[notifRes.size()]));

		CompletableFuture<List<ResponseEntity<String>>> allAccDetailsResFuture = allFutures.thenApply(v -> {
			return notifRes.stream().map(allAccDetFuture -> allAccDetFuture.join()).collect(Collectors.toList());
		});

		CompletableFuture<List<?>> countFuture = allAccDetailsResFuture.thenApply(accDets -> {
			return accDets.stream().filter(accDet -> accDet.getStatusCode().is2xxSuccessful())
					.collect(Collectors.toList());
		});
		try {
			return countFuture.get().size() == notifRes.size() ? true : false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;

	}

}

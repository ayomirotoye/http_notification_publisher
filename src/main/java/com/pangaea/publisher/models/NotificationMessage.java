package com.pangaea.publisher.models;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessage {
	private HashMap<String, Object> data;
	private String topic;
	private String notificationUrl;
}

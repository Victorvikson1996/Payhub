package com.payhub.notification

import org.springframework.stereotype.Service
import org.springframework.kafka.annotation.KafkaListener

@Service
class NotificationConsumer  {

    @KafkaListener(topics = ["wallet-events", "user-events"], groupId = "payhub-group")
    fun listenEvents(message: String) {
        println("Notification: $message")
    }

}
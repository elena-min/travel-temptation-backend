package org.individualproject.controller;

import lombok.AllArgsConstructor;
import org.individualproject.business.ChatService;
import org.individualproject.business.UserService;
import org.individualproject.domain.NotificationMessage;
import org.individualproject.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;

    @MessageMapping("/chat.sendMessage")
    public ResponseEntity<Void> sendMessage(@Payload NotificationMessage message) {
        chatService.saveNotification(message);
        messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/messages", message);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


//    @GetMapping("/messages/{userId}")
//    public ResponseEntity<List<NotificationMessage>> getMessages(@PathVariable Long  userIdReceiver) {
//        List<NotificationMessage> messages = chatService.getMessagesBetweenUsers(userIdReceiver);
//        return ResponseEntity.ok().body(messages);
//    }

}

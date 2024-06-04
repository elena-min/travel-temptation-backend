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

    @MessageMapping("/chat.send")
    public ResponseEntity<Void> sendMessage(@Payload NotificationMessage message) {
        chatService.saveNotification(message);
        messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/messages", message);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/chats/{userId}")
    public ResponseEntity<List<NotificationMessage>> getChatsForUser(@PathVariable(value = "userId") Long  userId) {
        List<NotificationMessage> messages = chatService.getChatsForUser(userId);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/chat/{fromUserId}/{toUserId}/messages")
    public ResponseEntity<List<NotificationMessage>> getChatMessages(
            @PathVariable Long fromUserId,
            @PathVariable Long toUserId) {

        List<NotificationMessage> messages = chatService.getChatMessages(toUserId, fromUserId);
        return ResponseEntity.ok(messages);
    }

}

package org.individualproject.persistence;

import org.individualproject.persistence.entity.NotificationMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationsRepository  extends JpaRepository<NotificationMessageEntity, Long> {
   // @Query("SELECT n FROM NotificationMessageEntity n where (n.toUser.id = :userIdReceiver AND n.fromUserId = :userIdSender) OR (n.toUser.id = :userIdSender AND n.fromUserId = :userIdReceiver) ORDER BY n.timestamp ASC")
  // List<NotificationMessageEntity> getMessagesBetweenUsers(Long userIdReceiver, Long userIdSender);

}


package org.individualproject.persistence;

import org.individualproject.persistence.entity.NotificationMessageEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationsRepository  extends JpaRepository<NotificationMessageEntity, Long> {

@Query("SELECT n FROM NotificationMessageEntity n " +
        "WHERE n.id = ( " +
        "  SELECT MAX(m.id) FROM NotificationMessageEntity m " +
        "  WHERE (m.toUser.id = :userId OR m.fromUser.id = :userId) AND " +
        "    CASE WHEN m.toUser.id = :userId THEN m.fromUser.id ELSE m.toUser.id END = n.fromUser.id OR " +
        "    CASE WHEN m.toUser.id = :userId THEN m.fromUser.id ELSE m.toUser.id END = n.toUser.id " +
        ")")
    List<NotificationMessageEntity> findAllUniqueChatsForUser(@Param("userId") Long userId);

    List<NotificationMessageEntity> findByToUserAndFromUser(UserEntity toUser, UserEntity fromUser);

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationMessageEntity n WHERE n.toUser.id = :userId OR n.fromUser.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}


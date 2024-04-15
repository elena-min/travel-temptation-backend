package org.individualproject.persistence;

import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<UserEntity, Long> {
}

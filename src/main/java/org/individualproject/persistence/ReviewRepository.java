package org.individualproject.persistence;

import org.individualproject.persistence.entity.ReviewEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository  extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByUserWriter(UserEntity userWriter);

    List<ReviewEntity> findByTravelAgency(UserEntity travelAgency);


}

package org.individualproject.persistence;

import org.individualproject.domain.BookingDataDTO;
import org.individualproject.domain.WeeklyStatisticsDTO;
import org.individualproject.domain.enums.BookingStatus;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository  extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByUser(UserEntity user);
    List<BookingEntity> findByExcursion(ExcursionEntity excursion);

    @Query("SELECT COALESCE(SUM(e.price * b.numberOfTravelers), 0) " +
            "FROM BookingEntity b " +
            "JOIN b.excursion e " +
            "WHERE b.bookingTime >= :startDate AND b.bookingTime <= :endDate AND b.status = :status")
    Double getTotalSalesInLastQuarter(
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate")LocalDateTime endDate,
            @Param("status") BookingStatus status
            );

    @Query("SELECT SUM(e.price * b.numberOfTravelers) AS totalRevenue " +
            "FROM BookingEntity b " +
            "JOIN b.excursion e " +
            "WHERE b.excursion.id = :excursionId " +
            "AND b.bookingTime >= :startDate " +
            "AND b.bookingTime <= :endDate " +
            "AND b.status = :status")
    Double getTotalSalesInLastQuarterForExcursion(
            @Param("excursionId") Long excursionId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") BookingStatus status
    );

    @Query("SELECT new org.individualproject.domain.WeeklyStatisticsDTO(" +
            "FUNCTION('YEAR', b.bookingTime), FUNCTION('WEEK', b.bookingTime), " +
            "SUM(b.numberOfTravelers), SUM(e.price * b.numberOfTravelers)) " +
            "FROM BookingEntity b " +
            "JOIN b.excursion e " +
            "WHERE b.bookingTime <= CURRENT_TIMESTAMP AND b.status = :status " +
            "AND e.id = :excursionId " +
            "GROUP BY FUNCTION('YEAR', b.bookingTime), FUNCTION('WEEK', b.bookingTime)")
    List<WeeklyStatisticsDTO> getWeeklyStatistics(@Param("excursionId") Long excursionId, @Param("status") BookingStatus status);


    @Query("SELECT NEW org.individualproject.domain.BookingDataDTO(b.bookingTime, COUNT(b), SUM(b.numberOfTravelers * e.price)) " +
            "FROM BookingEntity b " +
            "JOIN ExcursionEntity e ON b.excursion.id = e.id " +
            "WHERE b.bookingTime BETWEEN :startDate AND :endDate " +
            "AND e.id = :excursionId " +
            "GROUP BY b.bookingTime")
    List<BookingDataDTO> getBookingDataByDateRange(@Param("excursionId") Long excursionId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}

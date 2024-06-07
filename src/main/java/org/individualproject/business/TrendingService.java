package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.ReviewConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.Review;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.ReviewRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.ReviewEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
public class TrendingService {

    private ReviewRepository reviewRepository;
    private ExcursionRepository excursionRepository;
    private  BookingRepository bookingRepository;

    public List<Excursion> getTrendingExcursion(int limit) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, 6);

        List<Excursion> allExcursions = excursionRepository.findAll().stream()
                                        .map(ExcursionConverter::mapToDomain)
                                        .filter(excursion -> isAfterToday(excursion.getStartDate()) && isBeforeSixMonthsFromToday(excursion.getStartDate()))
                                        .toList();
        return allExcursions.stream()
                .map(this::calculateTrendinessScore)
                .sorted(Comparator.comparingDouble((Map.Entry<Excursion, Double> entry) -> entry.getValue()).reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    private Map.Entry<Excursion, Double> calculateTrendinessScore(Excursion excursion){
        UserEntity travelAgency = UserConverter.convertToEntity(excursion.getTravelAgency());
        List<ReviewEntity> reviewsEntities = reviewRepository.findByTravelAgency(travelAgency);

        List<Review> reviews = ReviewConverter.mapToDomainList(reviewsEntities);
        double averageRating = reviews.stream()
                                .mapToInt(Review::getNumberOfStars)
                                .average()
                                .orElse(0);

        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(excursion);
        int totalBookings = bookingRepository.findByExcursion(excursionEntity).size();

        double trendinessScore = (int) (averageRating * 0.5 + totalBookings * 0.5);

        return Map.entry(excursion, trendinessScore);
    }
    private boolean isAfterToday(Date date) {
        LocalDate today = LocalDate.now();
        Date utilDate = new Date(date.getTime());
        LocalDate dateToCheck = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return dateToCheck.isAfter(today);
    }

    private boolean isBeforeSixMonthsFromToday(Date date) {
        LocalDate today = LocalDate.now();
        LocalDate sixMonthsFromToday = today.plusMonths(6);

        // Convert java.sql.Date to java.util.Date
        Date utilDate = new Date(date.getTime());

        // Convert java.util.Date to LocalDate
        LocalDate dateToCheck = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return dateToCheck.isBefore(sixMonthsFromToday);
    }
}

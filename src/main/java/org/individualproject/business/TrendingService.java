package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.ReviewConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.Review;
import org.individualproject.domain.User;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.ReviewRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.ReviewEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrendingService {

    private ReviewRepository reviewRepository;
    private ExcursionRepository excursionRepository;
    private  BookingRepository bookingRepository;

    public List<Excursion> getTrendingExcursion(int limit) {
        List<Excursion> allExcursions = excursionRepository.findAll().stream()
                                        .map(ExcursionConverter::mapToDomain)
                                        .collect(Collectors.toList());
        List<Excursion> trendingExcursions = allExcursions.stream()
                                            .map(this::calculateTrendinessScore)
                                            .sorted(Comparator.comparingDouble((Map.Entry<Excursion, Double> entry) -> entry.getValue()).reversed())
                                            .limit(limit)
                                            .map(Map.Entry::getKey)
                                            .collect(Collectors.toList());
        return trendingExcursions;
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

        double trendinessScore = (int) (averageRating * 0.5 + totalBookings * 05);

        return Map.entry(excursion, trendinessScore);
    }
}

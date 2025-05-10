package org.individualproject.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_agency_id")
    private UserEntity travelAgency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userWriter;

    @Column(name = "review_date")
    private Date reviewDate;

    @Column(name = "number_of_stars")
    private int numberOfStars;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;
}

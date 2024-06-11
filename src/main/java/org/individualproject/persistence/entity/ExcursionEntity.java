package org.individualproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "excursion")
public class ExcursionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Length(min = 2, max = 100)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Length(min = 2)
    @Column(name = "description")
    private String description;

    @Column(name = "destinations")
    private String destinations;

    @Temporal(TemporalType.DATE)
    @Column(name = "startDate")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "endDate")
    private Date endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "travelAgency_id")
    private UserEntity travelAgency;

    @NotNull
    @Min(0)
    @Column(name = "price")
    private double price;

    @NotNull
    @Column(name = "numberOfAvaliableSpaces")
    private int numberOfAvaliableSpaces;

    @NotNull
    @Column(name = "numberOfSpacesLeft")
    private int numberOfSpacesLeft;

}

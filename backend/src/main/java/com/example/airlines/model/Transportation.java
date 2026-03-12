package com.example.airlines.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "transportations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id")
    private Location origin;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Location destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportationType type;

    @OneToMany(mappedBy = "transportation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportationOperatingDay> operatingDaysEntities = new ArrayList<>();

    /**
    select * from TRANSPORTATIONS as t
    join transportation_operating_days as tod
    on t.id=tod.transportation_id
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public TransportationType getType() {
        return type;
    }

    public void setType(TransportationType type) {
        this.type = type;
    }

    public List<Integer> getOperatingDays() {
        if (operatingDaysEntities == null) {
            return List.of();
        }
        return operatingDaysEntities.stream()
                .map(TransportationOperatingDay::getDayOfWeek)
                .collect(Collectors.toList());
    }

    public void setOperatingDays(List<Integer> days) {
        this.operatingDaysEntities.clear();
        if (days != null) {
            for (Integer day : days) {
                this.operatingDaysEntities.add(new TransportationOperatingDay(this, day));
            }
        }
    }
}

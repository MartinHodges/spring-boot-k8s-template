package com.requillion_solutions.sb_k8s_template.fish;

import com.requillion_solutions.sb_k8s_template.fishtank.FishTankEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "FISHES")
@Getter
@Setter
public class FishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "TYPE")
    private String type;

    @ManyToOne
    @JoinColumn(name = "FISH_TANK_ID")
    private FishTankEntity fishTank;
}

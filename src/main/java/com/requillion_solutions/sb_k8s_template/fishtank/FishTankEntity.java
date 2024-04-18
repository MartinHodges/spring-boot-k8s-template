package com.requillion_solutions.sb_k8s_template.fishtank;

import com.requillion_solutions.sb_k8s_template.fish.FishEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "FISH_TANKS")
@Getter
@Setter
public class FishTankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @OneToMany
    Set<FishEntity> fishes = new HashSet<FishEntity>();

    public void addFish(FishEntity fish) {
        if (fish != null) {
            fishes.add(fish);
            fish.setFishTank(this);
        }
    }

    public void removeFish(FishEntity fish) {
        if (fish != null) {
            fishes.remove(fish);
            fish.setFishTank(null);
        }
    }

    public void removeAllFishes() {
        fishes.forEach(fish -> removeFish(fish));
    }
}

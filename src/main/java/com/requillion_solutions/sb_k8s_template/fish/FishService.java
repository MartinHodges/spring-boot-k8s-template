package com.requillion_solutions.sb_k8s_template.fish;

import com.requillion_solutions.sb_k8s_template.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FishService {

    private final FishRepo fishRepo;

    public FishEntity getFish(UUID id) {

        FishEntity fishTank = fishRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fish not found"));
        return fishTank;
    }

    public Set<FishEntity> getAllFishes() {
        Set<FishEntity> fishTanks = fishRepo.findAll();

        return fishTanks;
    }

    public FishEntity createFish(FishPostDTO dto) {

        FishEntity fish = new FishEntity();

        fish.setType(dto.getType());

        fish = fishRepo.save(fish);

        return fish;
    }

    public FishEntity updateFish(UUID id, FishPostDTO dto) {

        FishEntity fish = getFish(id);

        fish.setType(dto.getType());

        fish = fishRepo.save(fish);

        return fish;
    }

    public void deleteFish(UUID id) {

        fishRepo.deleteById(id);
    }
}

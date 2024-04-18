package com.requillion_solutions.sb_k8s_template.fishtank;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FishTankRepo extends CrudRepository<FishTankEntity, UUID> {

    public Set<FishTankEntity> findAll();
}

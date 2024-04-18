package com.requillion_solutions.sb_k8s_template.fish;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FishRepo extends CrudRepository<FishEntity, UUID> {

    public Set<FishEntity> findAll();

    public void deleteById(UUID id);
}

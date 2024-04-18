package com.requillion_solutions.sb_k8s_template.fishtank;

import com.requillion_solutions.sb_k8s_template.fish.FishEntity;
import com.requillion_solutions.sb_k8s_template.fish.FishGetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class FishTankGetDTO {

    private String id;
    private String name;
    private List<FishGetDTO> fishes;

    static public FishTankGetDTO getDTO(FishTankEntity tank) {
        FishTankGetDTO dto = new FishTankGetDTO(
                tank.getId().toString(),
                tank.getName(),
                FishGetDTO.getDTO(tank.getFishes()));
        return dto;
    }

    static public List<FishTankGetDTO> getDTO(Set<FishTankEntity> tanks) {
        List<FishTankGetDTO> dtos = tanks.stream()
                .map(FishTankGetDTO::getDTO)
                .toList();
        return dtos;
    }
}

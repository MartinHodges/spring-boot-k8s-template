package com.requillion_solutions.sb_k8s_template.fishtank;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FishTankPostDTO {

    @NotEmpty
    String name;
}

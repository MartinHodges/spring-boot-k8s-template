package com.requillion_solutions.sb_k8s_template.fish;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FishPostDTO {

    @NotEmpty
    String type;
}

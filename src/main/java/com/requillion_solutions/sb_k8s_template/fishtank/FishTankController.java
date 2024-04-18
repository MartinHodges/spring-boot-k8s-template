package com.requillion_solutions.sb_k8s_template.fishtank;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = "/api/v1/fish-tanks",  produces = {MediaType.APPLICATION_JSON_VALUE})
public class FishTankController {

    private final FishTankService fishTankService;

    /**
     * Get all fish tanks
     * @return A list of fish tanks
     */
    @Operation(summary = "List all fish tanks")
    @GetMapping
    public ResponseEntity<List<FishTankGetDTO>> getFishTanks(
    ) {
        log.info("Get all fish tanks");

        List<FishTankGetDTO> response = FishTankGetDTO.getDTO(fishTankService.getAllFishTanks());

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific fish tank
     * @param id - the UUID of the fish tank
     * @return THe fish tank (if it exists)
     */
    @Operation(summary = "Fetch fish tank")
    @GetMapping("/{id}")
    public ResponseEntity<FishTankGetDTO> getFishTank(
            @Parameter(description = "Id of fish tank", required = true) @PathVariable("id") UUID id
    ) {
        log.info("Get fish tank {}", id);

        FishTankGetDTO response = FishTankGetDTO.getDTO(fishTankService.getFishTank(id));

        return ResponseEntity.ok(response);
    }

    /**
     * Create a fish
     * @param dto Definition of the fish
     * @return a fish (not in a tank)
     */
    @Operation(summary = "Create a fish tank")
    @PostMapping
    public ResponseEntity<FishTankGetDTO> createFishTank(
            @Parameter(description = "Details of the fish tank to be created", required = true) @RequestBody FishTankPostDTO dto
    ) {
        log.info("Create fish tank");

        FishTankGetDTO response = FishTankGetDTO.getDTO(fishTankService.createFishTank(dto));

        return ResponseEntity.ok(response);
    }

    /**
     * Update the details of the fish tank
     * @param id the UUID of the fish tank to be updated
     * @param dto the details to be changed
     * @return the updated fish tank
     */
    @Operation(summary = "Update fish tank")
    @PutMapping("/{id}")
    public ResponseEntity<FishTankGetDTO> updateFishTank(
            @Parameter(description = "Id of fish tank", required = true) @PathVariable("id") UUID id,
            @Parameter(description = "Details of the fish tank to be updated", required = true) @RequestBody FishTankPostDTO dto
    ) {
        log.info("Update fish tank {}", id);

        FishTankGetDTO response = FishTankGetDTO.getDTO(fishTankService.updateFishTank(id, dto));

        return ResponseEntity.ok(response);
    }

    /**
     * Delete the given fish tank if it exists (idempotent)
     * @param id the UUID of the fish tank to be deleted
     * @return void
     */
    @Operation(summary = "Delete fish tank")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFishTank(
            @Parameter(description = "Id of fish tank", required = true) @PathVariable("id") UUID id
    ) {
        log.info("Delete fish tank {}", id);

        fishTankService.deleteFishTank(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Add the given fish to the given fish tank
     * @param tankId the UUID of the fish tank
     * @param fishId the UUID of the fish to be added to the tank
     * @return void
     */
    @Operation(summary = "Add fish to fish tank")
    @PutMapping("/{tank-id}/fishes/{fish-id}")
    public ResponseEntity<FishTankGetDTO> addFishToTank(
            @Parameter(description = "Id of fish tank", required = true) @PathVariable("tank-id") UUID tankId,
            @Parameter(description = "Id of fish", required = true) @PathVariable("fish-id") UUID fishId
    ) {
        log.info("Add fish {} to fish tank {}", fishId, tankId);

        FishTankGetDTO response = FishTankGetDTO.getDTO(fishTankService.addFishToTank(fishId, tankId));

        return ResponseEntity.ok(response);
    }

    /**
     * Remove the given fish to the given fish tank
     * @param tankId the UUID of the fish tank
     * @param fishId the UUID of the fish to be removed from the tank
     * @return void
     */
    @Operation(summary = "Remove fish from fish tank")
    @DeleteMapping("/{tank-id}/fishes/{fish-id}")
    public ResponseEntity<FishTankGetDTO> removeFishFromTank(
            @Parameter(description = "Id of fish tank", required = true) @PathVariable("tank-id") UUID tankId,
            @Parameter(description = "Id of fish", required = true) @PathVariable("fish-id") UUID fishId
    ) {
        log.info("Remove fish {} from fish tank {}", fishId, tankId);

        FishTankGetDTO response = FishTankGetDTO.getDTO(fishTankService.removeFishFromTank(fishId, tankId));

        return ResponseEntity.ok(response);
    }
}

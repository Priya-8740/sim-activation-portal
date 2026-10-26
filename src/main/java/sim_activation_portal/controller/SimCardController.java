
package sim_activation_portal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sim_activation_portal.entity.SimCard;
import sim_activation_portal.repository.SimCardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/sims")
public class SimCardController {

    private final SimCardRepository simCardRepository;

    public SimCardController(SimCardRepository simCardRepository) {
        this.simCardRepository = simCardRepository;
    }

    // Get all SIM cards
    @GetMapping
    public List<SimCard> getAllSims() {
        return simCardRepository.findAll();
    }

    // Add a new SIM card
    @PostMapping
    public ResponseEntity<SimCard> addSim(@RequestBody SimCard simCard) {
        simCard.setStatus("AVAILABLE");

        SimCard savedSim = simCardRepository.save(simCard);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedSim);
    }

    // Get a SIM card by ID
    @GetMapping("/{id}")
    public ResponseEntity<SimCard> getSimById(@PathVariable Long id) {
        return simCardRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

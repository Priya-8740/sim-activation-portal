
package sim_activation_portal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import sim_activation_portal.entity.ActivationRequest;
import sim_activation_portal.entity.Customer;
import sim_activation_portal.entity.SimCard;
import sim_activation_portal.repository.ActivationRequestRepository;
import sim_activation_portal.repository.CustomerRepository;
import sim_activation_portal.repository.SimCardRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activations")
public class ActivationController {

    private final ActivationRequestRepository activationRepository;
    private final CustomerRepository customerRepository;
    private final SimCardRepository simCardRepository;

    public ActivationController(
            ActivationRequestRepository activationRepository,
            CustomerRepository customerRepository,
            SimCardRepository simCardRepository) {

        this.activationRepository = activationRepository;
        this.customerRepository = customerRepository;
        this.simCardRepository = simCardRepository;
    }

    // View all activation requests
    @GetMapping
    public List<ActivationRequest> getAllRequests() {
        return activationRepository.findAll();
    }

    // Create an activation request
    @PostMapping
    @Transactional
    public ResponseEntity<?> createRequest(
            @RequestBody Map<String, Long> requestData) {

        Long customerId = requestData.get("customerId");
        Long simId = requestData.get("simId");

        if (customerId == null || simId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Customer ID and SIM ID are required."));
        }

        Customer customer = customerRepository.findById(customerId)
                .orElse(null);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found."));
        }

        SimCard sim = simCardRepository.findById(simId)
                .orElse(null);

        if (sim == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "SIM card not found."));
        }

        if (!"AVAILABLE".equals(sim.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "This SIM is not available."));
        }

        sim.setStatus("ASSIGNED");

        ActivationRequest request = new ActivationRequest();
        request.setCustomer(customer);
        request.setSimCard(sim);
        request.setStatus("PENDING");

        ActivationRequest savedRequest =
                activationRepository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "requestId", savedRequest.getId(),
                        "customerId", customer.getId(),
                        "simId", sim.getId(),
                        "status", savedRequest.getStatus(),
                        "message", "Activation request created successfully."
                ));
    }

    // Track a request by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        return activationRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Update activation request status
    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        ActivationRequest request = activationRepository
                .findById(id)
                .orElse(null);

        if (request == null) {
            return ResponseEntity.notFound().build();
        }

        String newStatus = body.get("status");

        if (newStatus == null ||
                !(newStatus.equals("APPROVED")
                || newStatus.equals("REJECTED"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Status must be APPROVED or REJECTED."));
        }

        if (!"PENDING".equals(request.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "Only pending requests can be updated."));
        }

        request.setStatus(newStatus);

        if ("REJECTED".equals(newStatus)) {
            SimCard sim = request.getSimCard();
            sim.setStatus("AVAILABLE");
        }

        activationRepository.save(request);

        return ResponseEntity.ok(Map.of(
                "requestId", request.getId(),
                "status", request.getStatus(),
                "message", "Activation status updated successfully."
        ));
    }
}


package sim_activation_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sim_activation_portal.entity.ActivationRequest;

import java.util.List;

public interface ActivationRequestRepository
        extends JpaRepository<ActivationRequest, Long> {

    List<ActivationRequest> findByStatus(String status);
}


package sim_activation_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sim_activation_portal.entity.SimCard;

public interface SimCardRepository extends JpaRepository<SimCard, Long> {
}

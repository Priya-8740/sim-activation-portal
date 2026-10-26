
package sim_activation_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sim_activation_portal.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

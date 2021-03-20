package ua.com.tracktor.data.deliveryService;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.tracktor.entity.deliveryService.AddressClassificatorItem;

public interface AddressClassificatorRepository  extends JpaRepository<AddressClassificatorItem, Long> {
}

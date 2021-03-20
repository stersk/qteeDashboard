package ua.com.tracktor.entity.deliveryService;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import ua.com.tracktor.entity.enums.DeliveryService;

import javax.persistence.*;

@Entity
@Table(name = "address_classificator_items")
@Data
@Builder
public class AddressClassificatorItem {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "address_classificator_item_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @ManyToOne(targetEntity = AddressClassificatorItem.class)
    @JoinColumn(name = "parent_id")
    @Setter(AccessLevel.NONE)
    private AddressClassificatorItem parent;

    @Column(name = "delivery_service")
    private DeliveryService deliveryService;

    @Column(name = "name_ua")
    @Builder.Default
    private String nameUa = "";

    @Column(name = "name_ru")
    @Builder.Default
    private String nameRu = "";

    @Column(name = "region_ua")
    @Builder.Default
    private String regionUa = "";

    @Column(name = "region_ru")
    @Builder.Default
    private String regionRu = "";

    @Builder.Default
    private Boolean archive = false;

    public void setParent(AddressClassificatorItem parent) {
        if (parent != null && parent.getId() == id) {
            throw new IllegalArgumentException("This object and it's parent shouldn't be the same");
        } else {
            this.parent = parent;
        }
    }
}

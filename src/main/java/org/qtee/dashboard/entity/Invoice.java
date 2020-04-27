package org.qtee.dashboard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
public class Invoice {
    @Id
    private UUID id;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private Account account;

    private String number;
    private LocalDateTime date;
    private Long sum;

    @Column(name = "commission_rate")
    private Long commissionRate;

    private String description;
    private Boolean notified;
}

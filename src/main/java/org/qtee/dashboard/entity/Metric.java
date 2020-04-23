package org.qtee.dashboard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.qtee.dashboard.entity.enums.MetricType;
import org.qtee.dashboard.entity.key.MetricId;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@IdClass(MetricId.class)
@Table(name = "metrics")
@Data
@NoArgsConstructor
public class Metric {
    @Id
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private Account account;

    @Id
    @Column(name = "metric_type")
    private MetricType metricType;

    private LocalDateTime date;
    private Double value;
    private Boolean notify;
}

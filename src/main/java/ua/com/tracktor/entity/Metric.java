package ua.com.tracktor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.enums.MetricType;
import ua.com.tracktor.entity.key.MetricId;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@IdClass(MetricId.class)
@Table(name = "metrics")
@Data
@AllArgsConstructor
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
}

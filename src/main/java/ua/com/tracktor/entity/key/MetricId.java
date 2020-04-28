package ua.com.tracktor.entity.key;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.enums.MetricType;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class MetricId implements Serializable {
    private Long account;
    private MetricType metricType;

    public MetricId(Account account, MetricType metricType) {
        this.account = account.getId();
        this.metricType = metricType;
    }
}

package org.qtee.dashboard.entity.key;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.enums.MetricType;

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

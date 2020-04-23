package org.qtee.dashboard.data;

import org.qtee.dashboard.entity.Metric;
import org.qtee.dashboard.entity.key.MetricId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, MetricId> {

}

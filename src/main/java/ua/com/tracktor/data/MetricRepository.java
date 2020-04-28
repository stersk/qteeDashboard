package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.tracktor.entity.Metric;
import ua.com.tracktor.entity.key.MetricId;

public interface MetricRepository extends JpaRepository<Metric, MetricId> {

}

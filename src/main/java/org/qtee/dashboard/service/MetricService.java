package org.qtee.dashboard.service;

import org.qtee.dashboard.data.MetricRepository;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Metric;
import org.qtee.dashboard.entity.enums.MetricType;
import org.qtee.dashboard.entity.key.MetricId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricService {
    @Autowired
    private MetricRepository metricRepository;

    public void saveAll(List<Metric> metrics){
        metricRepository.saveAll(metrics);
        metricRepository.flush();
    }

    public Metric getMetric(Account account, MetricType metricType) {
        return metricRepository.getOne(new MetricId(account, metricType));
    }

    public Metric updateMetric(Metric metric) {
        return metricRepository.save(metric);
    }
}

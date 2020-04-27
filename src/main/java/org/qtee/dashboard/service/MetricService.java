package org.qtee.dashboard.service;

import org.qtee.dashboard.data.MetricRepository;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Metric;
import org.qtee.dashboard.entity.enums.MetricType;
import org.qtee.dashboard.entity.key.MetricId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MetricService {
    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private InvoiceService invoiceService;

    public void saveAll(List<Metric> metrics){
        metricRepository.saveAll(metrics);
        metricRepository.flush();
    }

    public Metric getMetric(Account account, MetricType metricType) {
        return metricRepository.findById(new MetricId(account, metricType)).orElse(null);
    }

    public Metric updateMetric(Metric metric) {
        return metricRepository.save(metric);
    }

    public void recalculateMetric(Account account) {
        //TODO Make getting price from db
        Long price = 500l;

        LocalDateTime currentDate = LocalDateTime.now();
        Long balance = invoiceService.getTotalSum(account) - shipmentService.getShipmentsCount(account) * price;
        Long shipmentsLeft = balance / price;

        Metric balanceMetric = new Metric(account, MetricType.BALANSE, currentDate, balance.doubleValue()/100, false, "");
        Metric shipmentsLeftMetric = new Metric(account, MetricType.SHIPMENTS_LEFT, currentDate, shipmentsLeft.doubleValue(), false, "");

        metricRepository.save(balanceMetric);
        metricRepository.save(shipmentsLeftMetric);
        metricRepository.flush();
    }
}

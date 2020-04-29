package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.MetricRepository;
import ua.com.tracktor.data.projection.ShipmentDayStat;
import ua.com.tracktor.dto.NotifyDTO;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.entity.Metric;
import ua.com.tracktor.entity.enums.MetricType;
import ua.com.tracktor.entity.key.MetricId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

        Invoice lastInvoice = invoiceService.getLastInvoice(account);
        if (lastInvoice == null) {
            lastInvoice = new Invoice();
            lastInvoice.setSum(0l);
            lastInvoice.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(1l), ZoneId.of("UTC")));
        }

        Long shipmentsCount = 0l;
        Long shipmentsSum = 0l;
        LocalDateTime dateTimeStart = currentDate.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dateTimeEnd = dateTimeStart.plusSeconds(86399);
        List<ShipmentDayStat> shipmentsByDays = shipmentService.getDayStats(dateTimeStart, dateTimeEnd, account);
        if (!shipmentsByDays.isEmpty()) {
            ShipmentDayStat currentStat = shipmentsByDays.get(0);
            shipmentsCount = currentStat.getCount();
            shipmentsSum = currentStat.getSum();
        }

        Metric balanceMetric = new Metric(account, MetricType.BALANSE, currentDate, balance.doubleValue()/100);
        Metric shipmentsLeftMetric = new Metric(account, MetricType.SHIPMENTS_LEFT, currentDate, shipmentsLeft.doubleValue());
        Metric lastInvoiceMetric = new Metric(account, MetricType.LAST_INVOICE, lastInvoice.getDate(), lastInvoice.getSum().doubleValue()/100);
        Metric shipmentsCountByDay = new Metric(account, MetricType.SHIPMENTS_COUNT_BY_DAY, dateTimeStart, shipmentsCount.doubleValue());
        Metric shipmentsSumByDay = new Metric(account, MetricType.SHIPMENTS_SUM_BY_DAY, dateTimeStart, shipmentsSum.doubleValue()/100);

        metricRepository.save(balanceMetric);
        metricRepository.save(shipmentsLeftMetric);
        metricRepository.save(lastInvoiceMetric);
        metricRepository.save(shipmentsCountByDay);
        metricRepository.save(shipmentsSumByDay);
        metricRepository.flush();
    }

    @Nullable
    public NotifyDTO getNotify(Metric metric) {
        NotifyDTO notify = null;

        if (metric == null) {
            return null;
        }

        switch (metric.getMetricType()){
            case BALANSE:
                // show notify about new invoice only once
                Invoice lastInvoice = invoiceService.getLastInvoice(metric.getAccount());
                if (!lastInvoice.getNotified()) {
                    Float sum = (float) lastInvoice.getSum();
                    notify = new NotifyDTO();
                    notify.setText("Ваш рахунок поповнено на " + Math.round(sum/100) + " грн.");
                    notify.setDate(lastInvoice.getDate());

                    lastInvoice.setNotified(true);
                    invoiceService.save(lastInvoice);
                }

                break;
            default:
                notify = null;
        }


        return notify;
    }
}

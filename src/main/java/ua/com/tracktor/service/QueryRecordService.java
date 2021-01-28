package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.QueryRecordRepository;
import ua.com.tracktor.entity.QueryRecord;

import java.util.List;
import java.util.Objects;

@Service
public class QueryRecordService {
    @Autowired
    private QueryRecordRepository queryRecordRepository;

    @Autowired
    private Environment env;

    public Long save(QueryRecord queryRecord) {
        return queryRecordRepository.save(queryRecord).getId();
    }

    public void updateFilteredFlag(Long id, Boolean filtered){
        queryRecordRepository.updateFilteredFlag(id, filtered);
    }

    public List<QueryRecord> getTopTenQueryRecords(){
        return queryRecordRepository.findTop10ByOrderByIdDesc();
    }

    public void delete(QueryRecord queryRecord){
        queryRecordRepository.delete(queryRecord);
    }

    @Async
    @Scheduled(cron = "${delivery-service.send-query-log.scheduler.cron-expression}")
    public void uploadAllRecordsToServer(){
        System.out.println("Run");

        String server = env.getProperty("delivery-service.server.address");
        String basePath = env.getProperty("delivery-service.server.path") + "/query-log";
        String userName = env.getProperty("delivery-service.server.user");
        String password = env.getProperty("delivery-service.server.password");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("delivery-service.server.port")));

//        HttpHeaders headers = new HttpHeaders();
//        RestUtil.addBasicAuthorizationHeader(headers, userName, password);
//
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters()
//                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
//
//        ResponseEntity<String> responseEntity;
//
//        List<QueryRecord> records = getTopTenQueryRecords();
//        while (!records.isEmpty()) {
//            for (QueryRecord record: records) {
//                HttpEntity<QueryRecord> httpEntity = new HttpEntity<>(record, headers);
//
//                try {
//                    URI uri = new URI("https", null, server, port, basePath, "", null);
//                    responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
//
//                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                        delete(record);
//                    } else {
//                        return;
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//
//            records = getTopTenQueryRecords();
//        }
    }
}

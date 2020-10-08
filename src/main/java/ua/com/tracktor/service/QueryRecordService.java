package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.QueryRecordRepository;
import ua.com.tracktor.entity.QueryRecord;

@Service
public class QueryRecordService {
    @Autowired
    private QueryRecordRepository queryRecordRepository;

    public Long save(QueryRecord queryRecord) {
        return queryRecordRepository.save(queryRecord).getId();
    }

    public void updateFilteredFlag(Long id, Boolean filtered){
        queryRecordRepository.updateFilteredFlag(id, filtered);
    }
}

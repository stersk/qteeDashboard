package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.com.tracktor.entity.QueryRecord;

import java.util.List;


public interface QueryRecordRepository extends CrudRepository<QueryRecord, Long> {
    @Transactional
    @Modifying
    @Query(value =
            "UPDATE QueryRecord queryRecord " +
            "SET queryRecord.filtered = :filtered " +
            "WHERE queryRecord.id = :id")
    void updateFilteredFlag(@Param("id") Long id, @Param("filtered") Boolean filtered);
    List<QueryRecord> findTop10ByOrderByIdDesc();
}
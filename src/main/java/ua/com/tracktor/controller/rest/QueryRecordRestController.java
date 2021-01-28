package ua.com.tracktor.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.tracktor.service.QueryRecordService;

@RestController
@RequestMapping("/services/query-record")
public class QueryRecordRestController {
    @Autowired
    QueryRecordService queryRecordService;

    @Autowired
    private Environment env;

    @Autowired
    private QueryRecordRestController controllerForAsync;

    @GetMapping(path = "/upload-all")
    public ResponseEntity<String> uploadQueriesRequest() {

        queryRecordService.uploadAllRecordsToServer();

        return new ResponseEntity<>("Upload process started", HttpStatus.OK);
    }
}

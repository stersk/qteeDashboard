package ua.com.tracktor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "query_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private Account account;

    private String uri;
    private Boolean filtered;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    @Lob @Type(type = "org.hibernate.type.TextType")
    @Column(name = "request_body")
    private String requestBody;

    @Lob @Type(type = "org.hibernate.type.TextType")
    @Column(name = "request_headers")
    private String requestHeaders;

    @Column(name = "response_status_code")
    private Integer responseStatusCode;

    @Lob @Type(type = "org.hibernate.type.TextType")
    @Column(name = "response_body")
    private String responseBody;

    @Lob @Type(type = "org.hibernate.type.TextType")
    @Column(name = "response_headers")
    private String responseHeaders;

    @Column(name = "source_ip")
    private String sourceIp;
}

package xyz.luomu32.config.server.console.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String application;

    private String profile;

    private String configKey;

    @Enumerated(EnumType.STRING)
    private LogChangeType changeType;

    private String content;

    private LocalDateTime createdDatetime;

    private Long operatorId;

    private String operatorName;
}

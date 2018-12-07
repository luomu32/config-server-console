package xyz.luomu32.config.server.console.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_config_server")
public class ConfigServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ConfigServerType type;

    private String url;

    private String prefix;
}




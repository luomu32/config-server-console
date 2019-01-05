package xyz.luomu32.config.server.console.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tb_menu")
public class Menu implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String title;

    private String icon;

    private String url;

    private String serverUrl;
}

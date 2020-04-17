package xyz.luomu32.config.server.console.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tb_menu_action")
public class MenuAction implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Long menuId;

    private String name;

    private String type;

    private String url;

    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private PermissionHttpMethod httpMethod;
}

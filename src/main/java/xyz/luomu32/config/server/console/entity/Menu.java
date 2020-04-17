package xyz.luomu32.config.server.console.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@Table(name = "tb_menu")
public class Menu implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "father_id")
    private Long fatherId;

    private String name;

    private String title;

    private String icon;

    private String url;

    @JsonIgnore
    private String serverUrl;

    //TODO N+1问题
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "father_id")
    private Set<Menu> children;
}

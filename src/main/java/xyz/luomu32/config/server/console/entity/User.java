package xyz.luomu32.config.server.console.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private String username;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private DeleteFlagEnum deleted = DeleteFlagEnum.UN_DELETED;

    private LocalDateTime createdDatetime;

    private LocalDateTime lastModifyDatetime;
}

package xyz.luomu32.config.server.console.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_role_action_permission")
public class RoleActionPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roleId;

    private Long menuActionId;
}

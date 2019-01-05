package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.luomu32.config.server.console.entity.RoleActionPermission;

import java.util.List;

public interface RoleActionPermissionRepo extends JpaRepository<RoleActionPermission, Long> {

    List<RoleActionPermission> findByRoleId(Long roleId);

    Long deleteByMenuActionId(Long menuActionId);
}

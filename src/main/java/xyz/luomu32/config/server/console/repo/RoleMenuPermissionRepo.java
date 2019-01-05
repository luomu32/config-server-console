package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.luomu32.config.server.console.entity.RoleMenuPermission;

import java.util.List;

public interface RoleMenuPermissionRepo extends JpaRepository<RoleMenuPermission, Long> {

    List<RoleMenuPermission> findByRoleId(Long roleId);

    Long deleteByMenuId(Long menuId);
}

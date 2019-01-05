package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.luomu32.config.server.console.entity.MenuAction;

import java.util.List;

public interface MenuActionRepo extends JpaRepository<MenuAction, Long> {


    List<MenuAction> findByMenuId(Long menuId);
}

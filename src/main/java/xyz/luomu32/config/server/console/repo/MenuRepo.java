package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.luomu32.config.server.console.entity.Menu;

public interface MenuRepo extends JpaRepository<Menu, Long> {
}

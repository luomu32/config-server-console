package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.luomu32.config.server.console.entity.UserApplication;

import java.util.List;
import java.util.Optional;

public interface UserApplicationRepo extends JpaRepository<UserApplication, Long> {

    List<UserApplication> findByUserId(Long userId);

    Optional<UserApplication> findByUserIdAndApplication(Long userId, String application);
}

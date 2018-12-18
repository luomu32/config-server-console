package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.luomu32.config.server.console.entity.DeleteFlagEnum;
import xyz.luomu32.config.server.console.entity.User;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepo extends JpaRepository<User, Long> {


    Optional<User> findByUsernameAndDeleted(String username, DeleteFlagEnum deleteFlag);

    Optional<User> findByIdAndDeleted(Long id, DeleteFlagEnum deleteFlag);

    Stream<User> findByRoleIdAndDeleted(Long roleId, DeleteFlagEnum deleteFlag);
}

package xyz.luomu32.config.server.console.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.luomu32.config.server.console.entity.Log;

public interface LogRepo extends JpaRepository<Log, Long>, QuerydslPredicateExecutor, JpaSpecificationExecutor {

}

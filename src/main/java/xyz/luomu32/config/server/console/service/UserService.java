package xyz.luomu32.config.server.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.luomu32.config.server.console.entity.DeleteFlagEnum;
import xyz.luomu32.config.server.console.repo.UserApplicationRepo;
import xyz.luomu32.config.server.console.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserApplicationRepo userApplicationRepo;


    @Transactional
    public void delete(Long id) {
        userRepo.findById(id).ifPresent(user -> {
            user.setDeleted(DeleteFlagEnum.DELETED);
            userRepo.save(user);

            userApplicationRepo.deleteByUserId(id);
        });
    }
}

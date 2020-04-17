package xyz.luomu32.config.server.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.luomu32.config.server.console.entity.DeleteFlagEnum;
import xyz.luomu32.config.server.console.entity.Role;
import xyz.luomu32.config.server.console.entity.User;
import xyz.luomu32.config.server.console.repo.RoleRepo;
import xyz.luomu32.config.server.console.repo.UserApplicationRepo;
import xyz.luomu32.config.server.console.repo.UserRepo;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
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

    @Transactional
    public void update(Long userId, Long roleId) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new ServiceException(ServiceExceptionEnum.USER_NOT_FOUND));

        if (user.getRole().getId().equals(roleId))
            return;

        Role role = roleRepo.findById(roleId).orElseThrow(() -> new ServiceException(ServiceExceptionEnum.ROLE_NOT_FOUND));

        user.setRole(role);
        user.setLastModifyDatetime(LocalDateTime.now());
        userRepo.save(user);
    }

    public void resetPassword(Long userId, String newPassword) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new ServiceException(ServiceExceptionEnum.USER_NOT_FOUND));

        user.setPassword(newPassword);
        user.setLastModifyDatetime(LocalDateTime.now());
        userRepo.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepo.findById(userId).orElseThrow(() ->
                new ServiceException(ServiceExceptionEnum.USER_NOT_FOUND));

        if (!user.getPassword().equalsIgnoreCase(oldPassword))
            throw new ServiceException(ServiceExceptionEnum.USER_PASSWORD_NOT_MATCH);

        user.setPassword(newPassword);
        user.setLastModifyDatetime(LocalDateTime.now());
        userRepo.save(user);
    }
}

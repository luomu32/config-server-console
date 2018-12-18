package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.DeleteFlagEnum;
import xyz.luomu32.config.server.console.entity.Role;
import xyz.luomu32.config.server.console.entity.User;
import xyz.luomu32.config.server.console.entity.UserApplication;
import xyz.luomu32.config.server.console.pojo.UserPojo;
import xyz.luomu32.config.server.console.pojo.UserPrincipal;
import xyz.luomu32.config.server.console.repo.UserApplicationRepo;
import xyz.luomu32.config.server.console.repo.UserRepo;
import xyz.luomu32.config.server.console.service.UserService;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserApplicationRepo userApplicationRepo;

    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public User findById(@PathVariable Long id) {
        return userRepo.findByIdAndDeleted(id, DeleteFlagEnum.UN_DELETED).orElse(null);
    }

    @GetMapping
    public Page<User> findAll(Pageable page) {
        User query = new User();
        query.setDeleted(DeleteFlagEnum.UN_DELETED);

        return userRepo.findAll(Example.of(query), page);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @PostMapping
    public void create(@Validated(UserPojo.CreateValid.class) @RequestBody UserPojo userPojo) {

        userRepo.findByUsernameAndDeleted(userPojo.getName(), DeleteFlagEnum.UN_DELETED).ifPresent(u -> {
                    throw new ServiceException(ServiceExceptionEnum.CONFIG_SERVER_NOT_FOUND);
                }
        );

        User user = new User();
        Role role = new Role();
        role.setId(userPojo.getRoleId());
        user.setRole(role);
        user.setUsername(userPojo.getName());
        user.setPassword(userPojo.getPassword());
        user.setCreatedDatetime(LocalDateTime.now());
        user.setLastModifyDatetime(user.getCreatedDatetime());
        userRepo.save(user);
    }

    /**
     * 获取当前登录用户，可管理的应用列表
     *
     * @return
     */
    @GetMapping("application")
    public List<String> applications(UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        return applications(userId);
    }

    @GetMapping("{id}/application")
    public List<String> applications(@PathVariable Long id) {
        return userApplicationRepo.findByUserId(id)
                .stream()
                .map(u -> u.getApplication())
                .collect(Collectors.toList());
    }

    @PostMapping("{id}/application")
    public void addApplication(@PathVariable Long id,
                               @RequestParam String[] applications) {

        Stream.of(applications).forEach(a -> {
            UserApplication userApplication = new UserApplication();
            userApplication.setUserId(id);
            userApplication.setApplication(a);
            userApplicationRepo.save(userApplication);
        });
    }

    @DeleteMapping("{id}/application")
    public void deleteApplication(@PathVariable Long id,
                                  @RequestParam String[] applications) {

        Stream.of(applications).forEach(a -> {
            userApplicationRepo.findByUserIdAndApplication(id, a).ifPresent(ua -> {
                userApplicationRepo.delete(ua);
            });
        });
    }
}

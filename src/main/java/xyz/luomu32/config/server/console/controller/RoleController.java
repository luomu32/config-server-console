package xyz.luomu32.config.server.console.controller;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.Role;
import xyz.luomu32.config.server.console.pojo.RolePojo;
import xyz.luomu32.config.server.console.repo.RoleRepo;
import xyz.luomu32.config.server.console.service.RoleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private RoleService roleService;

    @GetMapping()
    public List<Role> getAllRoles(@RequestParam(required = false) String name) {
        Role query = new Role();
        query.setName(name);
        return roleRepo.findAll(Example.of(query));
    }

    @GetMapping("{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleRepo.findById(id).orElse(null);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @PutMapping
    public void update(@Validated @RequestBody RolePojo role) {
        Role r = new Role();
        r.setId(role.getId());
        r.setName(role.getName());
        r.setLastModifyDatetime(LocalDateTime.now());
        roleRepo.save(r);
    }

    @PostMapping
    public void create(@Validated @RequestBody RolePojo role) {
        Role r = new Role();
        r.setName(role.getName());
        r.setCreatedDatetime(LocalDateTime.now());
        r.setLastModifyDatetime(r.getCreatedDatetime());
        roleRepo.save(r);
    }

    @GetMapping("menus")
    public List<String> menus() {
        return Stream.of("application", "config", "log").collect(Collectors.toList());
    }
}

package xyz.luomu32.config.server.console.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.luomu32.config.server.console.entity.Log;
import xyz.luomu32.config.server.console.repo.LogRepo;

import java.time.LocalDate;

@RestController
@RequestMapping("log")
public class LogController {

    @Autowired
    private LogRepo logRepo;

    @GetMapping
    public Slice<Log> query(@RequestParam(required = false) String application,
                            @RequestParam(required = false) String operatorName,
                            @RequestParam(required = false) LocalDate startDate,
                            @RequestParam(required = false) LocalDate endDate,
                            @PageableDefault(sort = "createdDatetime", direction = Sort.Direction.DESC) Pageable page) {

        return logRepo.findAll(((Specification) (root, query, criteriaBuilder) -> {
            if (startDate == null)
                return null;

            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdDatetime").as(LocalDate.class), startDate);
        }).and((Specification) (root, query, criteriaBuilder) -> {
            if (endDate == null)
                return null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("createdDatetime").as(LocalDate.class), endDate);
        }).and((Specification) (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(application))
                return null;

            return criteriaBuilder.equal(root.get("application"), application);
        }).and((Specification) (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(operatorName))
                return null;

            return criteriaBuilder.equal(root.get("operatorName"), operatorName);
        }), page);
    }
}

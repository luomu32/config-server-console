package xyz.luomu32.config.server.console.entity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;


public enum DeleteFlagEnum {

    @Enumerated(EnumType.STRING)
    DELETED,
    @Enumerated(EnumType.STRING)
    UN_DELETED
}

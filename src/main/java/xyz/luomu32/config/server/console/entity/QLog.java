package xyz.luomu32.config.server.console.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLog is a Querydsl query type for Log
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLog extends EntityPathBase<Log> {

    private static final long serialVersionUID = -1145690139L;

    public static final QLog log = new QLog("log");

    public final StringPath application = createString("application");

    public final EnumPath<LogChangeType> changeType = createEnum("changeType", LogChangeType.class);

    public final StringPath configKey = createString("configKey");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdDatetime = createDateTime("createdDatetime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> operatorId = createNumber("operatorId", Long.class);

    public final StringPath operatorName = createString("operatorName");

    public final StringPath profile = createString("profile");

    public QLog(String variable) {
        super(Log.class, forVariable(variable));
    }

    public QLog(Path<? extends Log> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLog(PathMetadata metadata) {
        super(Log.class, metadata);
    }

}


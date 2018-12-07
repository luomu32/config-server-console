#config server console
配置中心管理台。
支持多种数据源
- 支持ZooKeeper
- 支持Git
- 支持Consul

支持profile，配置加密

## TODO
- [x] 全局化的Jackson反序列化配置，日期格式的格式指定，包括Java8的LocalDateTime等
- [ ] 全局化的日期Formatter
- [ ] 实体验证失败的，全局默认异常处理器
- [ ] JPA实体类的审计，@CreatedBy等
- [ ] Pageable参数直接实例化
- [ ] 多种JPA高级查询方式的支持
- [ ] JPA，ID默认生成策略的配置，目前MySQL默认采用table的方式，除非手动指定自增的方式
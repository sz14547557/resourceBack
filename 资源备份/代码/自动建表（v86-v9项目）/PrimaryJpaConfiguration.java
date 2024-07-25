package com.eplugger;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.eplugger.extend.Init;
import com.eplugger.service.dao.BaseDAOImpl;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA配置
 * <p>数据源配置
 * <p>JPA查询对接配置
 * <p>jdbctemplate配置
 * <p>事务配置
 * <p>为多数据源提供扩展
 *
 * @author sun'afei
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary", // 持久化实例工厂
        transactionManagerRef = "transactionManagerPrimary", // 事务
        basePackages = {PrimaryJpaConfiguration.BASE_PACKAGE_REPOSITORY}, // 设置Repository所在位置
        repositoryBaseClass = BaseDAOImpl.class, // 自定义JpaRepository实现
        enableDefaultTransactions = false // 关闭JPA默认事务
)
@EntityScan(basePackages = PrimaryJpaConfiguration.BASE_PACKAGE_ENTITY)
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class PrimaryJpaConfiguration implements BeanFactoryAware {

    /**
     * 设置Repository路径，即dao层路径，当前数据源只作用于此路径
     */
    public static final String BASE_PACKAGE_REPOSITORY = "com.eplugger.**.dao";
    /**
     * 设置持久化对象路径，即entity路径，当前数据源只作用于此路径
     */
    public static final String BASE_PACKAGE_ENTITY = "com.eplugger.**.entity";
    /**
     * 定义切入点,事务只作用于此路径Controller
     */
    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.eplugger..controller.*Controller.*(..))";

    @Resource
    private JpaProperties jpaProperties;
    @Resource
    private HibernateProperties hibernateProperties;

    @Primary
    @Bean(value = "dataSource")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        log.debug("Init PrimaryDataSource");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("jdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(primaryDataSource());
    }

    @Bean("namedParameterJdbcTemplate")
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(primaryDataSource());
    }

    @Primary
    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(primaryDataSource())// 设置数据源
                .properties(getVendorProperties())// 设置hibernate配置
                .packages(BASE_PACKAGE_ENTITY) //设置实体类所在位置
                .persistenceUnit("primaryPersistenceUnit")// 设置持久化单元名，用于@PersistenceContext注解获取EntityManager时指定数据源
                .build();
    }

    private Map getVendorProperties() {
        Map vendorProps=hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        //首次由系统sql生成表
        if(instanceFactory.getBean(Init.class).determineWhether2Initialize()) {
            //vendorProps.remove(AvailableSettings.HBM2DDL_AUTO);
            vendorProps.put(AvailableSettings.HBM2DDL_AUTO,"none");
            vendorProps.put("hibernate.ddl-auto","none");
        }
        return vendorProps;
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public TransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }

    @Primary
    @Bean("txAdvicePrimary")
    public TransactionInterceptor txAdvice(@Qualifier("transactionManagerPrimary") TransactionManager transactionManagerPrimary) {
        // 方法命名匹配开启事务source
        NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource = new NameMatchTransactionAttributeSource();
        // 只读事务，不做更新操作
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setTimeout(300);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("do_*", requiredTx);
        txMap.put("to_*", readOnlyTx);
        nameMatchTransactionAttributeSource.setNameMap(txMap);

        // 注解开启事务source
        AnnotationTransactionAttributeSource annotationTransactionAttributeSource = new AnnotationTransactionAttributeSource();

        // 合并两个source，即两个方法均可开启事务
        CompositeTransactionAttributeSource source = new CompositeTransactionAttributeSource(nameMatchTransactionAttributeSource, annotationTransactionAttributeSource);
        return new TransactionInterceptor(transactionManagerPrimary, source);
    }

    /**
     * 切面拦截规则 参数会自动从容器中注入
     */
    @Primary
    @Bean("defaultPointcutAdvisorPrimary")
    public DefaultPointcutAdvisor defaultPointcutAdvisor(@Qualifier("txAdvicePrimary") TransactionInterceptor txAdvicePrimary) {
        DefaultPointcutAdvisor pointcutAdvisor = new DefaultPointcutAdvisor();
        pointcutAdvisor.setAdvice(txAdvicePrimary);
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        pointcutAdvisor.setPointcut(pointcut);
        return pointcutAdvisor;
    }
    private BeanFactory instanceFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.instanceFactory=beanFactory;
    }

}
/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.OrganismPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 *
 * @author aacain
 */
public class DataManagerWeb extends DataManager implements PropertyChangeListener {

    private List<? extends Organism> orgList;
    private List<? extends AnnotationMethod> methodList;
    private boolean dbSet = Boolean.TRUE;
    private final Connection connection;
    private ServiceManager serviceManager;

    public DataManagerWeb(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List createQuery(final String query) {
        return serviceManager.createQuery(query);
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type) {
        return serviceManager.createQuery(query, type);
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult) {
        return serviceManager.createQuery(query, type, firstResult, maxResult);
    }

    @Override
    public synchronized List<Organism> getOrganisms() {
        if (dbSet) {
            orgList = createQuery("select org from Organism as org order by org.strain", Organism.class);
            return Collections.unmodifiableList(orgList);
        }
        return Collections.EMPTY_LIST;
    }

    //@Override
    public synchronized List<? extends AnnotationMethod> getAnnotationMethods() {
        if (dbSet) {

            methodList = createQuery("select method from AnnotationMethod as method", AnnotationMethod.class);
            return Collections.unmodifiableList(methodList);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public String getDatabaseColumnName(String entityName, String propertyName) {
        return serviceManager.getDatabaseColumnName(entityName, propertyName);
    }

    @Override
    public String getDatabaseTableName(String entityName) {
        return serviceManager.getDatabaseTableName(entityName);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return serviceManager.get(entityName, id);
    }

    @Override
    public List<Object[]> createNativeQuery(String query) {
        return serviceManager.createNativeSQLQuery(query);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements) {
        serviceManager.createNativeStatement(statements);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements, boolean notify) {
        serviceManager.createNativeStatement(statements);
    }

    @Override
    public void persist(List<? extends Persister> persisters) {
        serviceManager.persist(persisters);
    }

    @Override
    public synchronized String getDatabaseName() {
        return connection.getConnectionName();
    }

    @Override
    public String getConnectionId() {
        return connection.getConnectionId();
    }

    @Override
    public synchronized void save(CustomizableEntity object) {
        serviceManager.save(object);
    }

    @Override
    public boolean isDatabaseSet() {
        return dbSet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void connect() throws InvalidConnectionException {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//        HttpInvokerProxyFactoryBean bean = (HttpInvokerProxyFactoryBean) context.getBean("&genosetsWeb");
//        bean.setServiceUrl(connection.getUrl() + "testService.service");
//        serviceManager = (ServiceManager) context.getBean("genosetsWeb");
//        SecurityContextImpl sc = new SecurityContextImpl();
//        Authentication auth = new UsernamePasswordAuthenticationToken(connection.getUserName(), connection.getPassword());
//        sc.setAuthentication(auth);
//        SecurityContextHolder.setContext(sc);
//        OrganismPersister.addPropertyChangeListener(this);
//        OrthologTableCreator.listenToDataManager(this);
        GenericApplicationContext context = new GenericApplicationContext() {
            @Override
            protected ResourcePatternResolver getResourcePatternResolver() {
                return new PathMatchingResourcePatternResolverEx(this);
            }
        };
        //set Spring's classloader to context classloader
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
        //xmlReader.loadBeanDefinitions(new ClassPathResource("spring.xml", DataManagerWeb.class));
        xmlReader.loadBeanDefinitions(new ClassPathResource("spring.xml"));
        context.refresh();
        HttpInvokerProxyFactoryBean bean = (HttpInvokerProxyFactoryBean) context.getBean("&genosetsWeb");
        bean.setServiceUrl(connection.getUrl() + "testService.service");
        serviceManager = (ServiceManager) context.getBean("genosetsWeb");
        SecurityContextImpl sc = new SecurityContextImpl();
        Authentication auth = new UsernamePasswordAuthenticationToken(connection.getUserName(), connection.getPassword());
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);
        OrganismPersister.addPropertyChangeListener(this);
        OrthologTableCreator.listenToDataManager(this);
    }

    @Override
    public List createNativeSQLQuery(String query) {
        return serviceManager.createNativeSQLQuery(query);
    }

    @Override
    public Object initializeLazy(CustomizableEntity obj, String property) {
        return serviceManager.initializeLazy(obj, property);
    }

    private static class PathMatchingResourcePatternResolverEx extends PathMatchingResourcePatternResolver {

        public PathMatchingResourcePatternResolverEx(ResourceLoader resourceLoader) {
            super(resourceLoader);
        }

        public PathMatchingResourcePatternResolverEx(ClassLoader classLoader) {
            super(classLoader);
        }

        public PathMatchingResourcePatternResolverEx() {
            super();
        }

        @Override
        protected boolean isJarResource(Resource resource) throws IOException {
            return super.isJarResource(resource) || "nbjcl".equals(resource.getURL().getProtocol());
        }
    }
}

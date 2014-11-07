package edu.uncc.genosets.datamanager.hibernate;

import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.StudySetExtended;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author aacain
 */
public class HibernateUtil_spring {

    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public List createQuery(String query) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery(query);
        List result = session.createQuery(query).list();
        return result;
    }

    @Transactional(readOnly = false)
    public boolean save(CustomizableEntity obj) {
        Session session = sessionFactory.getCurrentSession();
        session.save(obj.getEntityName(), obj);
        //session.getTransaction().commit();
        return true;
    }

    @Transactional(readOnly = true)
    public String getColumnName(String entityName, String propertyName) {
//        Configuration ac = sessionFactory.getConfiguration();
//        PersistentClass classMapping = ac.getClassMapping(entityName);
//        Property property = classMapping.getProperty(propertyName);
//        for (Iterator<Column> it = property.getColumnIterator(); it.hasNext();) {
//            Column c = it.next();
//            return c.getName();
//        }
//
//        return null;

        ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        if (classMetadata == null) {
            return null;
        }
        if (classMetadata instanceof AbstractEntityPersister) {
            AbstractEntityPersister persister = (AbstractEntityPersister) classMetadata;
            return persister.getTableName();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String getTableName(String entityName) {
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityName);
        if (classMetadata == null) {
            return null;
        }
        if (classMetadata instanceof AbstractEntityPersister) {
            AbstractEntityPersister persister = (AbstractEntityPersister) classMetadata;
            return persister.getTableName();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Object get(String entityName, Serializable id) {
        Session session = sessionFactory.getCurrentSession();
        Object result = session.get(entityName, id);
        return result;
    }

    @Transactional(readOnly = true)
    public List createNativeQuery(final String query) {
        Session session = sessionFactory.getCurrentSession();
        return session.createSQLQuery(query).list();
    }

    @Transactional(readOnly = false)
    public void createNativeStatement(ArrayList<String> statements) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        for (String statement : statements) {
            SQLQuery sqlQuery = session.createSQLQuery(statement);
            sqlQuery.executeUpdate();
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = false)
    public void persist(List<? extends Persister> persisters) throws Exception {
        StatelessSession session = sessionFactory.openStatelessSession();
//        session.beginTransaction();
        if (persisters != null) {
            try {
                for (Persister persister : persisters) {
                    persister.persist(session);
                }
            } catch (Exception ex) {
            }
        }
    }

    @Transactional(readOnly=true)
    public Object get(Object obj, String property) {
        Session session = sessionFactory.getCurrentSession();
        Object merge = session.merge(obj);
        try {
            BeanInfo info = Introspector.getBeanInfo(StudySetExtended.class);
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor desc : propertyDescriptors) {
                if (desc.getName().equals(property)) {
                    Method readMethod = desc.getReadMethod();
                    Hibernate.initialize(readMethod.invoke(merge, (Object[]) null));
                    break;
                }
            }
        } catch (IntrospectionException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return merge;
    }
}

/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dao;

import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Example;

/**
 *
 * @author aacain
 */
@Deprecated
public abstract class LocationDAO {

    public static LocationDAO instantiate() {
        return new LocationDAOImpl();
    }

    public abstract List<Location> getLocation(Location lookup);

    @Deprecated
    public static class LocationDAOImpl extends LocationDAO {

        @Override
        public List<Location> getLocation(Location lookup) {
            StatelessSession session = HibernateUtil.currentSession();
            Criteria crit = session.createCriteria(Location.class, Location.DEFAULT_NAME);
            Example example = Example.create(lookup);
            crit.add(example);
            List<Location> result = crit.list();
            HibernateUtil.closeSession();
            return result;
        }
    }
}

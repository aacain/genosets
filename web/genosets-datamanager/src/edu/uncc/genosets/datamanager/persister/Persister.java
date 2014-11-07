/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.persister;

import java.io.Serializable;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
public interface Persister extends Serializable{
    public void persist(StatelessSession session);
}

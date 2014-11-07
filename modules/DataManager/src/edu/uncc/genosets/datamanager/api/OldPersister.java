/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
@Deprecated
public interface OldPersister {


    public boolean persist(StatelessSession session, boolean needslookup);
    

    public static class Fact<E extends CustomizableEntity, F extends CustomizableEntity>{
        private E entity;
        private F fact;

        public Fact(E entity, F factEntity) {
            this.entity = entity;
            this.fact = factEntity;
        }

        public E getEntity() {
            return entity;
        }

        public F getFactEntity(){
            return fact;
        }

        public void setEntity(E entity) {
            this.entity = entity;
        }

        public void setFact(F fact) {
            this.fact = fact;
        }
    }
}

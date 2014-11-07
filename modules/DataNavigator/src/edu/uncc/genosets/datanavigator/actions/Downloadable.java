/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator.actions;

import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.util.List;

/**
 *
 * @author aacain
 */
public interface Downloadable<T extends FactType> {
    public Downloadable instantiate();
    public T getType();
    public void download(List<AnnotationMethod> methods);
}


package edu.uncc.genosets.taskmanager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lucy
 */
public class TaskLogFactoryTest {
    

    /**
     * Test of getDefault method, of class TaskLogFactory.
     */
    @Test
    public void testGetDefault() {
        System.out.println("getDefault");
        TaskLog result = TaskLogFactory.getDefault();
        assertNotNull(result);
    }
}
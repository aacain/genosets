/*
 * Copyright (C) 2014 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.taskmanager;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author lucy
 */
public class TaskManagerTest {

    public TaskManagerTest() {
    }

    /**
     * Test of addPendingTask method, of class TaskManager.
     */
    @Test
    public void testAddPendingTask() {
        Task task = new SimpleTask("Task 1") {
            @Override
            public void performTask() throws TaskException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskManagerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Performing task " + getName() + ", rank: " + getRank());
            }
        };
        task.setRank(0);
        TaskManagerFactory.getDefault().addPendingTask(task);
        task = new SimpleTask("Task 2") {
            @Override
            public void performTask() throws TaskException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskManagerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Performing task " + getName() + ", rank: " + getRank());
            }
        };
        task.setRank(0);
        TaskManagerFactory.getDefault().addPendingTask(task);
        task = new SimpleTask("Task 3") {
            @Override
            public void performTask() throws TaskException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskManagerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Performing task " + getName() + ", rank: " + getRank());
            }
        };
        task.setRank(1);
        TaskManagerFactory.getDefault().addPendingTask(task);
        task = new SimpleTask("Task 4") {
            @Override
            public void performTask() throws TaskException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskManagerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Performing task " + getName() + ", rank: " + getRank());
            }
        };
        task.setRank(1);
        TaskManagerFactory.getDefault().addPendingTask(task);
        task = new SimpleTask("Task 5") {
            @Override
            public void performTask() throws TaskException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TaskManagerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Performing task " + getName() + ", rank: " + getRank());
            }
        };
        task.setRank(0);
        TaskManagerFactory.getDefault().addPendingTask(task);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TaskManagerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
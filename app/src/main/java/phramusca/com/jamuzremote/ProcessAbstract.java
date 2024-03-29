/*
 * Copyright (C) 2012 Raphael Camus
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

package phramusca.com.jamuzremote;

/**
 * A process abstract class.
 *
 * @author phramusca
 */
public abstract class ProcessAbstract extends Thread {

    ProcessAbstract(String name) {
        super(name);
    }

    //Process information (internal)
    private boolean abort = false;

    /**
     * Abort process thread
     */
    public final void abort() {
        this.abort = true;
    }

    /**
     * Clears abort flag
     */
    protected final void resetAbort() {
        this.abort = false;
    }

    /**
     * Checks if user asked abortion
     *
     * @throws InterruptedException to be caught in process main function
     */
    synchronized void checkAbort() throws InterruptedException {
        if (this.abort) {
            throw new InterruptedException();
        }
    }
}

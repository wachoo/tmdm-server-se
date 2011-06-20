/*
 * This code is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any
 * later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package com.amalto.core.jobox.watch;

import java.util.List;

/**
 * Interface for listening to disk file changes.
 * 
 * @see DirMonitor
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public interface DirListener {

    /**
     * Called when one of the monitored files are created, deleted or modified.
     * 
     */
    void fileChanged(List newFiles, List deleteFiles, List modifyFiles);

    /**
     * Called when the job context are changed
     * 
     * @param context
     */
    void contextChanged(String jobfile, String context);
}

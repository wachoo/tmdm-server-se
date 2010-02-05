package com.amalto.core.jobox.watch;


/*
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA  02111-1307, USA.
 */




import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Class for monitoring changes in disk files.
 * Usage:
 *
 *    1. Implement the FileListener interface.
 *    2. Create a FileMonitor instance.
 *    3. Add the file(s)/directory(ies) to listen for.
 *
 * fileChanged() will be called when a monitored file is created,
 * deleted or its modified time changes.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class DirMonitor
{
  private Timer       timer_;
  private HashMap     files_;       // File -> Long
  private Collection  listeners_;   // of (FileListener)
   

  /**
   * Create a file monitor instance with specified polling interval.
   * 
   * @param pollingInterval  Polling interval in milli seconds.
   */
  public DirMonitor (long pollingInterval)
  {
    files_     = new HashMap();
    listeners_ = new ArrayList();

    //timer_ = new Timer (true);
    timer_ = new Timer ();
    timer_.schedule (new FileMonitorNotifier(), 0, pollingInterval);
  }


  
  /**
   * Stop the file monitor polling.
   */
  public void stop()
  {
    timer_.cancel();
  }
  

  /**
   * Add file to listen for. File may be any java.io.File (including a
   * directory) and may well be a non-existing file in the case where the
   * creating of the file is to be trepped.
   * <p>
   * More than one file can be listened for. When the specified file is
   * created, modified or deleted, listeners are notified.
   * 
   * @param file  File to listen for.
   */
  public void addFile (File file)
  {
    if (!files_.containsKey (file)) {
      long modifiedTime = file.exists() ? file.lastModified() : -1;
      files_.put (file, new DirLog(modifiedTime,file));
    }
  }

  

  /**
   * Remove specified file for listening.
   * 
   * @param file  File to remove.
   */
  public void removeFile (File file)
  {
    files_.remove (file);
  }


  
  /**
   * Add listener to this file monitor.
   * 
   * @param fileListener  Listener to add.
   */
  public void addListener (DirListener fileListener)
  {
    listeners_.add (fileListener);
  }


  
  /**
   * This is the timer thread which is executed every n milliseconds
   * according to the setting of the file monitor. It investigates the
   * file in question and notify listeners if changed.
   */
  private class FileMonitorNotifier extends TimerTask
  {
    public void run()
    {
      // Loop over the registered files and see which have changed.
      // Use a copy of the list in case listener wants to alter the
      // list within its fileChanged method.
      Collection files = new ArrayList (files_.keySet());
      
      for (Iterator i = files.iterator(); i.hasNext(); ) {
        File file = (File) i.next();
        DirLog lastDirLog=(DirLog) files_.get (file);
        DirLog newDirLog=new DirLog(new Long(file.exists() ? file.lastModified() : -1),file);

        Map<String, Long> lastFilesMap=lastDirLog.getFilesModifiedTime();
        Map<String, Long> newFilesMap=newDirLog.getFilesModifiedTime();
        
        List newFiles=new ArrayList();
        List deleteFiles=new ArrayList();
        List modifyFiles=new ArrayList();
        
        for (Iterator<String> iterator = newFilesMap.keySet().iterator(); iterator.hasNext();) {
			String fileName = iterator.next();
			if(lastFilesMap.containsKey(fileName)) {
				long lastModifiedTime = ((Long) lastFilesMap.get(fileName)).longValue();
				long newModifiedTime = ((Long) newFilesMap.get(fileName)).longValue();
				if (newModifiedTime != lastModifiedTime) {
					modifyFiles.add(fileName);
				}
			}else {
				newFiles.add(fileName);
			}
		}
        
        for (Iterator<String> iterator = lastFilesMap.keySet().iterator(); iterator.hasNext();) {
        	String fileName = iterator.next();
        	if(!newFilesMap.containsKey(fileName)) {
        		deleteFiles.add(fileName);
        	}
        }
        
        
        if(newFiles.size()>0||deleteFiles.size()>0||modifyFiles.size()>0) {
        	
        	// Register new modified time
            files_.put (file, newDirLog);

            
        	// Notify listeners
            for (Iterator j = listeners_.iterator(); j.hasNext(); ) {
              DirListener listener = (DirListener) j.next();
              listener.fileChanged (newFiles,deleteFiles,modifyFiles);
            }
        }
        
      }
    }
  }


  /**
   * Test this class.
   * 
   * @param args  Not used.
   */
  public static void main (String args[])
  {
    // Create the monitor
    DirMonitor monitor = new DirMonitor (1000);

    // Add some files to listen for
    monitor.addFile (new File ("E:/base/jobox/deploy"));

    // Add a jobox listener
    monitor.addListener (new JoboxListener());

    // Avoid program exit
    while (!false) ;
  }

  
  
}


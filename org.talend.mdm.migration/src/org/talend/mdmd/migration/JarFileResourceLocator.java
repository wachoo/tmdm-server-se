package org.talend.mdmd.migration;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


public class JarFileResourceLocator extends ClassLoader
{
    private JarFile _jarFile;
    private String _dir;
    private List<String> _paths;
    
    private File _file;
    
    private List<Class> _jarList = new ArrayList<Class>();
    
    public JarFileResourceLocator(String dir, List<String> paths)
    {
        _dir = dir;
        _paths = paths;
    }

    public File getFile(String fileName)
    {
        return null;
    }

    public InputStream getInputStream(String fileName) throws IOException
    {
    	if (_jarFile == null)return null;
    	
        ZipEntry entry = _jarFile.getEntry(fileName);

        if (entry == null)
        {
            return null;
        }

        return _jarFile.getInputStream(entry);
    }

    public boolean isDirectory(String fileName)
    {
    	if (_jarFile == null)return false;
    	
        ZipEntry entry = _jarFile.getEntry(fileName + "/");

        if (entry == null)
        {
			org.apache.log4j.Logger.getLogger(this.getClass()).error(
					"jar file : " + _jarFile.getName() +
					 " does not contain entry '" +
					 fileName + "/'");
					
            return false;
        }

        return entry.isDirectory();
    }

    private File[] getAllEntries(String dirName)
    {
    	File zipDir = new File(dirName);

        FileFilter fltr = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                if (pathname.isDirectory()
                    || pathname.getName().endsWith(".jar"))
                {
                    return true;
                }
                return false;
            }
        };
    	
        return zipDir.listFiles(fltr);
    }
    
    public Class[] loadFile()
    {
    	return loadFile(_dir);
    }
    
    private Class[] loadFile(String dirName)
    {
    	File[] entries = getAllEntries(dirName);
    	try
    	{
	    	for (File oneFile : entries)
	    	{
	    		if (oneFile.isDirectory())
	    		{
	    			loadFile(oneFile.getAbsolutePath());
	    		}
	    		else
	    		{
	    			_file = oneFile;
	    			String path = oneFile.getPath();
	    			_jarFile =  new JarFile(path);
	    			findClass();
	    		}
	    	}
    	}
    	catch(IOException e)
    	{
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getMessage());
    	}
    	
    	return _jarList.toArray(new Class[]{});
    }
    
    
    private void findClass()
    {
        try
        {
            ZipEntry entry = null;

            Enumeration enumeration = _jarFile.entries();

            while (enumeration.hasMoreElements())
            {
                entry = (ZipEntry) enumeration.nextElement();
                String clsPath = entry.getName().replaceAll("/", "."); 
                if (clsPath.endsWith(".class"))clsPath = clsPath.substring(0, clsPath.lastIndexOf(".class"));
                if (!entry.isDirectory() &&
                		_paths.indexOf(clsPath) != -1)
                {
                    URL url = _file.toURL();
                    URLClassLoader loader = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
                    Class cd = loader.loadClass(clsPath);
                    if (cd != null) {
                    	_jarList.add(cd);
					}
                }
            }
        }
        catch (Exception e)
        {
        	org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getCause() + "");
        }
    }

    public long getFileSize(String fileName)
    {
    	if (_jarFile == null)return -1;
    	
        ZipEntry entry = _jarFile.getEntry(fileName);

        if (entry == null)
        {
            return -1;
        }

        return entry.getSize();
    }

}

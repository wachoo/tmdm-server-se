package com.amalto.workbench.image;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.amalto.workbench.MDMWorbenchPlugin;

public class ImageCache {
	static org.eclipse.jface.resource.ImageRegistry registry=null;
	static{
		registry=JFaceResources.getImageRegistry();
		Map<String, EImage> map=EImage.getAlllEimages();
		Iterator<EImage> it=map.values().iterator();
		while(it.hasNext()){
			EImage image =it.next();			
			registry.put(image.getPath(), MDMWorbenchPlugin.getImageDescriptor(image.getPath()));
		}		
	}
	
	public static ImageDescriptor getImage(String path){
		if(null!=registry.getDescriptor(path))
			return registry.getDescriptor(path);
		else{
			return registry.getDescriptor("icons/talend-picto-small.gif");
		}
	}
	
	public static Image getCreatedImage(String path){
		if(null!=registry.get(path))
			return registry.get(path);
		else{
			return registry.get("icons/talend-picto-small.gif");
		}
	}
}

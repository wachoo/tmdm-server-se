package com.amalto.workbench.utils;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;

public class FontUtils {
	static FontRegistry fontRegistry= JFaceResources.getFontRegistry();
	
	public static Font getBoldFont(Font font){
		
		return fontRegistry.getBold(font.getFontData()[0].getName());
	}
	public static Font getItalicFont(Font font){
		
		return fontRegistry.getItalic(font.getFontData()[0].getName());
	}
}

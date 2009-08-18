package com.amalto.workbench.utils;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author aiming
 *
 */
public class WidgetUtils {
	
	public static void initRedoUndo(final TextViewer viewer){
        TextViewerUndoManager undoManager=new TextViewerUndoManager(20);
        undoManager.connect(viewer);
        viewer.setUndoManager(undoManager);            
        StyledText styledText = viewer.getTextWidget();
        styledText.addKeyListener(new KeyListener( ) {
                    public void keyPressed( KeyEvent e )
                    {
                    }
                    private boolean isUndoKeyPress( KeyEvent e )
                    {
                        // CTRL + z
                        return ( e.keyCode == 'z' && e.stateMask ==
                			SWT.CTRL);                            
                    }
                    private boolean isRedoKeyPress( KeyEvent e )
                    {
                        // CTRL + y
                        return ( e.keyCode == 'y' && e.stateMask ==
                			SWT.CTRL);
                    }
                    public void keyReleased( KeyEvent e )
                    {
                        if ( isUndoKeyPress( e ) )
                        {
                        	viewer.doOperation( ITextOperationTarget.UNDO );
                        }
                        else if ( isRedoKeyPress( e ) )
                        {
                        	viewer.doOperation( ITextOperationTarget.REDO );
                        }
                    }
                });
        //undoManager.getUndoContext();

	}
	
	public static void enable(Composite parent,boolean enabled){
		parent.setEnabled(enabled);
		for(Control control:parent.getChildren()){
			if(control instanceof Text || control instanceof CCombo || control instanceof Combo|| control instanceof Button || control instanceof StyledText){
				control.setEnabled(enabled);
				if(control instanceof StyledText){
					if(!enabled)
						control.setBackground(control.getParent().getBackground());
					else
						control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				}
			}
			if(control instanceof Composite){
				enable((Composite)control, enabled);
			}
		}
		
	}
}

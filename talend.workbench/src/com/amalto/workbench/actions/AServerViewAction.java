/*
 * Created on 15 nov. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.actions;

import org.eclipse.jface.action.Action;

import com.amalto.workbench.views.ServerView;

public abstract class AServerViewAction extends Action {

    private ServerView serverView;
    
    public AServerViewAction() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ServerView getServerView() {
        return serverView;
    }

    public void setServerView(ServerView serverView) {
        this.serverView = serverView;
    }


}

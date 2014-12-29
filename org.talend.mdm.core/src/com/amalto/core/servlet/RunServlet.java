package com.amalto.core.servlet;

import com.amalto.core.objects.configurationinfo.assemble.AssembleConcreteBuilder;
import com.amalto.core.objects.configurationinfo.assemble.AssembleDirector;
import com.amalto.core.objects.configurationinfo.assemble.AssembleProc;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.util.Util;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RunServlet extends HttpServlet {

    public RunServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        resp.setContentType("text/html; charset=\"UTF-8\"");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("<html><body>");
        try {
            if ("clean".equals(action)) {
                writer.write("<p><b>Cleanup the background jobs stored in MDM</b><br/>"
                        + "Check jboss/server/default/log/server.log or the jboss console output to determine when clean is completed</b></p>");

                final AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
                final AssembleDirector director = new AssembleDirector(concreteBuilder);
                director.constructCleanPart();
                AssembleProc assembleProc = concreteBuilder.getAssembleProc();

                Util.getConfigurationInfoCtrlLocal().autoUpgradeInBackground(assembleProc);

            } else if ("init".equals(action)) {
                writer.write("<p><b>Initialize the clusters and build-in data</b><br/>"
                        + "Check jboss/server/default/log/server.log or the jboss console output to determine when init is completed</b></p>");

                final AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
                final AssembleDirector director = new AssembleDirector(concreteBuilder);
                director.constructInitPart();
                AssembleProc assembleProc = concreteBuilder.getAssembleProc();

                Util.getConfigurationInfoCtrlLocal().autoUpgradeInBackground(assembleProc);

            } else if ("migrate".equals(action)) {
                MDMConfiguration.getConfiguration(true);
                writer.write("<p><b>Migrate data to the new revision</b><br/>"
                        + "Check jboss/server/default/log/server.log or the jboss console output to determine when migrate is completed</b></p>");

                final AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
                final AssembleDirector director = new AssembleDirector(concreteBuilder);
                director.constructMigratePart();
                AssembleProc assembleProc = concreteBuilder.getAssembleProc();

                Util.getConfigurationInfoCtrlLocal().autoUpgradeInBackground(assembleProc);

            } else if ("reindex".equals(action)) {
                String container = req.getParameter("container");
                if (container == null) {
                    writer.write("<p><b>Container parameter is mandatory for action " + action + "</b><br/>");
                } else {
                    StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
                    Storage storage = storageAdmin.get(container, storageAdmin.getType(container));
                    storage.reindex();
                }
            } else {
                writer.write("<p><b>Unknown action: </b>" + action + "<br/>");
            }

        } catch (Exception e) {
            writer.write("<h1>An error occurred: " + e.getLocalizedMessage() + "</h1>");
        }
        writer.write("</body></html>");

    }

}
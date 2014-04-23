// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class ImagePreviewWindow extends Window {

    private Image image;

    LayoutContainer imageContainer;

    private final int MAX_WINDOW_WIDTH = 850;

    private final int MAX_WINDOW_HEIGHT = 550;

    private final int WIDTH_OFFSET = 16;

    private final int HEIGHT_OFFSET = 22;

    public ImagePreviewWindow(String imagePath) {
        initWindow(imagePath);
    }

    private void initWindow(String imagePath) {

        setLayout(new AbsoluteLayout());
        setSize(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT);
        setModal(true);
        setBlinkModal(true);
        setHeading(getImageName(imagePath));
        setScrollMode(Scroll.AUTO);
        setResizable(false);
        setBorders(false);
        setDraggable(false);

        buildWindow(imagePath);

        show();
    }

    private void buildWindow(String imagePath) {
        image = new Image(imagePath);
        image.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                resetImageContainer();
                image.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            }

        });
        image.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);

        imageContainer = new LayoutContainer();
        imageContainer.add(image);
        imageContainer.setLayout(new AbsoluteLayout());

        add(imageContainer);
    }

    private void resetImageContainer() {
        if (image != null) {
            if (image.getWidth() > 0 && image.getHeight() > 0) {
                int posTempLeft = 0;
                int posTempTop = 0;

                imageContainer.setWidth(image.getWidth());
                imageContainer.setHeight(image.getHeight());

                if (image.getWidth() < MAX_WINDOW_WIDTH) {
                    posTempLeft = (MAX_WINDOW_WIDTH - image.getWidth() - WIDTH_OFFSET) / 2;
                }
                if (image.getHeight() < MAX_WINDOW_HEIGHT) {
                    posTempTop = (MAX_WINDOW_HEIGHT - image.getHeight() - HEIGHT_OFFSET) / 2;
                }

                imageContainer.setPosition(posTempLeft, posTempTop);
            } else {
                setLayout(new CenterLayout());
            }
        }
    }

    private String getImageName(String imagePath) {
        String defaultName = ""; //$NON-NLS-1$
        if (imagePath != null && imagePath.contains("/")) { //$NON-NLS-1$
            defaultName = imagePath.substring(imagePath.lastIndexOf("/") + 1); //$NON-NLS-1$
        }
        return defaultName;
    }
}

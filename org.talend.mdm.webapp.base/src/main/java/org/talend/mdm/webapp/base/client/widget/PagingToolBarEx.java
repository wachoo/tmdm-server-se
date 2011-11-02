// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.client.widget;

import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.ToolBarLayout;
import com.extjs.gxt.ui.client.widget.menu.HeaderMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;

public class PagingToolBarEx extends PagingToolBar {

    private El inputEl;

    public PagingToolBarEx(int pageSize) {
        super(pageSize);
        setLayout(new PagingToolBarExLayout());
        LabelToolItem sizeLabel = new LabelToolItem(BaseMessagesFactory.getMessages().page_size_label());

        final NumberField sizeField = new NumberField() {

            @Override
            protected void onRender(Element target, int index) {
                super.onRender(target, index);
                inputEl = this.input;
            }
        };
        sizeField.setWidth(30);
        sizeField.setValue(pageSize);
        sizeField.setValidator(validator);
        sizeField.addListener(Events.Change, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                if (sizeField.isValid() && sizeField.getValue() != null) {
                    setPageSize((int) Double.parseDouble(sizeField.getValue() + "")); //$NON-NLS-1$
                    first();
                }
            }
        });
        sizeField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent fe) {
                if (fe.getKeyCode() == KeyCodes.KEY_ENTER) {
                    blur(inputEl.dom);
                }
            }
        });
        sizeField.setValue(pageSize);
        this.insert(new SeparatorToolItem(), this.getItemCount() - 2);
        this.insert(sizeLabel, this.getItemCount() - 2);
        this.insert(sizeField, this.getItemCount() - 2);
    }

    protected void onResize(int width, int height) {
        super.onResize(width, height);
        this.layout(true);
    }

    private native void blur(Element el)/*-{
                                        el.blur();
                                        }-*/;

    private Validator validator = new Validator() {

        public String validate(Field<?> field, String value) {
            String valueStr = value == null ? "" : value.toString(); //$NON-NLS-1$
            boolean success = true;
            try {
                int num = Integer.parseInt(valueStr);
                if (num <= 0) {
                    success = false;
                }
            } catch (NumberFormatException e) {
                success = false;
            }
            if (!success) {
                return BaseMessagesFactory.getMessages().page_size_notice();
            }
            return null;
        }
    };

    class PagingToolBarExLayout extends ToolBarLayout {

        protected void initMore() {
            super.initMore();
            moreMenu.setWidth(250);
        }

        protected void addComponentToMenu(Menu menu, Component c) {
            if (c instanceof SeparatorToolItem) {
                menu.add(new SeparatorMenuItem());
            } else if (c instanceof SplitButton) {
                final SplitButton sb = (SplitButton) c;
                MenuItem item = new MenuItem(sb.getText(), sb.getIcon());
                item.setEnabled(c.isEnabled());
                item.setItemId(c.getItemId());
                if (sb.getMenu() != null) {
                    item.setSubMenu(sb.getMenu());
                }
                item.addSelectionListener(new SelectionListener<MenuEvent>() {

                    @Override
                    public void componentSelected(MenuEvent ce) {
                        ButtonEvent e = new ButtonEvent(sb);
                        e.setEvent(ce.getEvent());
                        sb.fireEvent(Events.Select, e);
                    }

                });
                menu.add(item);
            } else if (c instanceof LabelToolItem) {
                LabelToolItem l = (LabelToolItem) c;
                MenuItem item = new MenuItem(l.getLabel());
                menu.add(item);
            } else if (c instanceof Button) {
                final Button b = (Button) c;
                String menuText = b.getText();
                if (menuText == null || menuText.trim().length() == 0) {
                    menuText = b.getToolTip().getToolTipConfig() == null ? "" : b.getToolTip().getToolTipConfig().getText(); //$NON-NLS-1$
                }
                MenuItem item = new MenuItem(menuText, b.getIcon());
                item.setToolTip(b.getToolTip().getToolTipConfig());
                item.setItemId(c.getItemId());
                if (b.getMenu() != null) {
                    item.setHideOnClick(false);
                    item.setSubMenu(b.getMenu());
                }
                item.setEnabled(c.isEnabled());
                item.addSelectionListener(new SelectionListener<MenuEvent>() {

                    @Override
                    public void componentSelected(MenuEvent ce) {
                        ButtonEvent e = new ButtonEvent(b);
                        e.setEvent(ce.getEvent());
                        b.fireEvent(Events.Select, e);
                    }

                });
                menu.add(item);
            } else if (c instanceof ButtonGroup) {
                ButtonGroup g = (ButtonGroup) c;
                g.setItemId(c.getItemId());
                menu.add(new SeparatorMenuItem());
                String heading = g.getHeading();
                if (heading != null && heading.length() > 0 && !heading.equals("&#160;")) {
                    menu.add(new HeaderMenuItem(g.getHeading()));
                }
                for (Component c2 : g.getItems()) {
                    addComponentToMenu(menu, c2);
                }
                menu.add(new SeparatorMenuItem());
            }

              if (menu.getItemCount() > 0) {
                if (menu.getItem(0) instanceof SeparatorMenuItem) {
                    menu.remove(menu.getItem(0));
                }
                if (menu.getItemCount() > 0) {
                    if (menu.getItem(menu.getItemCount() - 1) instanceof SeparatorMenuItem) {
                        menu.remove(menu.getItem(menu.getItemCount() - 1));
                    }
                }
            }
        }
    }
}

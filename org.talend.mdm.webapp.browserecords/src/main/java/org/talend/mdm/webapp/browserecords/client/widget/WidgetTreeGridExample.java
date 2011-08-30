package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.Arrays;

import org.talend.mdm.webapp.browserecords.client.model.Folder;
import org.talend.mdm.webapp.browserecords.client.model.Music;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class WidgetTreeGridExample extends LayoutContainer {

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FlowLayout(10));

        Folder model = getTreeModel();

        TreeStore<ModelData> store = new TreeStore<ModelData>();
        store.add(model.getChildren(), true);

        ColumnConfig name = new ColumnConfig("name", "Name", 100);
        name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>() {

            @Override
            public Widget getWidget(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid) {
                Button b = new Button((String) model.get(property));
                b.setToolTip("Click for more information");
                return b;
            }
        });

        ColumnConfig date = new ColumnConfig("author", "Author", 100);
        ColumnConfig size = new ColumnConfig("genre", "Genre", 100);
        ColumnModel cm = new ColumnModel(Arrays.asList(name, date, size));

        ContentPanel cp = new ContentPanel();
        cp.setBodyBorder(false);
        cp.setHeading("Widget Renderer TreeGrid");
        cp.setButtonAlign(HorizontalAlignment.CENTER);
        cp.setLayout(new FitLayout());
        cp.setFrame(true);
        cp.setSize(600, 300);

        TreeGrid<ModelData> tree = new TreeGrid<ModelData>(store, cm);
        tree.setBorders(true);
        tree.setSize(400, 400);
        tree.setAutoExpandColumn("name");
        tree.getTreeView().setRowHeight(26);
        tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-music"));

        cp.add(tree);

        add(cp);
    }

    public static Folder getTreeModel() {
        Folder[] folders = new Folder[] {
                new Folder("Beethoven", new Folder[] {

                        new Folder("Quartets", new Music[] { new Music("Six String Quartets", "Beethoven", "Quartets"),
                                new Music("Three String Quartets", "Beethoven", "Quartets"),
                                new Music("Grosse Fugue for String Quartets", "Beethoven", "Quartets"), }),
                        new Folder("Sonatas", new Music[] { new Music("Sonata in A Minor", "Beethoven", "Sonatas"),
                                new Music("Sonata in F Major", "Beethoven", "Sonatas"), }),

                        new Folder("Concertos", new Music[] { new Music("No. 1 - C", "Beethoven", "Concertos"),
                                new Music("No. 2 - B-Flat Major", "Beethoven", "Concertos"),
                                new Music("No. 3 - C Minor", "Beethoven", "Concertos"),
                                new Music("No. 4 - G Major", "Beethoven", "Concertos"),
                                new Music("No. 5 - E-Flat Major", "Beethoven", "Concertos"), }),

                        new Folder("Symphonies", new Music[] { new Music("No. 1 - C Major", "Beethoven", "Symphonies"),
                                new Music("No. 2 - D Major", "Beethoven", "Symphonies"),
                                new Music("No. 3 - E-Flat Major", "Beethoven", "Symphonies"),
                                new Music("No. 4 - B-Flat Major", "Beethoven", "Symphonies"),
                                new Music("No. 5 - C Minor", "Beethoven", "Symphonies"),
                                new Music("No. 6 - F Major", "Beethoven", "Symphonies"),
                                new Music("No. 7 - A Major", "Beethoven", "Symphonies"),
                                new Music("No. 8 - F Major", "Beethoven", "Symphonies"),
                                new Music("No. 9 - D Minor", "Beethoven", "Symphonies"), }), }),
                new Folder("Brahms", new Folder[] {
                        new Folder("Concertos", new Music[] { new Music("Violin Concerto", "Brahms", "Concertos"),
                                new Music("Double Concerto - A Minor", "Brahms", "Concertos"),
                                new Music("Piano Concerto No. 1 - D Minor", "Brahms", "Concertos"),
                                new Music("Piano Concerto No. 2 - B-Flat Major", "Brahms", "Concertos"), }),
                        new Folder("Quartets", new Music[] { new Music("Piano Quartet No. 1 - G Minor", "Brahms", "Quartets"),
                                new Music("Piano Quartet No. 2 - A Major", "Brahms", "Quartets"),
                                new Music("Piano Quartet No. 3 - C Minor", "Brahms", "Quartets"),
                                new Music("String Quartet No. 3 - B-Flat Minor", "Brahms", "Quartets"), }),
                        new Folder("Sonatas", new Music[] { new Music("Two Sonatas for Clarinet - F Minor", "Brahms", "Sonatas"),
                                new Music("Two Sonatas for Clarinet - E-Flat Major", "Brahms", "Sonatas"), }),
                        new Folder("Symphonies", new Music[] { new Music("No. 1 - C Minor", "Brahms", "Symphonies"),
                                new Music("No. 2 - D Minor", "Brahms", "Symphonies"),
                                new Music("No. 3 - F Major", "Brahms", "Symphonies"),
                                new Music("No. 4 - E Minor", "Brahms", "Symphonies"), }), }),
                new Folder("Mozart", new Folder[] { new Folder("Concertos", new Music[] {
                        new Music("Piano Concerto No. 12", "Mozart", "Concertos"),
                        new Music("Piano Concerto No. 17", "Mozart", "Concertos"),
                        new Music("Clarinet Concerto", "Mozart", "Concertos"),
                        new Music("Violin Concerto No. 5", "Mozart", "Concertos"),
                        new Music("Violin Concerto No. 4", "Mozart", "Concertos"), }), }), };

        Folder root = new Folder("root");
        for (int i = 0; i < folders.length; i++) {
            root.add((Folder) folders[i]);
        }

        return root;
    }

}





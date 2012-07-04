package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;

import com.google.gwt.junit.client.GWTTestCase;


public class SortSubTypesTest extends GWTTestCase {

    public void testSortSubTypes() {
        List<ComplexTypeModel> reusableTypes = new ArrayList<ComplexTypeModel>();

        ComplexTypeModel typeEDA = new ComplexTypeModel("typeEDA", null); //$NON-NLS-1$
        typeEDA.setOrderValue(0);
        reusableTypes.add(typeEDA);

        ComplexTypeModel balit = new ComplexTypeModel("Balit", null); //$NON-NLS-1$
        balit.setOrderValue(1);
        reusableTypes.add(balit);

        ComplexTypeModel pointEchange = new ComplexTypeModel("PointEchange", null); //$NON-NLS-1$
        pointEchange.setOrderValue(3);
        reusableTypes.add(pointEchange);

        ComplexTypeModel secoursMutuelGrt = new ComplexTypeModel("SecoursMutuelGrt", null); //$NON-NLS-1$
        secoursMutuelGrt.setOrderValue(2);
        reusableTypes.add(secoursMutuelGrt);

        ComplexTypeModel pointSoutirageJumeleRpd = new ComplexTypeModel("PointSoutirageJumeleRpd", null); //$NON-NLS-1$
        pointSoutirageJumeleRpd.setOrderValue(9);
        reusableTypes.add(pointSoutirageJumeleRpd);

        ComplexTypeModel pointSoutirageRpt = new ComplexTypeModel("PointSoutirageRpt", null); //$NON-NLS-1$
        pointSoutirageRpt.setOrderValue(4);
        reusableTypes.add(pointSoutirageRpt);

        ComplexTypeModel pointInjectionRptRpd = new ComplexTypeModel("PointInjectionRptRpd", null); //$NON-NLS-1$
        pointInjectionRptRpd.setOrderValue(13);
        reusableTypes.add(pointInjectionRptRpd);

        ComplexTypeModel pointSoutirageRpd = new ComplexTypeModel("PointSoutirageRpd", null); //$NON-NLS-1$
        pointSoutirageRpd.setOrderValue(5);
        reusableTypes.add(pointSoutirageRpd);

        ComplexTypeModel pointSoutirageProfile = new ComplexTypeModel("PointSoutirageProfile", null); //$NON-NLS-1$
        pointSoutirageProfile.setOrderValue(7);
        reusableTypes.add(pointSoutirageProfile);

        ComplexTypeModel pointInjectionRpt = new ComplexTypeModel("PointInjectionRpt", null); //$NON-NLS-1$
        pointInjectionRpt.setOrderValue(11);
        reusableTypes.add(pointInjectionRpt);

        ComplexTypeModel pointInjectionRpd = new ComplexTypeModel("PointInjectionRpd", null); //$NON-NLS-1$
        pointInjectionRpd.setOrderValue(12);
        reusableTypes.add(pointInjectionRpd);

        ComplexTypeModel pointSoutirageJumeleRpt = new ComplexTypeModel("PointSoutirageJumeleRpt", null); //$NON-NLS-1$
        pointSoutirageJumeleRpt.setOrderValue(8);
        reusableTypes.add(pointSoutirageJumeleRpt);

        ComplexTypeModel pointSoutirageRptRpd = new ComplexTypeModel("PointSoutirageRptRpd", null); //$NON-NLS-1$
        pointSoutirageRptRpd.setOrderValue(6);
        reusableTypes.add(pointSoutirageRptRpd);

        ComplexTypeModel pointSoutirageJumeleRptRpd = new ComplexTypeModel("PointSoutirageJumeleRptRpd", null); //$NON-NLS-1$
        pointSoutirageJumeleRptRpd.setOrderValue(10);
        reusableTypes.add(pointSoutirageJumeleRptRpd);
        
        int originalSize = reusableTypes.size();

        Collections.sort(reusableTypes, new Comparator<ComplexTypeModel>() {
            public int compare(ComplexTypeModel o1, ComplexTypeModel o2) {
                return o1.getOrderValue() - o2.getOrderValue();
            }
        });

        assertNotNull(reusableTypes);
        assertEquals(originalSize, reusableTypes.size());
        assertEquals("typeEDA", reusableTypes.get(0).getName()); //$NON-NLS-1$
        assertEquals("Balit", reusableTypes.get(1).getName()); //$NON-NLS-1$
        assertEquals("SecoursMutuelGrt", reusableTypes.get(2).getName()); //$NON-NLS-1$
        assertEquals("PointEchange", reusableTypes.get(3).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageRpt", reusableTypes.get(4).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageRpd", reusableTypes.get(5).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageRptRpd", reusableTypes.get(6).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageProfile", reusableTypes.get(7).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageJumeleRpt", reusableTypes.get(8).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageJumeleRpd", reusableTypes.get(9).getName()); //$NON-NLS-1$
        assertEquals("PointSoutirageJumeleRptRpd", reusableTypes.get(10).getName()); //$NON-NLS-1$
        assertEquals("PointInjectionRpt", reusableTypes.get(11).getName()); //$NON-NLS-1$
        assertEquals("PointInjectionRpd", reusableTypes.get(12).getName()); //$NON-NLS-1$
        assertEquals("PointInjectionRptRpd", reusableTypes.get(13).getName()); //$NON-NLS-1$
    }

    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}

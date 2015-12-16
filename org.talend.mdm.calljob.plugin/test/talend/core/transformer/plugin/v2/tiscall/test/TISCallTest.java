package talend.core.transformer.plugin.v2.tiscall.test;


import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import talend.core.transformer.plugin.v2.tiscall.ConceptMappingParam;
import talend.core.transformer.plugin.v2.tiscall.ejb.TISCallTransformerPluginBean;
import talend.core.transformer.plugin.v2.tiscall.webservices.ArrayOfXsdString;

public class TISCallTest {

	@Test
    public void testBuildParameters() throws Exception {
        String expetedResult = "<results>\n<Agency>\n<Region>Lausanne</Region>\n<Etablissement><Adresse>Paris</Adresse></Etablissement>\n</Agency>\n</results>\n";
        List<ArrayOfXsdString> list = new ArrayList<ArrayOfXsdString>();

        ArrayOfXsdString arrayOfXsdString = new ArrayOfXsdString();
        arrayOfXsdString.getItem().add("Lausanne");
        arrayOfXsdString.getItem().add("Paris");

        list.add(arrayOfXsdString);
        ConceptMappingParam conceptMappingParam = new ConceptMappingParam("Agency", "{p0:Region,p1:\"Etablissement/Adresse\"}");
        TISCallTransformerPluginBean bean = new TISCallTransformerPluginBean();
        String parameter = bean.buildParameters(list, conceptMappingParam);
        assertEquals(expetedResult, parameter);
    }
	
}

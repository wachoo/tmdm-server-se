import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.talend.mdm.webapp.general.model.ItemBean;
import org.talend.mdm.webapp.general.server.util.Utils;



public class TestService extends TestCase {

    @Test
    public void testGetLanguage(){
        List<ItemBean> langs = Utils.getLanguages();
        for (ItemBean lang : langs){
            System.out.println(lang.getText() + ',' + lang.getValue());
        }
    }
}

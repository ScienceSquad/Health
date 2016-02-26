

import com.sciencesquad.health.nutrition.NutritionModule;
import org.junit.Before;
import org.junit.Test;
import android.content.Context;
import android.test.AndroidTestCase;
import junit.framework.Assert;

/**
 * Units tests for Health365
 */
public class TestHealth365Realm extends AndroidTestCase{

    Context context;

    @Test
    public void testRealm() throws Exception{
        try {
            NutritionModule nutritionModuleTest = new NutritionModule(getContext(), "test.realm");
            Assert.assertTrue(nutritionModuleTest.testNutritionModule());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

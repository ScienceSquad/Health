import com.sciencesquad.health.nutrition.NutritionModule;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Units tests for Health365
 */
public class TestHealth365Realm {
    @Test
    public void validdateRealm(){
        try {
            NutritionModule nutritionModule = new NutritionModule();
            assertTrue(nutritionModule.testNutritionModule());
            //assertThat(nutritionModule.testNutritionModule(), is(true));
        }
        catch (Exception e){

        }

    }
}


import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.nutrition.NutritionModule;

import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.app.Instrumentation;
import android.content.Context;

import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;



import junit.framework.Assert;

/**
 * Units tests for Health365
 */
public class TestHealth365Realm extends ApplicationTestCase<BaseApplication>{

    Context context;
    BaseApplication testBaseApplication;
    public TestHealth365Realm(){
        super(BaseApplication.class);
    }

    public TestHealth365Realm(Class<BaseApplication> applicationClass) {
        super(applicationClass);
    }

    @Test
    public void testRealm() throws Exception{
        createApplication();
        try {
            NutritionModule nutritionModuleTest = new NutritionModule(getSystemContext(), "test.realm");
            Assert.assertTrue(nutritionModuleTest.testNutritionModule());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

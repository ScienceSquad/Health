import android.test.ApplicationTestCase;

import org.junit.Test;
import junit.framework.Assert;

import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.nutrition.NutritionModule;

/**
 * This is a JUnit test for android.
 * This provides an example of how to run a test
 * which uses an application and allow it to use context.
 */

public class TestHealth365Realm extends ApplicationTestCase<BaseApplication>{

    /**
     * Constructor sets up the test case an Application class
     * and will allow to set up the context for the test case.
     */

    public TestHealth365Realm(){
        super(BaseApplication.class);
    }

    /**
     * Uses the testNutrition Module function to test the code.
     * To save time writing code, I've just called the unit test
     * within the Nutrition Module itself.
     */
    @Test
    public void testRealm(){
        createApplication();
        try {
            NutritionModule testNutrition = new NutritionModule();
            testNutrition.testNutritionModule();
        } catch (Exception e){
            Assert.fail();
        }

    }
}

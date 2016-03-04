import android.app.AlarmManager;
import android.test.ApplicationTestCase;

import org.junit.Test;
import junit.framework.Assert;

import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.nutrition.NutritionModel;
import com.sciencesquad.health.prescriptions.PrescriptionAlarm;
import com.sciencesquad.health.prescriptions.PrescriptionModel;

import io.realm.RealmQuery;
import java8.util.function.Consumer;
import java.util.Calendar;


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
     * Based off the old testNutrition Module function from before.
     * To save time writing code, I've just copied pasted the code
     * from the old unit test and modified it slightly to fit a testcase.
     */
    @Test
    public void testRealm(){
        createApplication();
        try {
            RealmContext testRealm = new RealmContext<>();
            testRealm.init(BaseApplication.application(), NutritionModel.class, "test.realm");
            testRealm.clear();
            NutritionModel testModel = new NutritionModel();
            testModel.setCalorieIntake(50);
            testModel.setHadCaffeine(false);
            Calendar rightNow = Calendar.getInstance();
            testModel.setDate(rightNow.getTime());
            testRealm.add(testModel);
            RealmQuery<NutritionModel> testQuery = testRealm.query();

            Assert.assertEquals(testQuery.findAll().size(), 1);
            Assert.assertEquals(testQuery.findAll().first().getCalorieIntake(), 50);

            boolean testCoffee = false;
            for (int i = 1 ; i < 12; i++){
                NutritionModel testModelI = new NutritionModel();
                testModelI.setCalorieIntake(i);
                testModelI.setHadCaffeine(testCoffee);
                testModelI.setDate(rightNow.getTime());
                testRealm.add(testModelI);
                testCoffee = !testCoffee;
            }

            Assert.assertEquals(testQuery.findAll().size(), 12);
            Assert.assertEquals(testQuery.findAll().get(4).getCalorieIntake(), 4);
            testRealm.updateRealmModel(4, new Consumer<NutritionModel>() {
                @Override
                public void accept(NutritionModel model) {
                    model.setCalorieIntake(500);
                }
            });

            Assert.assertEquals(testQuery.equalTo("calorieIntake", 500).findAll().size(), 1);
            Assert.assertEquals(testQuery.equalTo("calorieIntake", 500).findAll().get(0).getCalorieIntake(), 500);

            testRealm.clear();
            Assert.assertEquals(testQuery.findAll().size(), 0);

            try {
                testRealm.close();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Realm Failed to Close.");
            }

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail("An Exception Occurred.");
        }

    }

    @Test
    public void testPrescriptionRealm() {
        createApplication();
        try {
            RealmContext testRealm = new RealmContext<>();
            testRealm.init(BaseApplication.application(), PrescriptionModel.class, "test.realm");
            testRealm.clear();
            PrescriptionModel testModel = new PrescriptionModel();
            testModel.setName("Tylenol");
            Calendar rightNow = Calendar.getInstance();
            testModel.setStartDate(rightNow.getTimeInMillis());
            testModel.setDosage(10);
            testModel.setRepeatDuration(AlarmManager.INTERVAL_DAY);
            testRealm.add(testModel);
            RealmQuery<PrescriptionModel> testQuery = testRealm.query();

            PrescriptionAlarm.setAlarm(testModel, BaseApplication.application());

            Assert.assertEquals(testQuery.findAll().size(), 1);
            Assert.assertEquals(testQuery.findAll().first().getName(), "Tylenol");

            for (int i = 1 ; i < 12; i++){
                PrescriptionModel testModelI = new PrescriptionModel();
                testModelI.setDosage(i);
                testModelI.setStartDate(rightNow.getTimeInMillis());
                testRealm.add(testModelI);
            }

            Assert.assertEquals(testQuery.findAll().size(), 12);
            Assert.assertEquals(testQuery.findAll().get(4).getDosage(), 4);
            testRealm.updateRealmModel(4, new Consumer<PrescriptionModel>() {
                @Override
                public void accept(PrescriptionModel model) {
                    model.setDosage(20);
                }
            });

            Assert.assertEquals(testQuery.equalTo("dosage", 20).findAll().size(), 1);
            Assert.assertEquals(testQuery.equalTo("dosage", 20).findAll().get(0).getDosage(), 20);

            testRealm.clear();
            Assert.assertEquals(testQuery.findAll().size(), 0);

            try {
                testRealm.close();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Realm Failed to Close.");
            }

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail("An Exception Occurred.");
        }
    }
}

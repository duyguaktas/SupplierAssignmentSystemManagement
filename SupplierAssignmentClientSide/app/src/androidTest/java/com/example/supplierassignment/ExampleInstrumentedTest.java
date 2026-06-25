package com.example.supplierassignment;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule; import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private SupplierRepository repository;
    @Before
    public void setup() { // Initialize the repository using the test context
        repository = new SupplierRepository(androidx.test.core.app.ApplicationProvider.getApplicationContext()); }

    @After
    public void resetData(){
        repository.resetDatabase().blockingAwait();
    }

    @Test
    public void testAddAndVerifySupplier() {
        onView(withId(R.id.addSupplier)).perform(click()); //add screen
        // fill the form
        onView(withId(R.id.etSupplierName)).perform(typeText("Test Supplier Inc"), closeSoftKeyboard());
        // pick type
        onView(withId(R.id.rbType1)).perform(click());
        // save
        onView(withId(R.id.btnSaveSupplier)).perform(click());
        // go search
        onView(withId(R.id.btnSearch)).perform(click());
        // search name
        onView(withId(R.id.etSearchBar)).perform(typeText("Test Supplier Inc"), closeSoftKeyboard());
        // check if result came up
        onView(allOf(withId(R.id.textViewSupplierName), (withText("Test Supplier Inc")))).check(matches(isDisplayed()));
    }
}
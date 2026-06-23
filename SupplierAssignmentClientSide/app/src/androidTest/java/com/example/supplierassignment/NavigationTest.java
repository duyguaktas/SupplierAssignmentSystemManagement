package com.example.supplierassignment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigateToAddSupplier() {
        onView(withId(R.id.addSupplier)).perform(click());

        onView(withId(R.id.etSupplierName)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToSearchSuppliers() {
        onView(withId(R.id.btnSearch)).perform(click());

        onView(withId(R.id.etSearchBar)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToSendToServer() {
        onView(withId(R.id.btnSendToServer)).perform(click());

        onView(withId(R.id.cardInstruction)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackNavigationFromAddSupplier() {
        onView(withId(R.id.addSupplier)).perform(click());

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(click());

        onView(withId(R.id.addSupplier)).check(matches(isDisplayed()));
    }

}
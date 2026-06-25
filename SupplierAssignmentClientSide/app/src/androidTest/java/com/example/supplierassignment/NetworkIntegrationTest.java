package com.example.supplierassignment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class NetworkIntegrationTest {

    private ServerSocket mockServer;
    private int port;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private SupplierRepository repository;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        repository = new SupplierRepository(context);

        mockServer = new ServerSocket(0);
        port = mockServer.getLocalPort();

        // Start a simple TCP server thread
        new Thread(() -> {
            try {
                while (isRunning.get()) {
                    try (Socket clientSocket = mockServer.accept();
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                        in.readLine();

                        String jsonResponse = "{ \"assignments\": [ { \"dayOfTheMonth\": 1, \"contractSupplier\": \"Supplier A\", \"stockSupplier\": \"Supplier B\" } ] }";
                        out.println(jsonResponse);
                    }
                }
            } catch (IOException e) {

            }
        }).start();
    }

    @After
    public void teardown() throws IOException {
        isRunning.set(false);
        if (mockServer != null) {
            mockServer.close();
        }
    }

    @Test
    public void testSyncWithServerAndDatabaseWrite() throws InterruptedException {
        repository.resetDatabase().blockingAwait();
        Thread.sleep(200);

        // add the supplier we expect the server to update
        repository.addSupplier("Supplier A", 1).blockingAwait();
        Thread.sleep(500);

        // navigate to send screen
        onView(withId(R.id.btnSendToServer)).perform(click());

        // open connection dialog
        onView(withId(R.id.btnSendToServer)).perform(click());

        // Enter Server IP and Port
        onView(withHint("Server IP Address")).perform(ViewActions.replaceText("127.0.0.1"), closeSoftKeyboard());
        onView(withHint("Port Number")).perform(ViewActions.replaceText(String.valueOf(port)), closeSoftKeyboard());

        // connect
        onView(withText("Connect")).perform(click());

        // wait for network and database operations
        Thread.sleep(3000);

        // verify database write
        List<Assignment> assignments = repository.getAllAssignments();
        assertFalse("Database should contain the new assignment from server", assignments.isEmpty());
        assertEquals("Assignment day should match server JSON", 1, assignments.get(0).getDayOfTheMonth());
        assertEquals("Assignment supplier should match server JSON", "Supplier A", assignments.get(0).getContractSupplier());

        // verify supplier update
        List<Supplier> suppliers = repository.getAllSuppliers("Supplier A");
        assertFalse("Supplier A should exist", suppliers.isEmpty());
        // Verify the formatted string matches what we expect from the sync logic
        assertEquals("Supplier reserved days should be updated via sync", "1", suppliers.get(0).getReservedDays());
    }
}
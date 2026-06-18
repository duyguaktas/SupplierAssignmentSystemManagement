package com.example.supplierassignment;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.supplierassignment.databinding.ActivitySendToServerBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendToServerActivity extends AppCompatActivity {

    private ActivitySendToServerBinding binding;
    private SupplierRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySendToServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new SupplierRepository(this);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loadSuppliers();

        binding.btnSendToServer.setOnClickListener(v -> ShowConnectionDialog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_to_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_reset_db) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Reset Database")
                    .setMessage("This will delete all suppliers and assignments, and reset IDs to 1. Are you sure?")
                    .setPositiveButton("Reset", (dialog, which) -> {
                        new Thread(() -> {
                            repository.resetDatabase();
                            runOnUiThread(() -> {
                                loadSuppliers();
                                Toast.makeText(this, "Database reset successfully", Toast.LENGTH_SHORT).show();
                            });
                        }).start();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = repository.getAllSuppliers("");
        ArrayAdapter<Supplier> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_list_item_1, suppliers);
        binding.lvSuppliers.setAdapter(adapter);

        binding.lvSuppliers.setOnItemClickListener((parent, view, position, id) -> {
            Supplier selected = suppliers.get(position);
            Intent intent = new Intent(this, EditSupplierDetail.class);
            intent.putExtra(EditSupplierDetail.EXTRA_SUPPLIER_ID, selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuppliers();
    }

    protected void ShowConnectionDialog(){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, 0);

        final TextInputLayout tilIP = new TextInputLayout(this);
        tilIP.setHint("Server IP Address");
        final TextInputEditText etIP = new TextInputEditText(this);
        etIP.setText("10.0.2.2"); // Default for emulator
        tilIP.addView(etIP);
        layout.addView(tilIP);

        final TextInputLayout tilPort = new TextInputLayout(this);
        tilPort.setHint("Port Number");
        tilPort.setPadding(0, padding, 0, 0);
        final TextInputEditText etPort = new TextInputEditText(this);
        etPort.setHint("e.g. 8080");
        etPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        tilPort.addView(etPort);
        layout.addView(tilPort);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Connect to Server")
                .setView(layout)
                .setPositiveButton("Connect", (dialog, which) -> {
                    String ip = etIP.getText().toString();
                    if (ip.isEmpty()) {
                        Toast.makeText(this, "IP address cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String portStr = etPort.getText().toString();
                    if(portStr.isEmpty()){
                        Toast.makeText(this, "Port cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int port = Integer.parseInt(portStr);
                        sendDataToServer(ip, port);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void handleServerResponse(String json){
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, List<Assignment>>>() {}.getType();
        Map<String, List<Assignment>> data = gson.fromJson(json, mapType);
        List<Assignment> assignments = data.get("assignments");

        if (assignments != null && !assignments.isEmpty()) {
            Toast.makeText(this, "Received " + assignments.size() + " assignments from server", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No assignments received", Toast.LENGTH_SHORT).show();
        }
        
        new Thread (
                () -> {
                    repository.clearAssignments();
                    if (assignments != null) {
                        for (Assignment a : assignments) {
                            repository.addAssignment(a);
                        }
                    }
                    runOnUiThread(
                            () -> {
                                Toast.makeText(this, "All assignments saved!", Toast.LENGTH_LONG).show();
                            }
                    );
                }
        ).start();
    }
    
    private void sendDataToServer(String ip, int port) {
        new Thread(
                () -> { try { List<Supplier> suppliers = repository.getAllSuppliers("");
                    Gson gson = new Gson();
                    Map<String, List<Supplier>> wrapper = new HashMap<>();
                    wrapper.put("suppliers", suppliers);
                    String jsonRequest = gson.toJson(wrapper);
                    
                    Socket socket = new Socket(ip, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(jsonRequest);

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String jsonResponse = in.readLine();
                    Log.d("Network", "Response: " + jsonResponse);

                    runOnUiThread(
                            () -> { 
                                handleServerResponse(jsonResponse);
                            }
                    );
                socket.close();
                } catch (Exception e) {
                    runOnUiThread(
                            () -> {
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    );
                }
            }).start();
    }
    
}
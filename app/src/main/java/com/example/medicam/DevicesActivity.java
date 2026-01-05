package com.example.medicam;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DevicesActivity extends AppCompatActivity {

    private static final String TAG = "DevicesActivity";
    private static final int REQUEST_ENABLE_BT = 1001;
    private static final String PREF_NAME = "medicam_pref";
    private static final String KEY_CONNECTED_DEVICES = "connected_devices";

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDeviceInfo> discoveredDevices;
    private List<BluetoothDeviceInfo> connectedDevices;
    private Set<String> discoveredAddresses;
    private BluetoothDeviceAdapter deviceAdapter;
    private BluetoothDeviceAdapter connectedDeviceAdapter;

    private RecyclerView rvDiscoveredDevices;
    private RecyclerView rvConnectedDevices;
    private LinearLayout emptyStateLayout;
    private LinearLayout scanningLayout;
    private LinearLayout connectedSection;
    private MaterialButton btnScan;
    private ProgressBar progressScanning;
    private TextView tvScanStatus;

    private boolean isScanning = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        // Handle Window Insets for EdgeToEdge
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        initBluetooth();
        setupActivityLaunchers();
        loadConnectedDevices();
        setupBottomNavigation();
    }

    private void initViews() {
        rvDiscoveredDevices = findViewById(R.id.rvDiscoveredDevices);
        rvConnectedDevices = findViewById(R.id.rvConnectedDevices);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        scanningLayout = findViewById(R.id.scanningLayout);
        connectedSection = findViewById(R.id.connectedSection);
        btnScan = findViewById(R.id.btnScan);
        progressScanning = findViewById(R.id.progressScanning);
        tvScanStatus = findViewById(R.id.tvScanStatus);

        discoveredDevices = new ArrayList<>();
        connectedDevices = new ArrayList<>();
        discoveredAddresses = new HashSet<>();

        // Setup discovered devices adapter
        deviceAdapter = new BluetoothDeviceAdapter(this, discoveredDevices, device -> {
            connectToDevice(device);
        });
        if (rvDiscoveredDevices != null) {
            rvDiscoveredDevices.setLayoutManager(new LinearLayoutManager(this));
            rvDiscoveredDevices.setAdapter(deviceAdapter);
        }

        // Setup connected devices adapter
        connectedDeviceAdapter = new BluetoothDeviceAdapter(this, connectedDevices, device -> {
            showDisconnectDialog(device);
        });
        if (rvConnectedDevices != null) {
            rvConnectedDevices.setLayoutManager(new LinearLayoutManager(this));
            rvConnectedDevices.setAdapter(connectedDeviceAdapter);
        }

        if (btnScan != null) {
            btnScan.setOnClickListener(v -> {
                if (isScanning) {
                    stopScanning();
                } else {
                    startBluetoothScan();
                }
            });
        }
    }

    private void initBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
            if (btnScan != null) btnScan.setEnabled(false);
        }
    }

    private void setupActivityLaunchers() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        checkBluetoothEnabled();
                    } else {
                        Toast.makeText(this, "Bluetooth permissions required to scan devices", Toast.LENGTH_LONG).show();
                    }
                }
        );

        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        startDiscovery();
                    } else {
                        Toast.makeText(this, "Bluetooth must be enabled to scan", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startBluetoothScan() {
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }
        checkBluetoothEnabled();
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
    }

    private void checkBluetoothEnabled() {
        if (bluetoothAdapter == null) return;

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                enableBluetoothLauncher.launch(enableBtIntent);
            }
        } else {
            startDiscovery();
        }
    }

    private void startDiscovery() {
        if (bluetoothAdapter == null) return;

        // Clear previous results
        discoveredDevices.clear();
        discoveredAddresses.clear();
        deviceAdapter.notifyDataSetChanged();

        // Register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);

        // Start discovery
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            
            boolean started = bluetoothAdapter.startDiscovery();
            if (started) {
                isScanning = true;
                updateScanUI();
                
                // Auto-stop after 30 seconds
                handler.postDelayed(this::stopScanning, 30000);
            } else {
                Toast.makeText(this, "Failed to start scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopScanning() {
        if (bluetoothAdapter != null) {
            // Check permission before calling isDiscovering()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                try {
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException checking discovery state: " + e.getMessage());
                }
            }
        }
        isScanning = false;
        updateScanUI();
        handler.removeCallbacksAndMessages(null);
        
        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered
        }
    }

    private void updateScanUI() {
        if (btnScan != null) {
            btnScan.setText(isScanning ? "Stop Scanning" : "Scan for Devices");
        }
        if (scanningLayout != null) {
            scanningLayout.setVisibility(isScanning ? View.VISIBLE : View.GONE);
        }
        if (progressScanning != null) {
            progressScanning.setVisibility(isScanning ? View.VISIBLE : View.GONE);
        }
        if (tvScanStatus != null) {
            tvScanStatus.setText(isScanning ? "Scanning for nearby devices..." : "");
        }
        
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean hasDevices = !discoveredDevices.isEmpty() || !connectedDevices.isEmpty();
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(hasDevices || isScanning ? View.GONE : View.VISIBLE);
        }
        if (connectedSection != null) {
            connectedSection.setVisibility(connectedDevices.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String deviceName = null;
                    String deviceAddress = device.getAddress();
                    
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ||
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        deviceName = device.getName();
                    }
                    
                    // Filter for watch-like devices or show all if no name filter
                    if (deviceAddress != null && !discoveredAddresses.contains(deviceAddress)) {
                        discoveredAddresses.add(deviceAddress);
                        
                        String displayName = deviceName != null ? deviceName : "Unknown Device";
                        String deviceType = getDeviceType(deviceName);
                        
                        BluetoothDeviceInfo deviceInfo = new BluetoothDeviceInfo(
                                displayName,
                                deviceAddress,
                                deviceType,
                                false
                        );
                        
                        discoveredDevices.add(deviceInfo);
                        deviceAdapter.notifyItemInserted(discoveredDevices.size() - 1);
                        updateEmptyState();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                isScanning = false;
                updateScanUI();
                
                if (discoveredDevices.isEmpty()) {
                    Toast.makeText(context, "No devices found nearby", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Found " + discoveredDevices.size() + " devices", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private String getDeviceType(String deviceName) {
        if (deviceName == null) return "Unknown";
        
        String nameLower = deviceName.toLowerCase();
        if (nameLower.contains("watch") || nameLower.contains("band") || nameLower.contains("fit")) {
            return "Smart Watch";
        } else if (nameLower.contains("heart") || nameLower.contains("pulse") || nameLower.contains("bp")) {
            return "Health Monitor";
        } else if (nameLower.contains("scale") || nameLower.contains("weight")) {
            return "Smart Scale";
        } else if (nameLower.contains("thermo") || nameLower.contains("temp")) {
            return "Thermometer";
        } else if (nameLower.contains("oximeter") || nameLower.contains("spo2")) {
            return "Pulse Oximeter";
        } else if (nameLower.contains("glucose") || nameLower.contains("sugar")) {
            return "Glucose Monitor";
        }
        return "Bluetooth Device";
    }

    private void connectToDevice(BluetoothDeviceInfo device) {
        // Check if already connected
        for (BluetoothDeviceInfo connected : connectedDevices) {
            if (connected.getAddress().equals(device.getAddress())) {
                Toast.makeText(this, "Device already connected", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        device.setConnected(true);
        connectedDevices.add(device);
        connectedDeviceAdapter.notifyDataSetChanged();
        
        // Remove from discovered list
        for (int i = 0; i < discoveredDevices.size(); i++) {
            if (discoveredDevices.get(i).getAddress().equals(device.getAddress())) {
                discoveredDevices.remove(i);
                deviceAdapter.notifyItemRemoved(i);
                break;
            }
        }
        
        saveConnectedDevices();
        updateEmptyState();
        
        Toast.makeText(this, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
    }

    private void showDisconnectDialog(BluetoothDeviceInfo device) {
        new AlertDialog.Builder(this)
                .setTitle("Disconnect Device")
                .setMessage("Do you want to disconnect " + device.getName() + "?")
                .setPositiveButton("Disconnect", (dialog, which) -> {
                    disconnectDevice(device);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void disconnectDevice(BluetoothDeviceInfo device) {
        for (int i = 0; i < connectedDevices.size(); i++) {
            if (connectedDevices.get(i).getAddress().equals(device.getAddress())) {
                connectedDevices.remove(i);
                connectedDeviceAdapter.notifyItemRemoved(i);
                break;
            }
        }
        
        saveConnectedDevices();
        updateEmptyState();
        
        Toast.makeText(this, "Disconnected from " + device.getName(), Toast.LENGTH_SHORT).show();
    }

    private void saveConnectedDevices() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(connectedDevices);
        prefs.edit().putString(KEY_CONNECTED_DEVICES, json).apply();
    }

    private void loadConnectedDevices() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_CONNECTED_DEVICES, null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BluetoothDeviceInfo>>(){}.getType();
            List<BluetoothDeviceInfo> saved = gson.fromJson(json, type);
            if (saved != null) {
                connectedDevices.clear();
                connectedDevices.addAll(saved);
                connectedDeviceAdapter.notifyDataSetChanged();
            }
        }
        
        updateEmptyState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(DevicesActivity.this, DashboardActivity.class));
                finish();
            });
        }

        if (navPathology != null) {
            navPathology.setOnClickListener(v -> {
                startActivity(new Intent(DevicesActivity.this, PathologyActivity.class));
                finish();
            });
        }

        if (navABHA != null) {
            navABHA.setOnClickListener(v -> {
                startActivity(new Intent(DevicesActivity.this, ABHAActivity.class));
                finish();
            });
        }

        if (navBMI != null) {
            navBMI.setOnClickListener(v -> {
                startActivity(new Intent(DevicesActivity.this, BMIGenderActivity.class));
                finish();
            });
        }

        if (navDevices != null) {
            navDevices.setOnClickListener(v -> {
                Toast.makeText(this, "Connected Devices", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

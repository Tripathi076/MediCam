package com.example.medicam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.DeviceViewHolder> {

    private Context context;
    private List<BluetoothDeviceInfo> devices;
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(BluetoothDeviceInfo device);
    }

    public BluetoothDeviceAdapter(Context context, List<BluetoothDeviceInfo> devices, OnDeviceClickListener listener) {
        this.context = context;
        this.devices = devices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bluetooth_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDeviceInfo device = devices.get(position);

        holder.tvDeviceName.setText(device.getName());
        holder.tvDeviceType.setText(device.getDeviceType());
        holder.tvDeviceAddress.setText(device.getAddress());

        // Set icon based on device type
        int iconRes = getIconForDeviceType(device.getDeviceType());
        holder.ivDeviceIcon.setImageResource(iconRes);

        // Set button text based on connection status
        if (device.isConnected()) {
            holder.btnAction.setText("Connected");
            holder.btnAction.setBackgroundTintList(context.getColorStateList(R.color.medicam_success));
        } else {
            holder.btnAction.setText("Connect");
            holder.btnAction.setBackgroundTintList(context.getColorStateList(R.color.medicam_primary));
        }

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeviceClick(device);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeviceClick(device);
            }
        });
    }

    private int getIconForDeviceType(String deviceType) {
        if (deviceType == null) return R.drawable.ic_smart_watch;
        
        switch (deviceType) {
            case "Smart Watch":
                return R.drawable.ic_smart_watch;
            case "Health Monitor":
                return R.drawable.ic_heart_monitor;
            case "Smart Scale":
                return R.drawable.ic_scale;
            case "Thermometer":
                return R.drawable.ic_thermometer;
            case "Pulse Oximeter":
                return R.drawable.ic_oximeter;
            case "Glucose Monitor":
                return R.drawable.ic_glucose;
            default:
                return R.drawable.ic_smart_watch;
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDeviceIcon;
        TextView tvDeviceName;
        TextView tvDeviceType;
        TextView tvDeviceAddress;
        MaterialButton btnAction;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDeviceIcon = itemView.findViewById(R.id.ivDeviceIcon);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceType = itemView.findViewById(R.id.tvDeviceType);
            tvDeviceAddress = itemView.findViewById(R.id.tvDeviceAddress);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}

package com.example.sensorapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var scanner: BluetoothLeScanner? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if ((device.name ?: "").contains("M5600", ignoreCase = true)) {
                runOnUiThread { statusText.text = "Encontrado: ${device.name}
Conectando..." }
                scanner?.stopScan(this)
                connectToDevice(device)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusText = findViewById(R.id.statusText)

        val btManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = btManager.adapter
        scanner = bluetoothAdapter?.bluetoothLeScanner

        findViewById<Button>(R.id.scanButton).setOnClickListener {
            startScan()
        }
    }

    private fun startScan() {
        val scanPermission = checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
        if (scanPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), 1)
            return
        }
        statusText.text = "Scanner ligado..."
        scanner?.startScan(scanCallback)
    }

    private fun connectToDevice(device: BluetoothDevice) {
        device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runOnUiThread { statusText.text = "Conectado! Descobrindo serviços..." }
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    runOnUiThread { statusText.text = "Desconectado" }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.services.firstOrNull()
                val ch = service?.characteristics?.firstOrNull()
                if (ch != null) {
                    gatt.readCharacteristic(ch)
                } else {
                    runOnUiThread { statusText.text = "Serviços encontrados, mas nenhuma característica legível" }
                }
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                val raw = characteristic.value
                runOnUiThread { statusText.text = "Bytes: ${raw.joinToString()}" }
            }
        })
    }
}

package com.example.fcmmultipleaccounts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fcmmultipleaccounts.utils.FIREBASE_SECOND_INSTANCE
import com.example.fcmmultipleaccounts.utils.FIREBASE_THIRD_INSTANCE
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.getString(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }

        findViewById<Button>(R.id.logTokenButton).setOnClickListener {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                printToken(it)
            }
        }

        findViewById<Button>(R.id.logTokenButton2).setOnClickListener {
            val app = FirebaseApp.getInstance(FIREBASE_SECOND_INSTANCE)
            val firebaseMessaging = app.get(FirebaseMessaging::class.java) as FirebaseMessaging?
            firebaseMessaging?.token?.addOnCompleteListener {
                printToken(it)
            }
        }

        findViewById<Button>(R.id.logTokenButton3).setOnClickListener {
            val app = FirebaseApp.getInstance(FIREBASE_THIRD_INSTANCE)
            val firebaseMessaging = app.get(FirebaseMessaging::class.java) as FirebaseMessaging?
            firebaseMessaging?.token?.addOnCompleteListener {
                printToken(it)
            }
        }


        Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()
        askNotificationPermission()
    }

    private fun printToken(task: Task<String>) {
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return
        }
        val token = task.result
        val msg = "FCM registration Token: $token"
        Log.d(TAG, msg)
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}
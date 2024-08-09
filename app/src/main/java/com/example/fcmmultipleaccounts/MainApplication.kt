package com.example.fcmmultipleaccounts

import android.app.Application
import com.example.fcmmultipleaccounts.utils.FIREBASE_SECOND_INSTANCE
import com.example.fcmmultipleaccounts.utils.FIREBASE_THIRD_INSTANCE
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the default Firebase object
        // (this one gets the options automatically from google-services.json using google services plugin)
        FirebaseApp.initializeApp(this)

        // Initialize the second Firebase object
        FirebaseApp.initializeApp(this, getSecondInstanceOptions(), FIREBASE_SECOND_INSTANCE)

        // Initialize the third Firebase object
        FirebaseApp.initializeApp(this, getThirdInstanceOptions(), FIREBASE_THIRD_INSTANCE)
    }

    private fun getSecondInstanceOptions() = FirebaseOptions.Builder()
            .setProjectId("melitest-75428")
            .setApplicationId("1:432321637641:android:021962e6dbea8f21f277d8")
            .setApiKey("AIzaSyBAyLp8-EF-B94s9exG2aDZijiZeLgOXjI")
            .setStorageBucket("melitest-75428.appspot.com")
            .build()

    private fun getThirdInstanceOptions() = FirebaseOptions.Builder()
            .setProjectId("melitest3-ca331")
            .setApplicationId("1:996139010595:android:fb0207372c023d1d263f96")
            .setApiKey("AIzaSyDaLLmX13MSntjfO2LmTsRGajUa2_WGmdg")
            .setStorageBucket("melitest3-ca331.appspot.com")
            .build()

}
package com.example.quizapp

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

private const val USER_PREFERENCES_NAME = "user_preferences"
val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        // Since we're migrating from SharedPreferences, add a migration based on the
        // SharedPreferences name
        listOf(SharedPreferencesMigration(context, USER_PREFERENCES_NAME))
    }
)

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }
            if (destination.id == R.id.seriesFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }
            if (destination.id == R.id.informationFragment) {
                bottomNavigationView.visibility = View.GONE
            }
            if (destination.id == R.id.quizzesFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }
            if (destination.id == R.id.levelFragment) {
                bottomNavigationView.visibility = View.GONE
            }
            if (destination.id == R.id.profileFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
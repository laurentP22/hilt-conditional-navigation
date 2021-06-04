package com.example.hiltconditionalnavigation.ui.profile

import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hiltconditionalnavigation.R
import com.example.hiltconditionalnavigation.data.FakeUserRepository
import com.example.hiltconditionalnavigation.data.repository.UserRepository
import com.example.hiltconditionalnavigation.di.HiltConditionalNavigationModule
import com.example.hiltconditionalnavigation.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(HiltConditionalNavigationModule::class)
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userRepository: UserRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    private fun setAuthentication(auth: Boolean) {
        (userRepository as FakeUserRepository).auth = auth
    }

    @Test
    fun profileFragment_unauthenticated() {
        setAuthentication(false)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ProfileFragment> {
            navController.setViewModelStore(ViewModelStore())
            navController.setGraph(R.navigation.nav_main)
            navController.setCurrentDestination(R.id.profileFragment)
            Navigation.setViewNavController(findViewById(R.id.main_nav_host), navController)
        }

        assertEquals(navController.currentDestination?.id, R.id.loginFragment)
    }

    @Test
    fun profileFragment_authenticated() {
        setAuthentication(true)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ProfileFragment> {
            navController.setViewModelStore(ViewModelStore())
            navController.setGraph(R.navigation.nav_main)
            navController.setCurrentDestination(R.id.profileFragment)
            Navigation.setViewNavController(findViewById(R.id.main_nav_host), navController)
        }
        assertEquals(navController.currentDestination?.id, R.id.profileFragment)
    }
}
package com.daya.githubuser.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.daya.githubuser.R
import com.daya.githubuser.databinding.ActivityMainBinding
import com.daya.githubuser.ui.settings.SettingsActivity
import com.daya.githubuser.ui.search.SearchActivity
import com.daya.githubuser.xtension.capitalized
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager2.adapter = MainStateAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.for_you).capitalized()
                else -> getString(R.string.favorite).capitalized()
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_search -> {
                val intentToSearch = Intent(this, SearchActivity::class.java)
                startActivity(intentToSearch)

                true
            }
            R.id.main_menu_settings -> {
                val intentToSearch = Intent(this, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                }
                startActivity(intentToSearch)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
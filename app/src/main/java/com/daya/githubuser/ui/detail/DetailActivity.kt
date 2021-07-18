package com.daya.githubuser.ui.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ca.allanwang.kau.utils.toast
import com.bumptech.glide.Glide
import com.daya.githubuser.R
import com.daya.githubuser.data.Resource
import com.daya.githubuser.data.profile.general.GeneralBio
import com.daya.githubuser.databinding.ActivityDetailBinding
import com.daya.githubuser.ui.detail.follow.FollowFragmentStateAdapter
import com.daya.githubuser.xtension.avatarForMainApp
import com.daya.githubuser.xtension.isValidUrl
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel : DetailViewModel by viewModels ()

    private lateinit var skeleton : Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bio = intent.getParcelableExtra<GeneralBio>(KEY_USER_EXTRA)
        detailViewModel.sculptingBio(bio)

        supportActionBar?.title = bio?.username ?: getString(R.string.detail_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailViewModel.getDetailBioLiveData.observe(this, { resBio ->
            when (resBio) {
                is Resource.Loading -> {
                    if (::skeleton.isInitialized) {
                        skeleton.showSkeleton()
                    } else {
                        skeleton = binding.root.createSkeleton().apply {
                            showSkeleton()
                        }
                    }
                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        delay(500)
                        //skeleton.hide()
                        val nonNullBio = resBio.data
                        detailViewModel.sculptingBio(nonNullBio)// re save it, nonNullBio for addToFavorite
                        bindLayout(nonNullBio)
                        invalidateOptionsMenu()
                    }
                }
                is Resource.Error -> {
                    //skeleton.hide()
                    toast(resBio.exceptionMessage.toString(), Toast.LENGTH_SHORT)
                }
            }
        })

        detailViewModel.isUserFavLiveData().observe(this, {
                invalidateOptionsMenu()
        })
    }

    private fun bindLayout(generalBio: GeneralBio) {
        val avatar = if (generalBio.avatar.isValidUrl()) generalBio.avatar else generalBio.avatar.avatarForMainApp()
        Glide.with(this@DetailActivity)
            .load(avatar)
            .dontAnimate()
            .into(binding.profileImage)

        binding.tvName.text = generalBio.name
        binding.tvUserName.text = generalBio.username
        binding.tvCompany.text = generalBio.company
        binding.tvLocation.text = generalBio.location
        binding.tvFollowersCount.text = generalBio.followerCount.toString()
        binding.tvFollowingCount.text = generalBio.followingCount.toString()
        binding.tvRepoCount.text = generalBio.repoCount.toString()

        binding.pager.adapter = FollowFragmentStateAdapter(
            this@DetailActivity,
            followersUrl = generalBio.followersUrl,
            followingUrl = generalBio.followingUrl,
            listFollowers = generalBio.followers,
            listFollowings = generalBio.followings
        )

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.followers).capitalize(Locale.getDefault())
                else -> getString(R.string.followings).capitalize(Locale.getDefault())
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val itemFavorite = it.findItem(R.id.detail_add_favorite)
            val itemUnFavorite = it.findItem(R.id.detail_un_favorite)
            val isFavorite = detailViewModel.isUserFavLiveData().value ?: false
            itemFavorite.isVisible = isFavorite
            itemUnFavorite.isVisible = !isFavorite
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.detail_add_favorite -> {
                detailViewModel.removeUserFromFavorite()
                true
            }
            R.id.detail_un_favorite -> {
                detailViewModel.addUserToFavorite()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    companion object{
        const val KEY_USER_EXTRA = "key_user_extra"
    }

}
package com.dmbaryshev.bd.view

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.BDPresenter
import com.dmbaryshev.bd.utils.find
import com.dmbaryshev.bd.utils.image.loadRoundedImage
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.utils.withArguments
import com.dmbaryshev.bd.view.calendar.CalendarFragment
import com.dmbaryshev.bd.view.common.HeaderView
import com.dmbaryshev.bd.view.friends.fragment.FriendsFragment
import com.dmbaryshev.bd.view.gift.fragment.FavoriteGiftsFragment
import com.dmbaryshev.bd.view.gift.fragment.GiftsFragment
import com.dmbaryshev.bd.view.gift_details.GiftDetailsFragment
import com.dmbaryshev.bd.view.groups.fragment.GroupFragment
import com.dmbaryshev.bd.view.messages.fragment.MessagesFragment
import kotlinx.android.synthetic.main.activity_bd.*
import kotlinx.android.synthetic.main.content_bd.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*

class BDActivity : AppCompatActivity(),
        AppBarLayout.OnOffsetChangedListener,
        IMainView,
        FriendsFragment.IFriendsFragmentListener,
        NavigationView.OnNavigationItemSelectedListener,
        GroupFragment.IGroupsFragmentListener,
        GiftsFragment.IGiftsFragmentListener,
        GiftDetailsFragment.IGiftDetailsFragmentListener,
        CalendarFragment.ICalendarFragmentListener {

    private val TAG = makeLogTag(MainActivity::class.java)
    private val KEY_ME = "com.dmbaryshev.vkschool.view.ME"
    private val mPresenter by lazy { BDPresenter() }
    private var mManager: FragmentManager? = null
    private var mCurrentFragmentTag = ""
    private var mMyProfileVM: UserVM? = null
    private var isHideToolbarView = false
    private val mHeaderToolbar: HeaderView by lazy { find<HeaderView>(R.id.toolbar_header_view) }
    private val mFloatHeaderToolbar: HeaderView by lazy { find<HeaderView>(R.id.float_header_view) }
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bd)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavDrawer()
        mManager = fragmentManager
        mManager?.addOnBackStackChangedListener { setNavIcon() }
        mPresenter.bindView(this)
        if (savedInstanceState == null) {
            mPresenter.load()
            openFriendsFragment()
        }
    }

    protected fun setNavIcon() {
        val backStackEntryCount = fragmentManager.backStackEntryCount
        mDrawerToggle?.isDrawerIndicatorEnabled = backStackEntryCount == 0
    }

    private fun setupNavDrawer() {
        mDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                                              R.string.navigation_drawer_open,
                                              R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle?.syncState()
        supportActionBar?.title = ""
        ctl_main_collapsing.title = ""
        nav_view.setNavigationItemSelectedListener(this)
        appbar.addOnOffsetChangedListener(this)
        mDrawerToggle?.setToolbarNavigationClickListener { onBackPressed() }
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.unbindView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(KEY_ME, mMyProfileVM)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        mMyProfileVM = savedInstanceState?.getParcelable(KEY_ME)
        initNavigationHeaderView(mMyProfileVM)
    }

    override fun openFriendsFragment() {
        replaceFragment(FriendsFragment.newInstance(), FriendsFragment.TAG, false)
    }

    private fun replaceFragment(fragment: Fragment, tag: String, backstack: Boolean) {
        val newFragment = mManager?.findFragmentByTag(tag)
        if (newFragment == null || !mCurrentFragmentTag.equals(tag)) {
            mCurrentFragmentTag = tag
            val fragmentTransaction = mManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.container, fragment, tag)
            if (backstack) {
                fragmentTransaction?.addToBackStack(null)
            }
            fragmentTransaction?.commit()
        }
        mCurrentFragmentTag = tag
    }

    override fun onOffsetChanged(AppBarLayout: AppBarLayout?, offset: Int) {
        val maxScroll = appbar.totalScrollRange
        val percentage: Float = Math.abs(offset).toFloat() / maxScroll

        if (percentage == 1f && isHideToolbarView) {
            mHeaderToolbar.visibility = View.VISIBLE
            isHideToolbarView = !isHideToolbarView

        } else if (percentage < 1f && !isHideToolbarView) {
            mHeaderToolbar.visibility = View.GONE
            isHideToolbarView = !isHideToolbarView
        }

        mFloatHeaderToolbar.visibility = if (isHideToolbarView) View.VISIBLE else View.GONE
    }

    override fun setCollapsingToolbarImage(imageUrl: String, expandAppBar: Boolean) {
        setNavIcon()
        if (!expandAppBar) mFloatHeaderToolbar.visibility = View.GONE
        appbar.setExpanded(expandAppBar, expandAppBar)
        Glide.with(this).load(imageUrl).placeholder(R.drawable.img_placeholder).into(iv_main_image)
    }

    override fun onBackPressed() {
        if (drawer_layout?.isDrawerOpen(GravityCompat.START) ?: false) {
            drawer_layout?.closeDrawer(GravityCompat.START)
        } else if (fragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_friends -> openFriendsFragment(false)
            R.id.nav_calendar -> openCalendarFragment(false)
            R.id.nav_my_groups -> if (mMyProfileVM != null) openGroupsFragment(mMyProfileVM!!,
                                                                               false)
            R.id.nav_fav_gifts -> openFavoriteGiftsFragment(false)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openCalendarFragment(backstack: Boolean) {
        mManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragment(CalendarFragment.newInstance(mPresenter.getFriends()),
                        CalendarFragment.TAG,
                        backstack)
    }

    private fun openFriendsFragment(backstack: Boolean) {
        mManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragment(FriendsFragment.newInstance(), FriendsFragment.TAG, backstack)
    }

    override fun openMessageFragment(userVM: UserVM) {
        replaceFragment(MessagesFragment().withArguments(MessagesFragment.KEY_USER to userVM),
                        MessagesFragment.TAG,
                        true)
    }

    override fun openGroupsFragment(user: UserVM, backstack: Boolean) {
        if (!backstack) mManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragment(GroupFragment().withArguments(GroupFragment.KEY_USER to user),
                        GroupFragment.TAG,
                        backstack)
    }

    fun openFavoriteGiftsFragment(backstack: Boolean) {
        if (!backstack) mManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragment(FavoriteGiftsFragment().withArguments(FavoriteGiftsFragment.KEY_ME to mMyProfileVM!!),
                        FavoriteGiftsFragment.TAG,
                        backstack)
    }

    override fun openGoodsFragment(group: GroupVM) {
        replaceFragment(GiftsFragment().withArguments(GiftsFragment.KEY_GROUP to group),
                        GiftsFragment.TAG,
                        true)
    }

    override fun openGoodsFragment(filteredGifts: List<GiftVM>, userVM: UserVM) {
        replaceFragment(GiftsFragment.newInstance(ArrayList(filteredGifts), userVM),
                        GiftsFragment.TAG,
                        true)
    }

    override fun openGiftDetailsFragment(gift: GiftVM) {
        replaceFragment(GiftDetailsFragment().withArguments(GiftDetailsFragment.KEY_GIFT to gift),
                        GiftDetailsFragment.TAG,
                        true)
    }

    override fun showTitle(title: String) {
        mHeaderToolbar.setTitle(title)
        mFloatHeaderToolbar.setTitle(title)
    }

    override fun showSubtitle(subtitle: String) {
        mHeaderToolbar.setSubTitle(subtitle)
        mFloatHeaderToolbar.setSubTitle(subtitle)
    }

    override fun showError(errorTextRes: Int) {
    }

    override fun showError(errorText: String) {
    }

    override fun stopLoad() {
    }

    override fun startLoad() {
    }

    override fun showData(data: Collection<UserVM>) {
        mMyProfileVM = if (!data.isEmpty()) data.first() else null
        initNavigationHeaderView(mMyProfileVM)
    }

    private fun initNavigationHeaderView(userVM: UserVM?) {
        val headerView = nav_view.getHeaderView(0)
        headerView.iv_my_avatar.loadRoundedImage(this, userVM?.photo100 ?: return)
        headerView.tv_my_name.text = userVM?.name
    }

    override fun showCount(count: Int) {
    }

    override fun sendMessage(group: GroupVM, giftTitle: String) {
        replaceFragment(MessagesFragment().withArguments(MessagesFragment.KEY_GROUP to group,
                                                         MessagesFragment.KEY_GIFT_TITLE to giftTitle),
                        MessagesFragment.TAG,
                        true)
    }
}
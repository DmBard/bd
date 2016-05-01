package com.dmbaryshev.bd.view.gift_details

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.presenter.GiftDetailsPresenter
import com.dmbaryshev.bd.utils.act
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.inflate
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.ICommonFragmentCallback
import kotlinx.android.synthetic.main.fragment_gift_details.*

class GiftDetailsFragment : BaseFragment<GiftDetailsPresenter>(), IGiftDetailsView, ViewPager.OnPageChangeListener {
    val INDICATOR_MARGIN = 4
    private var mListener: IGiftDetailsFragmentListener? = null
    private var mMenu: Menu? = null
    private var mGift: GiftVM? = null
    private val mPagerAdapter: GiftImagesPageAdapter by lazy {
        GiftImagesPageAdapter(act, arguments.getParcelable<GiftVM>(KEY_GIFT).photos)
    }
    private var indicators: Array<ImageView>? = null
    override val presenter = GiftDetailsPresenter()

    override fun getGift(): GiftVM? {
        return mGift ?: arguments.getParcelable<GiftVM>(KEY_GIFT)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as IGiftDetailsFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement FragmentButtonListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_gift_details)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setNestedScrollingEnabled(nsl_fragment_gift_details, false)
        val giftVM = getGift()
        mListener?.showTitle(giftVM?.title ?: "")
        mListener?.showSubtitle(giftVM?.price ?: "")
        mListener?.setCollapsingToolbarImage(giftVM?.thumbPhoto ?: "", false)
        tv_gift_details_description.text = giftVM?.description
        fab_gift_details_buy.setOnClickListener { mPresenter.buy() }
        vp_gift_details.adapter = mPagerAdapter
        vp_gift_details.addOnPageChangeListener(this)
        setPagerIndicator()

        mPresenter.bindView(this)
    }

    private fun setPagerIndicator() {
        indicators = Array(mPagerAdapter.count,
                           {
                               ImageView(act).apply {
                                   this.setImageDrawable(resources.getDrawable(R.drawable.indicator_pager_not_selected))
                               }
                           })
        indicators?.forEach {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                   LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(INDICATOR_MARGIN, 0, INDICATOR_MARGIN, 0)
            ll_pager_counter.addView(it, params)
        }
        if (indicators != null && indicators!!.size > 0) {
            (indicators as Array<ImageView>)[0].setImageDrawable(resources.getDrawable(R.drawable.indicator_pager_selected))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        mMenu = menu
        inflater?.inflate(R.menu.menu_gift_details, menu)
        var item = mMenu?.findItem(R.id.action_like)
        val liked = getGift()?.liked
        item?.setIcon(if (liked ?: false) R.drawable.ic_like_yes else R.drawable.ic_like_no)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_like -> mPresenter.like(getGift()?.liked ?: false)
        }
        return true
    }

    override fun showLike(isLiked: Boolean) {
        mGift = getGift()
        mGift?.liked = isLiked
        dogi(TAG, "showLike liked" + isLiked)
        var item = mMenu?.findItem(R.id.action_like)
        item?.setIcon(if (isLiked) R.drawable.ic_like_yes else R.drawable.ic_like_no)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        indicators?.forEach { it.setImageDrawable(resources.getDrawable(R.drawable.indicator_pager_not_selected)) }
        if (indicators != null) (indicators as Array<ImageView>)[position].setImageDrawable(
                resources.getDrawable(
                        R.drawable.indicator_pager_selected))
    }

    override fun showError(errorTextRes: Int) {
        showErrorSnackbar(view, errorTextRes)
    }

    override fun showError(errorText: String) {
        showErrorSnackbar(view, errorText)
    }

    override fun stopLoad() {
    }

    override fun startLoad() {
    }

    override fun showData(data: Collection<GiftVM>) {
    }

    override fun showCount(count: Int) {
    }

    override fun openMessagesFragment(group: GroupVM) {
        mListener?.sendMessage(group, getGift()?.title ?: "")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_KEY_GIFT, getGift())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mGift = savedInstanceState?.getParcelable(BUNDLE_KEY_GIFT)
    }

    interface IGiftDetailsFragmentListener : ICommonFragmentCallback {
        fun sendMessage(group: GroupVM, giftTitle: String)
    }

    companion object {
        val TAG = makeLogTag(GiftDetailsFragment::class.java)
        val KEY_GIFT = "com.dmbaryshev.vkschool.view.KEY_GIFT"
        private val BUNDLE_KEY_GIFT = "com.dmbaryshev.vkschool.view.BUNDLE_KEY_GIFT"
    }
}
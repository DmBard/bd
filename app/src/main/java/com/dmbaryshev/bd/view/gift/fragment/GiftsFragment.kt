package com.dmbaryshev.bd.view.gift.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.GiftsPresenter
import com.dmbaryshev.bd.utils.ctx
import com.dmbaryshev.bd.utils.inflate
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.utils.setCountingString
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.FilterDialog
import com.dmbaryshev.bd.view.common.ICommonFragmentCallback
import com.dmbaryshev.bd.view.common.adapter.IHolderClick
import com.dmbaryshev.bd.view.common.adapter.IViewType
import com.dmbaryshev.bd.view.gift.adapter.GiftsAdapter
import kotlinx.android.synthetic.main.fragment_gifts.*
import java.util.*

class GiftsFragment : BaseFragment<GiftsPresenter>(), IGiftView, IHolderClick {
    private val mGiftsAdapter by lazy { GiftsAdapter(mGifts as MutableList<IViewType>, this) }
    private var mListener: IGiftsFragmentListener? = null
    private var mGifts: MutableList<GiftVM> = ArrayList()
    private var mGroup: GroupVM? = null
    private var mMenu: Menu? = null
    private var mLoading = false
    private val mFilterDialog by lazy {
        val onOkButtonListener: (String, Float, Float) -> Unit = { name, minPrice, maxPrice ->
            mPresenter.getFilteredGifts(name, minPrice, maxPrice)
        }
        FilterDialog(ctx, R.style.AppBottomSheetDialog, onOkButtonListener)
    }

    override val presenter: GiftsPresenter = GiftsPresenter()

    override fun getGroup(): GroupVM? {
        return mGroup ?: arguments.getParcelable<GroupVM>(KEY_GROUP)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_KEY_GROUP, mGroup)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mGroup = savedInstanceState?.getParcelable(BUNDLE_KEY_GROUP)
    }

    override fun onItemClick(adapterPosition: Int) {
        mListener?.openGiftDetailsFragment(mGiftsAdapter.getGift(adapterPosition))
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as IGiftsFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement FragmentButtonListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_gifts)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupVM: GroupVM? = arguments.getParcelable<GroupVM>(KEY_GROUP)
        val giftsByUser = arguments.getParcelableArrayList<GiftVM>(KEY_GIFTS)
        mPresenter.bindView(this)

        if (groupVM != null) {
            srl_refresh_gifts.setOnRefreshListener({ mPresenter.forceLoad() })
            mListener?.showTitle(groupVM.name)
            mListener?.showSubtitle(setCountingString(R.plurals.goods_count, mPresenter.count))
            mListener?.setCollapsingToolbarImage(groupVM.photo200)
            mPresenter.load()
        } else if (giftsByUser != null) {
            srl_refresh_gifts.isEnabled = false
            val user = arguments.getParcelable<UserVM>(KEY_USER)
            mListener?.showTitle(getString(R.string.gift_title_gifts_by_user, user?.name))
            mListener?.showSubtitle(setCountingString(R.plurals.goods_count, giftsByUser.size))
            mListener?.setCollapsingToolbarImage(user?.photoMax ?: "", user != null)
            mGifts.clear()
            mGifts.addAll(giftsByUser)
            mGiftsAdapter.notifyDataSetChanged()
        }
        initRecyclerView(groupVM != null)
        val childCount = rv_gifts?.layoutManager?.childCount
        if (childCount == null || childCount >= mGiftsAdapter.itemCount ) removeListLoader()
    }

    private fun initRecyclerView(isAutoloading: Boolean) {
        with(rv_gifts) {
            val llm = LinearLayoutManager(activity)
            layoutManager = llm
            setHasFixedSize(false)
            itemAnimator = DefaultItemAnimator()
            adapter = mGiftsAdapter
            if (isAutoloading) addRecyclerViewAutoloading(llm) else mGiftsAdapter.removeLoader()
        }
    }

    private fun RecyclerView.addRecyclerViewAutoloading(llm: LinearLayoutManager) {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = llm.childCount
                    val totalItemCount = llm.itemCount
                    val pastVisibleItems = llm.findFirstVisibleItemPosition()

                    if (!mLoading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            mLoading = true
                            mPresenter.loadMore()
                        }
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        with(srl_refresh_gifts) {
            isRefreshing = false;
            destroyDrawingCache();
            clearAnimation();
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (getGroup() != null) {
            mMenu = menu
            inflater?.inflate(R.menu.menu_gifts, menu)
            var item = menu?.findItem(R.id.action_join_group)
            val isMember = getGroup()?.isMember == 1
            item?.setTitle(if (isMember) R.string.gift_menu_leave_group else R.string.gift_menu_join_group)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> mFilterDialog.show()
            R.id.action_join_group -> mPresenter.joinGroup(getGroup()?.isMember == 1)
        }
        return true
    }

    override fun changeMemberStatus(isMember: Boolean) {
        mGroup = getGroup()
        mGroup?.isMember = if (isMember) 1 else 0
        var item = mMenu?.findItem(R.id.action_join_group)
        item?.setTitle(if (isMember) R.string.gift_menu_leave_group else R.string.gift_menu_join_group)
    }

    override fun showError(errorText: String) {
        showErrorSnackbar(view, errorText)
    }

    override fun stopLoad() {
        mLoading = false
        srl_refresh_gifts.isRefreshing = false
    }

    override fun showError(errorTextRes: Int) {
        showErrorSnackbar(view, errorTextRes)
    }

    override fun startLoad() {
        srl_refresh_gifts.post { srl_refresh_gifts.isRefreshing = true }
    }

    override fun showData(data: Collection<GiftVM>) {
        mLoading = false
        mGiftsAdapter.clearAndAddGifts(data)
        val childCount = rv_gifts?.layoutManager?.childCount
        if (childCount == null || childCount >= mGiftsAdapter.itemCount ) removeListLoader()
    }

    override fun removeListLoader() {
        mGiftsAdapter.removeLoader()
    }

    override fun showFilteredGifts(filteredGiftsByGroup: List<GiftVM>) {
        mGiftsAdapter.animateTo(filteredGiftsByGroup)
        rv_gifts.scrollToPosition(0)
        showCount(filteredGiftsByGroup.size)
    }

    override fun showCount(count: Int) {
        mListener?.showSubtitle(setCountingString(R.plurals.goods_count, count))
    }

    interface IGiftsFragmentListener : ICommonFragmentCallback {
        fun openGiftDetailsFragment(gift: GiftVM)

    }

    companion object {
        val TAG = makeLogTag(GiftsFragment::class.java)
        val KEY_GROUP = "com.dmbaryshev.vkschool.view.groups.fragment.KEY_GROUP"
        val BUNDLE_KEY_GROUP = "com.dmbaryshev.vkschool.view.groups.fragment.BUNDLE_KEY_GROUP"
        val KEY_GIFTS = "com.dmbaryshev.vkschool.view.groups.fragment.KEY_GIFTS"
        val KEY_USER = "com.dmbaryshev.vkschool.view.groups.fragment.KEY_USER"

        fun newInstance(gifts: ArrayList<GiftVM>, user: UserVM): GiftsFragment {
            var fragment = GiftsFragment()
            var args = Bundle()
            args.putParcelableArrayList(KEY_GIFTS, gifts)
            args.putParcelable(KEY_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}

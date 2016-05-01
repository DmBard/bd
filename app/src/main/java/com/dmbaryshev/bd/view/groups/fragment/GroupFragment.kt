package com.dmbaryshev.bd.view.groups.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.GroupPresenter
import com.dmbaryshev.bd.utils.ctx
import com.dmbaryshev.bd.utils.inflate
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.utils.setCountingString
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.FilterDialog
import com.dmbaryshev.bd.view.common.ICommonFragmentCallback
import com.dmbaryshev.bd.view.common.adapter.IHolderClick
import com.dmbaryshev.bd.view.groups.adapter.GroupsAdapter
import kotlinx.android.synthetic.main.fragment_groups.*
import java.util.*

class GroupFragment : BaseFragment<GroupPresenter>(), IGroupView, IHolderClick {
    private val mGroupsAdapter by lazy { GroupsAdapter(mGroups, this) }
    private var mListener: IGroupsFragmentListener? = null
    private var mGroups: MutableList<GroupVM> = ArrayList()
    private var mAllGroups: MutableList<GroupVM> = ArrayList()
    private val mUser by lazy { arguments.getParcelable<UserVM>(KEY_USER) }
    private var mSwipeToRefresh: SwipeRefreshLayout? = null
    private val mFilterDialog by lazy {
        val onOkButtonListener: (String, Float, Float) -> Unit = { name, minPrice, maxPrice ->
            mPresenter.getFilteredGifts(name, minPrice, maxPrice)
        }
        FilterDialog(ctx, R.style.AppBottomSheetDialog, onOkButtonListener)
    }


    override val presenter: GroupPresenter = GroupPresenter()

    override fun onItemClick(adapterPosition: Int) {
        mListener?.openGoodsFragment(mGroupsAdapter.getGroup(adapterPosition))
    }

    override fun getUser(): UserVM {
        return mUser
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as IGroupsFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement FragmentButtonListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_groups)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        mSwipeToRefresh = srl_refresh_groups
        mSwipeToRefresh?.setOnRefreshListener({ mPresenter.forceLoad() })
        val userVM = arguments.getParcelable<UserVM>(KEY_USER)
        mListener?.showTitle(userVM.name)
        mListener?.showSubtitle(setCountingString(R.plurals.groups_count, mPresenter.count))
        mListener?.setCollapsingToolbarImage(userVM.photoMax)
        mPresenter.bindView(this)
        mPresenter.load()
    }

    private fun initRecyclerView() {
        with(rv_groups) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(false)
            itemAnimator = DefaultItemAnimator()
            adapter = mGroupsAdapter
        }
    }

    override fun onStop() {
        super.onStop()
        mSwipeToRefresh.apply {
            this?.isRefreshing = false
            this?.destroyDrawingCache()
            this?.clearAnimation()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_groups, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mGroupsAdapter.animateTo(mPresenter.getFilteredGroups(mAllGroups, query))
                rv_groups?.scrollToPosition(0)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> mFilterDialog.show()
        }
        return true
    }

    override fun showError(errorText: String) {
        showErrorSnackbar(view, errorText)
    }

    override fun stopLoad() {
        mSwipeToRefresh?.isRefreshing = false
    }

    override fun showError(errorTextRes: Int) {
        showErrorSnackbar(view, errorTextRes)
    }

    override fun startLoad() {
        mSwipeToRefresh?.post { mSwipeToRefresh?.isRefreshing = true }
    }

    override fun showData(data: Collection<GroupVM>) {
        mGroups.clear()
        mGroups.addAll(data)
        mAllGroups.clear()
        mAllGroups.addAll(data)
        mGroupsAdapter.notifyDataSetChanged()
    }

    override fun showFilteredGifts(filteredGifts: List<GiftVM>) {
        mListener?.openGoodsFragment(filteredGifts, mUser)
    }

    override fun showCount(count: Int) {
        mListener?.showSubtitle(setCountingString(R.plurals.groups_count, count))
    }

    interface IGroupsFragmentListener : ICommonFragmentCallback {
        fun openGoodsFragment(group: GroupVM)
        fun openGoodsFragment(filteredGifts: List<GiftVM>, userVM: UserVM)
    }

    companion object {
        val TAG = makeLogTag(GroupFragment::class.java)
        val KEY_USER = "com.dmbaryshev.vkschool.view.groups.fragment.KEY_USER"
    }
}
package com.dmbaryshev.bd.view.friends.fragment

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.TextView
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.ACTION_SORT_BY_BDATE
import com.dmbaryshev.bd.presenter.ACTION_SORT_BY_MAJORITY
import com.dmbaryshev.bd.presenter.ACTION_SORT_BY_NAME
import com.dmbaryshev.bd.presenter.FriendsPresenter
import com.dmbaryshev.bd.utils.*
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.ICommonFragmentCallback
import com.dmbaryshev.bd.view.friends.adapter.FriendsAdapter
import com.dmbaryshev.bd.view.friends.adapter.IFriendsHolderClick
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.*

class FriendsFragment : BaseFragment<FriendsPresenter>(), IFriendsView, IFriendsHolderClick {

    private val mFriendsAdapter by lazy { FriendsAdapter(mUsers, this) }
    private var mRecyclerView: RecyclerView? = null
    private var mListener: IFriendsFragmentListener? = null
    private var mUsers: MutableList<UserVM> = ArrayList()
    private var mAllUsers: MutableList<UserVM> = ArrayList()
    private var mSwipeToRefresh:SwipeRefreshLayout? = null
    private val mSortDialog by lazy {
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(ctx)
        with(bottomSheetDialog) {
            val view = layoutInflater?.inflate(R.layout.dialog_friends_sorting, null)
            setContentView(view)
            view?.find<TextView>(R.id.tv_by_name)?.setOnClickListener {
                mPresenter.sort(ACTION_SORT_BY_NAME)
                dismiss()
            }
            view?.find<TextView>(R.id.tv_by_bdate)?.setOnClickListener {
                mPresenter.sort(ACTION_SORT_BY_BDATE)
                dismiss()
            }
            view?.find<TextView>(R.id.tv_by_majority)?.setOnClickListener {
                mPresenter.sort(ACTION_SORT_BY_MAJORITY)
                dismiss()
            }
        }
        bottomSheetDialog
    }

    override val presenter: FriendsPresenter = FriendsPresenter()

    override fun openMessage(adapterPosition: Int) {
        mListener?.openMessageFragment(mFriendsAdapter.getUser(adapterPosition) ?: return)
    }

    override fun onItemClick(adapterPosition: Int) {
        mListener?.openGroupsFragment(mFriendsAdapter.getUser(adapterPosition)?: return)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as IFriendsFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement FragmentButtonListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_friends)
        setHasOptionsMenu(true)
        mListener?.setCollapsingToolbarImage("", false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        mSwipeToRefresh = srl_refresh_friends
        stopLoad()
        mSwipeToRefresh?.setOnRefreshListener({ mPresenter.forceLoad() })
        mListener?.showTitle(getString(R.string.fragment_title_friends))
        mListener?.showSubtitle(setCountingString(R.plurals.friends_count, mPresenter.count))
        mPresenter.bindView(this)
        mPresenter.load()
    }

    private fun initRecyclerView() {
        with(rv_friends) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(false)
            itemAnimator = DefaultItemAnimator()
            adapter = mFriendsAdapter
        }
        ViewCompat.setNestedScrollingEnabled(rv_friends, false)
        mRecyclerView = rv_friends
    }

    override fun onStop() {
        super.onStop()
        mSwipeToRefresh?.apply {
            isRefreshing = false
            destroyDrawingCache()
            clearAnimation()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_friends, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mFriendsAdapter.animateTo(mPresenter.getFilteredFriends(mAllUsers, query))
                mRecyclerView?.scrollToPosition(0)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_sort -> mSortDialog.show()
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
        mSwipeToRefresh?.isRefreshing = true
    }

    override fun showData(data: Collection<UserVM>) {
        mUsers.clear()
        mAllUsers.clear()
        mUsers.addAll(data)
        mAllUsers.addAll(data)
        mFriendsAdapter.notifyDataSetChanged()
    }

    override fun showCount(count: Int) {
        mListener?.showSubtitle(setCountingString(R.plurals.friends_count, count))
    }

    interface IFriendsFragmentListener : ICommonFragmentCallback {
        fun openMessageFragment(userVM: UserVM)
    }

    companion object {
        val TAG = makeLogTag(FriendsFragment::class.java)

        fun newInstance(): FriendsFragment {
            return FriendsFragment()
        }
    }
}

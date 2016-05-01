package com.dmbaryshev.bd.view.gift.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GiftVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.FavoriteGiftsPresenter
import com.dmbaryshev.bd.utils.inflate
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.utils.setCountingString
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.adapter.IHolderClick
import com.dmbaryshev.bd.view.common.adapter.IViewType
import com.dmbaryshev.bd.view.gift.adapter.GiftsAdapter
import kotlinx.android.synthetic.main.fragment_gifts.*
import java.util.*

class FavoriteGiftsFragment : BaseFragment<FavoriteGiftsPresenter>(), IFavoriteGiftsView, IHolderClick {
    private val mGiftsAdapter by lazy { GiftsAdapter(mGifts as MutableList<IViewType>, this) }
    private var mListener: GiftsFragment.IGiftsFragmentListener? = null
    private var mGifts: MutableList<GiftVM> = ArrayList()
    private var mLoading = false

    override val presenter: FavoriteGiftsPresenter = FavoriteGiftsPresenter()

    override fun onItemClick(adapterPosition: Int) {
        mListener?.openGiftDetailsFragment(mGiftsAdapter.getGift(adapterPosition))
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as GiftsFragment.IGiftsFragmentListener
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
        initRecyclerView()
        srl_refresh_gifts.setOnRefreshListener({ mPresenter.forceLoad() })
        val me = arguments.getParcelable<UserVM>(KEY_ME)
        mListener?.showTitle(me.name)
        mListener?.showSubtitle(setCountingString(R.plurals.goods_count, mPresenter.count))
        mListener?.setCollapsingToolbarImage(me.photoMax)
        mPresenter.bindView(this)
        mPresenter.load()
        val childCount = rv_gifts?.layoutManager?.childCount
        if(childCount == null || childCount  >= mGiftsAdapter.itemCount ) removeListLoader()

    }

    private fun initRecyclerView() {
        with(rv_gifts) {
            val llm = LinearLayoutManager(activity)
            layoutManager = llm
            setHasFixedSize(false)
            itemAnimator = DefaultItemAnimator()
            adapter = mGiftsAdapter
            addRecyclerViewAutoloading(llm)
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
        if(childCount == null || childCount  >= mGiftsAdapter.itemCount ) removeListLoader()
    }

    override fun removeListLoader() {
        mGiftsAdapter.removeLoader()
    }

    override fun showCount(count: Int) {
        mListener?.showSubtitle(setCountingString(R.plurals.goods_count, count))
    }

    companion object {
        val TAG = makeLogTag(FavoriteGiftsFragment::class.java)
        val KEY_ME = "com.dmbaryshev.vkschool.view.gift.KEY_ME"
    }
}
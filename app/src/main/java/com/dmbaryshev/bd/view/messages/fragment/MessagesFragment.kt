package com.dmbaryshev.bd.view.messages.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.GroupVM
import com.dmbaryshev.bd.model.view_model.MessageVM
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.MessagePresenter
import com.dmbaryshev.bd.utils.convertTimestampToString
import com.dmbaryshev.bd.utils.dogi
import com.dmbaryshev.bd.utils.makeLogTag
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.ICommonFragmentCallback
import com.dmbaryshev.bd.view.messages.adapter.MessagesAdapter
import kotlinx.android.synthetic.main.fragment_messages.*

class MessagesFragment : BaseFragment<MessagePresenter>(), IMessageView {

    private val mMessagesAdapter by lazy { MessagesAdapter() }
    private var mListener: ICommonFragmentCallback? = null
    private var mLoading = false

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as ICommonFragmentCallback
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement FragmentButtonListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)
        iv_send.setOnClickListener { v -> mPresenter.sendMessage(et_message.text.toString()) }

        mPresenter.bindView(this)
        val userVM = arguments.getParcelable<UserVM>(KEY_USER)
        val groupVM = arguments.getParcelable<GroupVM>(KEY_GROUP)
        val giftTitle = arguments.getString(KEY_GIFT_TITLE)
        mPresenter.userVM = userVM
        mPresenter.groupVM = groupVM
        if (giftTitle != null) {
            et_message.setText(getString(R.string.message_edit_text_message, giftTitle))
        }
        setTitles(userVM, groupVM)
        mListener?.setCollapsingToolbarImage("", false)
        mPresenter.load()
    }

    private fun setTitles(userVM: UserVM?, groupVM: GroupVM?) {
        if (userVM != null) {
            mListener?.showTitle(userVM.name)
            mListener?.showSubtitle(if (userVM.online == 1) getString(R.string.status_online)
                                    else {
                if (userVM.lastSeen != null) {
                    "${getString(R.string.fragment_subtitle_message)} ${convertTimestampToString(
                            userVM.lastSeen?.time)}"
                } else ""
            })
        } else if (groupVM != null) {
            mListener?.showTitle(groupVM.name)
            mListener?.showSubtitle("")
        }
    }

    private fun initRecyclerView(view: View) {
        val rvMessages = view.findViewById(R.id.rv_messages) as RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        rvMessages.layoutManager = layoutManager
        rvMessages.setHasFixedSize(true)
        rvMessages.itemAnimator = DefaultItemAnimator()
        rvMessages.adapter = mMessagesAdapter
        rvMessages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy < 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (!mLoading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            mLoading = true
                            mPresenter.loadMore()
                        }
                    }
                }
            }
        })

        ViewCompat.setNestedScrollingEnabled(rvMessages, false)
    }

    override val presenter: MessagePresenter
        get() = MessagePresenter()

    override fun showData(data: Collection<MessageVM>) {
        dogi(TAG, "showMessages: answer" + data)
        mMessagesAdapter.clearAndAddMessages(data)
        val visibleItems = rv_messages?.layoutManager?.childCount
        if (visibleItems == mMessagesAdapter.itemCount) removeListLoader()
    }

    fun removeListLoader() {
        mMessagesAdapter.removeLoader()
    }

    override fun showCount(count: Int) {

    }

    override fun addMessage(messageVM: MessageVM) {
        mMessagesAdapter.addMessage(messageVM)
        et_message.setText("")
        rv_messages.scrollToPosition(0)
    }

    override fun showError(errorText: String) {
        showErrorSnackbar(view, errorText)
    }

    override fun stopLoad() {
        mLoading = false
    }

    override fun showError(errorTextRes: Int) {
        showErrorSnackbar(view, errorTextRes)
    }

    override fun startLoad() {
    }

    companion object {
        val TAG = makeLogTag(MessagesFragment::class.java)

        val KEY_USER = "com.dmbaryshev.vkschool.view.messages.fragment.ID_USER"
        val KEY_GROUP = "com.dmbaryshev.vkschool.view.messages.fragment.GROUP"
        val KEY_GIFT_TITLE = "com.dmbaryshev.vkschool.view.messages.fragment.GIFT_TITLE"
    }
}

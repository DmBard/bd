package com.dmbaryshev.bd.view.calendar

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmbaryshev.bd.R
import com.dmbaryshev.bd.model.view_model.UserVM
import com.dmbaryshev.bd.presenter.CalendarPresenter
import com.dmbaryshev.bd.utils.*
import com.dmbaryshev.bd.view.common.BaseFragment
import com.dmbaryshev.bd.view.common.ICommonFragmentCallback
import com.dmbaryshev.bd.view.common.adapter.IHolderClick
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

class CalendarFragment : BaseFragment<CalendarPresenter>(), ICalendarView, IHolderClick {
    override val presenter: CalendarPresenter = CalendarPresenter()
    private var mListener: ICalendarFragmentListener? = null
    private var mUsers: MutableList<UserVM>? = ArrayList()
    private val mAdapter by lazy { CalendarUsersAdapter(mUsers, this) }
    private val mDialog by lazy {
        val mBottomSheetDialog = BottomSheetDialog(act);
        with(mBottomSheetDialog) {
            val view = layoutInflater.inflate(R.layout.dialog_calendar_users, null)
            val rv = view.find<RecyclerView>(R.id.rv_calendar)
            rv.layoutManager = LinearLayoutManager(act)
            rv.setHasFixedSize(false)
            rv.itemAnimator = DefaultItemAnimator()
            rv.adapter = mAdapter
            setContentView(view)
        }
        mBottomSheetDialog
    }

    override fun onItemClick(adapterPosition: Int) {
        mListener?.openGroupsFragment(mAdapter.getUser(adapterPosition))
        mDialog.dismiss()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as ICalendarFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement FragmentButtonListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_calendar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener?.showTitle(getString(R.string.fragment_title_calendar))
        mListener?.showSubtitle("")
        mListener?.setCollapsingToolbarImage("", false)
        mPresenter.bindView(this)
        initCalendar()
        ViewCompat.setNestedScrollingEnabled(nsl_calendar, false)
    }

    private fun initCalendar() {
        with(calendar) {
            firstDayOfWeek = Calendar.MONDAY
            addDecorator(EventDecorator(mPresenter.getEvents(),
                                        resources.getDrawable(R.drawable.drawable_calendar_decorator)))
            setOnDateChangedListener { widget, calendarDay, isSelected ->
                mPresenter.getBirthdaysByDate(calendarDay.day,
                                              calendarDay.month + 1)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        mDialog.dismiss()
    }

    override fun getUsers(): List<UserVM> {
        return arguments.getParcelableArrayList<UserVM>(KEY_USERS)
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

    override fun showData(data: Collection<UserVM>) {
        dogi(makeLogTag(CalendarFragment::class.java), "${data.size}")
        if (data.size == 0) return
        mUsers?.clear()
        mUsers?.addAll(data)
        mAdapter.notifyDataSetChanged()
        mDialog.show()
    }

    override fun showCount(count: Int) {
    }

    interface ICalendarFragmentListener : ICommonFragmentCallback {
    }

    companion object {
        val TAG = makeLogTag(CalendarFragment::class.java)
        val KEY_USERS = "com.dmbaryshev.vkschool.view.calendar.KEY_USER"

        fun newInstance(users: ArrayList<UserVM>): CalendarFragment {
            var fragment = CalendarFragment()
            var args = Bundle()
            args.putParcelableArrayList(KEY_USERS, users)
            fragment.arguments = args
            return fragment
        }
    }
}
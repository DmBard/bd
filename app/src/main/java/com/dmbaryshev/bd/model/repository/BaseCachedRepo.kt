package com.dmbaryshev.bd.model.repository

import com.dmbaryshev.bd.model.dto.common.RealmCachedObject
import com.dmbaryshev.bd.model.dto.common.VkError
import com.dmbaryshev.bd.model.view_model.IViewModel
import com.dmbaryshev.bd.network.ResponseAnswer
import com.dmbaryshev.bd.utils.now
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

abstract class BaseCachedRepo<V : RealmObject, VM : IViewModel>: BaseRepo<V, VM>() {

    override  var isSaved:Boolean = true

    protected fun getItemsFromDb(dbConcreteObservable: Observable<RealmResults<V>>): Observable<ResponseAnswer<VM>> {
        val dbObservable = dbConcreteObservable.map({ initDbResponseAnswer(it) }).observeOn(AndroidSchedulers.mainThread())
        return dbObservable
    }

    private fun initDbResponseAnswer(realmResults: RealmResults<V>?): ResponseAnswer<VM> {
        val vkError: VkError? = null
        val responseAnswer = ResponseAnswer(realmResults?.count() ?: 0,
                                            realmResults.orEmpty(),
                                            vkError)
        return initMapper().execute(responseAnswer)
    }

    override  fun save(items: List<V>?) {
        val realm = Realm.getDefaultInstance()
        val updateTime = now()
        realm.beginTransaction()
        items?.forEach {
            if (it is RealmCachedObject) {
                it.updateTime = updateTime
                val changedItem = getUpdatedItem(realm, it)
                realm.copyToRealmOrUpdate(changedItem)
            }
        }
        var deletedItems: RealmResults<V>? = getDeletedItems(realm, updateTime)
        deletedItems?.clear()

        realm.commitTransaction()
        realm.close()
    }

    protected abstract fun getUpdatedItem(realm: Realm, it: RealmCachedObject): V

    protected abstract fun getDeletedItems(realm: Realm, now: Long): RealmResults<V>?
}

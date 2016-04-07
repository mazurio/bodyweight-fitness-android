package com.bodyweight.fitness.stream

import rx.Observable
import rx.subjects.PublishSubject

object Stream {
    private val mMenuSubject = PublishSubject.create<Int>()
    private val mDrawerSubject = PublishSubject.create<Int>()
    private val mLoggedSecondsSubject = PublishSubject.create<Int>()

    val menuObservable: Observable<Int> get() = mMenuSubject
    val drawerObservable: Observable<Int> get() = mDrawerSubject
    val loggedSecondsObservable: Observable<Int> get() = mLoggedSecondsSubject

    fun setMenu(menuId: Int) = mMenuSubject.onNext(menuId)
    fun setDrawer(menuId: Int) = mDrawerSubject.onNext(menuId)
    fun setLoggedSeconds(loggedSeconds: Int) = mLoggedSecondsSubject.onNext(loggedSeconds)
}
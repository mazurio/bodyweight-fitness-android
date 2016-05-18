package com.bodyweight.fitness.stream

import com.bodyweight.fitness.model.CalendarDay

import rx.Observable
import rx.subjects.PublishSubject

data class SetReps(val set: Int, val reps: Int)

enum class DialogType { LogWorkout, Progress }
data class Dialog(val dialogType: DialogType, val exerciseId: String)

object UiEvent {
    private val dialogSubject = PublishSubject.create<Dialog>()

    val dialogObservable: Observable<Dialog> get() = dialogSubject

    fun showDialog(dialogType: DialogType, exerciseId: String) {
        dialogSubject.onNext(Dialog(dialogType, exerciseId))
    }
}

object Stream {
    var currentCalendarPage: Int = 60
        private set

    var currentCalendarDay: CalendarDay = CalendarDay()
        private set

    private val mMenuSubject = PublishSubject.create<Int>()
    private val mDrawerSubject = PublishSubject.create<Int>()
    private val mLoggedSecondsSubject = PublishSubject.create<Int>()
    private val mLoggedSetRepsSubject = PublishSubject.create<SetReps>()

    private val mCalendarPageSubject = PublishSubject.create<Int>()
    private val mCalendarDaySubject = PublishSubject.create<CalendarDay>()

    /**
     * Emits when changes to repository have been made.
     */
    private val mRepositorySubject = PublishSubject.create<Boolean>()

    /**
     * All observables should be fun() similar to RoutineStream
     */
    val menuObservable: Observable<Int> get() = mMenuSubject
    val drawerObservable: Observable<Int> get() = mDrawerSubject
    val loggedSecondsObservable: Observable<Int> get() = mLoggedSecondsSubject
    val loggedSetRepsObservable: Observable<SetReps> get() = mLoggedSetRepsSubject
    val calendarPageObservable: Observable<Int> get() = mCalendarPageSubject
    val calendarDayObservable: Observable<CalendarDay> get() = mCalendarDaySubject
    val repositoryObservable: Observable<Boolean> get() = mRepositorySubject

    fun setMenu(menuId: Int) {
        mMenuSubject.onNext(menuId)
    }

    fun setDrawer(menuId: Int) {
        mDrawerSubject.onNext(menuId)
    }

    fun setLoggedSeconds(loggedSeconds: Int) {
        mLoggedSecondsSubject.onNext(loggedSeconds)
    }

    fun setLoggedSetReps(setReps: SetReps) {
        mLoggedSetRepsSubject.onNext(setReps)
    }

    fun streamPage(page: Int) {
        currentCalendarPage = page
        mCalendarPageSubject.onNext(page)
    }

    fun streamDay(day: CalendarDay) {
        currentCalendarDay = day
        mCalendarDaySubject.onNext(day)
    }

    fun setRepository() {
        mRepositorySubject.onNext(true)
    }
}
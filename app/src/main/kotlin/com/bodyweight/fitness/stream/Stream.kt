package com.bodyweight.fitness.stream

import com.bodyweight.fitness.R
import com.bodyweight.fitness.model.CalendarDay

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.PublishSubject

data class SetReps(val set: Int, val reps: Int)

enum class DialogType { LogWorkout, Progress }
data class Dialog(val dialogType: DialogType, val exerciseId: String)

object UiEvent {
    private val dialogSubject = PublishSubject.create<Dialog>()
    private val homePageSubject = PublishSubject.create<Int>()

    val dialogObservable: Observable<Dialog> get() = dialogSubject
    val homePageObservable: Observable<Int> get() = homePageSubject

    fun showDialog(dialogType: DialogType, exerciseId: String) {
        dialogSubject.onNext(Dialog(dialogType, exerciseId))
    }

    fun showHomePage(position: Int) {
        homePageSubject.onNext(position)
    }
}

object Stream {
    var currentDrawerId: Int = R.id.action_menu_home
        private set

    var currentCalendarPage: Int = 60
        private set

    var currentCalendarDay: CalendarDay = CalendarDay()
        private set

    var currentHomePage: Int = 0
        private set

    private val menuSubject = PublishSubject.create<Int>()
    private val drawerSubject = PublishSubject.create<Int>()
    private val loggedSecondsSubject = PublishSubject.create<Int>()
    private val loggedSetRepsSubject = PublishSubject.create<SetReps>()

    private val calendarPageSubject = PublishSubject.create<Int>()
    private val calendarDaySubject = PublishSubject.create<CalendarDay>()

    private val homePageSubject = PublishSubject.create<Int>()

    /**
     * Emits when changes to repository have been made.
     */
    private val repositorySubject = PublishSubject.create<Boolean>()

    /**
     * Observables that should be re-emitted should be functions rather than values.
     */
    val menuObservable: Observable<Int> get() = menuSubject
    val loggedSecondsObservable: Observable<Int> get() = loggedSecondsSubject
    val loggedSetRepsObservable: Observable<SetReps> get() = loggedSetRepsSubject
    val repositoryObservable: Observable<Boolean> get() = repositorySubject

    fun drawerObservable(): Observable<Int> {
        return Observable.merge(Observable.just(currentDrawerId).publish().refCount(), drawerSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount()
    }

    fun calendarPageObservable(): Observable<Int> {
        return Observable.merge(Observable.just(currentCalendarPage).publish().refCount(), calendarPageSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount()
    }

    fun calendarDayObservable(): Observable<CalendarDay> {
        return Observable.merge(Observable.just(currentCalendarDay).publish().refCount(), calendarDaySubject)
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount()
    }

    fun homePageObservable(): Observable<Int> {
        return Observable.merge(Observable.just(currentHomePage).publish().refCount(), homePageSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount()
    }

    fun setMenu(toolbarMenuItemId: Int) {
        menuSubject.onNext(toolbarMenuItemId)
    }

    fun setDrawer(drawerMenuItemId: Int) {
        if (drawerMenuItemId.equals(R.id.action_menu_home) || drawerMenuItemId.equals(R.id.action_menu_workout_log)) {
            currentDrawerId = drawerMenuItemId
        }

        drawerSubject.onNext(drawerMenuItemId)
    }

    fun setLoggedSeconds(loggedSeconds: Int) {
        loggedSecondsSubject.onNext(loggedSeconds)
    }

    fun setLoggedSetReps(setReps: SetReps) {
        loggedSetRepsSubject.onNext(setReps)
    }

    fun setCalendarPage(page: Int) {
        currentCalendarPage = page
        calendarPageSubject.onNext(page)
    }

    fun setCalendarDay(day: CalendarDay) {
        currentCalendarDay = day
        calendarDaySubject.onNext(day)
    }

    fun setRepository() {
        repositorySubject.onNext(true)
    }

    fun setHomePage(page: Int) {
        currentHomePage = page
        homePageSubject.onNext(page)
    }
}
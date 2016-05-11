package com.bodyweight.fitness.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

import java.util.*

open class RepositoryRoutine(
    @PrimaryKey
    @Required
    open var id: String = "",

    @Index
    open var routineId: String = "routine0",

    open var title: String = "",
    open var subtitle: String = "",

    @Index
    open var startTime: Date = Date(),

    @Index
    open var lastUpdatedTime: Date = Date(),

    open var categories: RealmList<RepositoryCategory> = RealmList(),
    open var sections: RealmList<RepositorySection> = RealmList(),
    open var exercises: RealmList<RepositoryExercise> = RealmList()
) : RealmObject() {}

open class RepositoryCategory(
    @PrimaryKey
    @Required
    open var id: String = "",

    @Index
    open var categoryId: String = "",

    open var title: String = "",

    open var routine: RepositoryRoutine? = null,

    open var sections: RealmList<RepositorySection> = RealmList(),
    open var exercises: RealmList<RepositoryExercise> = RealmList()
) : RealmObject() {}

open class RepositorySection(
    @PrimaryKey
    @Required
    open var id: String = "",

    @Index
    open var sectionId: String = "",

    open var title: String = "",
    open var mode: String = "",

    open var routine: RepositoryRoutine? = null,
    open var category: RepositoryCategory? = null,

    open var exercises: RealmList<RepositoryExercise> = RealmList()
) : RealmObject() {}

open class RepositoryExercise(
    @PrimaryKey
    @Required
    open var id: String = "",

    @Index open var exerciseId: String = "",

    open var title: String = "",
    open var description: String = "",
    open var defaultSet: String = "timed",

    open var isVisible: Boolean = false,

    open var routine: RepositoryRoutine? = null,
    open var category: RepositoryCategory? = null,
    open var section: RepositorySection? = null,

    open var sets: RealmList<RepositorySet> = RealmList()
) : RealmObject() {}

open class RepositorySet(
    @PrimaryKey
    @Required
    open var id: String = "",

    open var isTimed: Boolean = false,

    open var weight: Double = 0.0,
    open var reps: Int = 0,
    open var seconds: Int = 0,

    open var exercise: RepositoryExercise? = null
) : RealmObject() {}

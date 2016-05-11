package com.bodyweight.fitness.model

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class JSONRoutine {
    @SerializedName("routineId")
    var routineId: String = ""

    @SerializedName("title")
    var title: String = ""

    @SerializedName("subtitle")
    var subtitle: String = ""

    @SerializedName("routine")
    var partRoutines = ArrayList<JSONLinkedRoutine>()

    val size: Int by lazy {
        partRoutines.size
    }
}

class JSONLinkedRoutine {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("categoryId")
    var categoryId: String = ""

    @SerializedName("sectionId")
    var sectionId: String = ""

    @SerializedName("exerciseId")
    var exerciseId: String = ""

    @SerializedName("level")
    var level: String = ""

    @SerializedName("title")
    var title: String = ""

    @SerializedName("description")
    var description: String = ""

    @SerializedName("youTubeId")
    var youTubeId: String = ""

    @SerializedName("gifId")
    var gifId: String = ""

    @SerializedName("gifUrl")
    var gifUrl: String = ""

    @SerializedName("type")
    private var mType: String = ""

    @SerializedName("mode")
    private var mMode: String = ""

    @SerializedName("defaultSet")
    var defaultSet: String = ""

    val type: RoutineType by lazy {
        if (mType.matches("category".toRegex())) {
            RoutineType.CATEGORY
        } else if (mType.matches("section".toRegex())) {
            RoutineType.SECTION
        } else {
            RoutineType.EXERCISE
        }
    }

    val mode: SectionMode by lazy {
        if (mMode.matches("all".toRegex())) {
            SectionMode.ALL
        } else if (mMode.matches("pick".toRegex())) {
            SectionMode.PICK
        } else {
            SectionMode.LEVELS
        }
    }
}
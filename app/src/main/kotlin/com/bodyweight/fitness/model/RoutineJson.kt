package com.bodyweight.fitness.model

import java.util.ArrayList

class JSONRoutine {
    var routineId: String = ""
    var title: String = ""
    var subtitle: String = ""
    var shortDescription: String = ""
    var url: String = ""
    var routine = ArrayList<JSONLinkedRoutine>()

    val size: Int by lazy {
        routine.size
    }
}

class JSONLinkedRoutine {
    var categoryId: String = ""
    var sectionId: String = ""
    var exerciseId: String = ""
    var level: String = ""
    var title: String = ""
    var description: String = ""
    var youTubeId: String = ""
    var videoId: String = ""

    private var type: String = ""
    private var mode: String = ""

    var defaultSet: String = ""

    val routineType: RoutineType by lazy {
        if (type.equals("category")) {
            RoutineType.Category
        } else if (type.equals("section")) {
            RoutineType.Section
        } else {
            RoutineType.Exercise
        }
    }

    val sectionMode: SectionMode by lazy {
        if (mode.equals("all")) {
            SectionMode.All
        } else if (mode.equals("pick")) {
            SectionMode.Pick
        } else {
            SectionMode.Levels
        }
    }
}
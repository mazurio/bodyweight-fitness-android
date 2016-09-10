package com.bodyweight.fitness.adapter

import com.bodyweight.fitness.model.*
import com.robinhood.spark.SparkAdapter

import java.util.*

class RepsAdapter : SparkAdapter() {
    private var data: ArrayList<DateTimeRepositorySet>? = null

    fun changeData(data: ArrayList<DateTimeRepositorySet>) {
        this.data = data

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        data?.let {
            return it.size
        }

        return 0
    }

    override fun getItem(index: Int): Any {
        val item = data?.getOrNull(index)

        item?.let {
            return it
        }

        return ""
    }

    override fun getY(index: Int): Float {
        data?.getOrNull(index)?.repositorySet?.let {
            return it.reps.toFloat()
        }

        return 0f
    }

    override fun getX(index: Int): Float {
        return index.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return 15f
    }
}

class TimeAdapter : SparkAdapter() {
    private var data: ArrayList<DateTimeRepositorySet>? = ArrayList()

    fun changeData(data: ArrayList<DateTimeRepositorySet>) {
        this.data = data

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        data?.let {
            return it.size
        }

        return 0
    }

    override fun getItem(index: Int): Any {
        val item = data?.getOrNull(index)

        item?.let {
            return it
        }

        return ""
    }

    override fun getY(index: Int): Float {
        data?.getOrNull(index)?.repositorySet?.let {
            return it.seconds.toFloat()
        }

        return 0f
    }

    override fun getX(index: Int): Float {
        return index.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return 120f
    }
}

class WorkoutLengthAdapter : SparkAdapter() {
    private var data = ArrayList<DateTimeWorkoutLength>()

    fun changeData(data: ArrayList<DateTimeWorkoutLength>) {
        this.data = data

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): Any {
        val item = data.getOrNull(index)

        item?.let {
            return it
        }

        return ""
    }

    override fun getY(index: Int): Float {
        data[index].repositoryRoutine?.let {
            return RepositoryRoutine.getWorkoutLengthInMinutes(it).toFloat()
        }

        return 0f
    }

    override fun getX(index: Int): Float {
        return index.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return 60.0f
    }
}

class CategoryCompletionRateAdapter : SparkAdapter() {
    private var data = ArrayList<CategoryDateTimeCompletionRate>()

    fun changeData(data: ArrayList<CategoryDateTimeCompletionRate>) {
        this.data = data

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): Any {
        val item = data.getOrNull(index)

        item?.let {
            return it
        }

        return ""
    }

    override fun getY(index: Int): Float {
        data[index].repositoryCategory?.let {
            return RepositoryCategory.getCompletionRate(it).percentage.toFloat()
        }

        return 0f
    }

    override fun getX(index: Int): Float {
        return index.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return 100.0f
    }
}

class CompletionRateAdapter : SparkAdapter() {
    private var data = ArrayList<DateTimeCompletionRate>()

    fun changeData(data: ArrayList<DateTimeCompletionRate>) {
        this.data = data

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): Any {
        val item = data.getOrNull(index)

        item?.let {
            return it
        }

        return ""
    }

    override fun getY(index: Int): Float {
        data[index].repositoryRoutine?.let {
            return RepositoryRoutine.getCompletionRate(it).percentage.toFloat()
        }

        return 0f
    }

    override fun getX(index: Int): Float {
        return index.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return 100.0f
    }
}
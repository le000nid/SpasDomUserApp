/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.spasdomuserapp.database

import androidx.lifecycle.Transformations
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spasdomuserapp.domain.PlannedOrder
import com.example.spasdomuserapp.domain.NewsItem
import com.example.spasdomuserapp.domain.Alert
import okhttp3.Cache

@Entity
data class DatabaseNewsItem constructor(
    @PrimaryKey
    val url: String,
    val updated: String,
    val title: String,
    val description: String,
    val thumbnail: String)

fun List<DatabaseNewsItem>.asDomainModel(): List<NewsItem> {
    return map {
        NewsItem(
                url = it.url,
                title = it.title,
                description = it.description,
                updated = it.updated,
                thumbnail = it.thumbnail)
    }
}

// TODO(Replace primary key)

@Entity
data class DataBaseAlert(
    val data: String,
    val title: String,
    @PrimaryKey
    val description: String
)

fun List<DataBaseAlert>.asDomainAlertModel(): List<Alert> {
    return map {
        Alert(
            data = it.data,
            title = it.title,
            description = it.description
        )
    }
}


@Entity
data class CachePlannedOrder(
    @PrimaryKey
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val userRate: Int,
    val userReview: String?,
    val isFinished: Boolean,
    val workerImg: String,
    val workerName: String,
    val workerRate: Int,
    val workerInfo: String
)

fun List<CachePlannedOrder>.asDomainPlannedOrder(): List<PlannedOrder> {
    return map {
        PlannedOrder(
            id = it.id,
            title = it.title,
            date = it.date,
            time = it.time,
            userRate = it.userRate,
            userReview = it.userReview,
            isFinished = it.isFinished,
            workerImg = it.workerImg,
            workerName = it.workerName,
            workerRate = it.workerRate,
            workerInfo = it.workerInfo
        )
    }
}

fun PlannedOrder.asCachePlannerOrder(): CachePlannedOrder {
    return CachePlannedOrder(
        id = id,
        title = title,
        date = date,
        time = time,
        userRate = userRate,
        userReview = userReview,
        isFinished = isFinished,
        workerImg = workerImg,
        workerName = workerName,
        workerRate = workerRate,
        workerInfo = workerInfo
    )
}
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

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spasdomuserapp.domain.PlannedOrder
import com.example.spasdomuserapp.domain.NewsItem
import com.example.spasdomuserapp.domain.Alert

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
    val title: String,
    @PrimaryKey
    val desc: String,
    val rate: Int,
    val review: String?,
    val isFinished: Boolean
)

fun List<CachePlannedOrder>.asDomainPlannedOrder(): List<PlannedOrder> {
    return map {
        PlannedOrder(
            title = it.title,
            desc = it.desc,
            rate = it.rate,
            review = it.review,
            isFinished = it.isFinished
        )
    }
}

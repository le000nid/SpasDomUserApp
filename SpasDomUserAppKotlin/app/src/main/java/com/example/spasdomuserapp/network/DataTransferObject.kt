package com.example.spasdomuserapp.network

import com.example.spasdomuserapp.database.CachePlannedOrder
import com.example.spasdomuserapp.database.DataBaseAlert
import com.example.spasdomuserapp.database.DatabaseNewsItem
import com.example.spasdomuserapp.domain.PlannedOrder
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 * NewsHolder holds a list of Videos.
 * This is to parse first level of our network result which looks like
 * {
 *   "news": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkNewsContainer(val videos: List<NetworkNewsItem>)

/**
 * News represent a newsItem that can be opened.
 */
@JsonClass(generateAdapter = true)
data class NetworkNewsItem(
    val title: String,
    val description: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
    val closedCaptions: String?)


fun NetworkNewsContainer.asDatabaseModel(): Array<DatabaseNewsItem> {
    return videos.map {
        DatabaseNewsItem (
            title = it.title,
            description = it.description,
            url = it.url,
            updated = it.updated,
            thumbnail = it.thumbnail)
    }.toTypedArray()
}



@JsonClass(generateAdapter = true)
data class NetworkAlertsContainer(val alerts: List<NetworkAlerts>)

@JsonClass(generateAdapter = true)
data class NetworkAlerts(
    val data: String,
    val title: String,
    val description: String)

fun NetworkAlertsContainer.asDatabaseAlertModel(): Array<DataBaseAlert> {
    return alerts.map {
        DataBaseAlert (
            data = it.data,
            title = it.title,
            description = it.description)
    }.toTypedArray()
}



@JsonClass(generateAdapter = true)
data class NetworkPlannedOrdersContainer(val orders: List<PlannedOrder>)

@JsonClass(generateAdapter = true)
data class NetworkPlannedOrders(
    val title: String,
    val desc: String,
    val rate: Int?,
    val review: String?,
    val isFinished: Boolean)

fun NetworkPlannedOrdersContainer.asCachePlannedOrderModel(): Array<CachePlannedOrder> {
    return orders.map {
        CachePlannedOrder (
            title = it.title,
            desc = it.desc,
            rate = it.rate,
            review = it.review,
            isFinished = it.isFinished)
    }.toTypedArray()
}
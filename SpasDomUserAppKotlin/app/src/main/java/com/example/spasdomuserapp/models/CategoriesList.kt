package com.example.spasdomuserapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoriesList (
    val label: String,
    val categoryId: Int,
    val drawableUrl: String,
    val subcategory: List<CategoriesList>?
    ): Parcelable
package com.uniquindio.thecatapp.core.navigation

object AppRoutes {
    const val CAT_ID_ARG = "catId"
    const val LIST = "cat_list"
    const val DETAIL = "cat_detail/{$CAT_ID_ARG}"

    fun detail(catId: String): String = "cat_detail/$catId"
}


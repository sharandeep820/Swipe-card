package com.yuyakaido.android.cardstackview.sample

data class Spot(
        val id: Long = counter++,
        val name: String,
        val state: String,
        val city: String,
        val url: String,
        val courseTracks: String,
        val distance : String
) {
    companion object {
        private var counter = 0L
    }
}

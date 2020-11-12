package com.anioncode.smogu.events

/**
 * Navigation Events for RxJava
 */
class NavEvent(
    val destination: Destination
) {
    enum class Destination {
        StatsFragment, ChartFragment,MapFragment,InfoFragment,AboutFragment
    }
}
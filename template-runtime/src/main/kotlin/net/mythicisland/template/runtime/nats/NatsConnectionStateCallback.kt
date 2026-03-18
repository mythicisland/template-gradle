package net.mythicisland.template.runtime.nats

fun interface NatsConnectionStateCallback {
    fun onConnectionStateChanged(connected: Boolean)
}
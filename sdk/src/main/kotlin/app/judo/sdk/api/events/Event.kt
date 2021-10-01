package app.judo.sdk.api.events

import app.judo.sdk.api.analytics.AnalyticsEvent
import app.judo.sdk.api.models.Action
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Node
import app.judo.sdk.api.models.Screen

/**
 * These are the types of the Events emitted on the Event Bus.
 */
sealed class Event {
    /**
     * This Event is fired when the user views an Experience screen.
     *
     * @param experience The data model of the Experience.
     * @param screen The specific screen being Viewed.
     * @param dataContext A hashMap of all the data made available to the Screen being viewed.
     * This has three fields: `data` (the Data Source data being shown on the Screen), `url` (query
     * parameters on the URL used to open the Experience), and `user` (the user info supplied to
     * the Experience by your app code).
     */
    data class ScreenViewed(val experience: Experience, val screen: Screen, val dataContext: Map<String, Any?>): Event()

    /**
     * This Event is fired when the user taps (or otherwise activates) an Action, such as a button.
     *
     * @param experience The data model of the Experience.
     * @param screen The specific screen being Viewed.
     * @param node The node on which the Action was received (ie. the button that was tapped)
     * @param action The action data type that describes what behaviour will be executed for this action.
     * @param dataContext A hashMap of all the data made available to node that was tapped.
     * This has three fields: `data` (the Data Source data being shown on the Screen), `url` (query
     * parameters on the URL used to open the Experience), and `user` (the user info supplied to
     * the Experience by your app code).
     */
    internal data class ActionReceived(
        val experience: Experience,
        val screen: Screen,
        val node: Node,
        val action: Action,
        val dataContext: Map<String, Any?>
    ): Event()

    /**
     * This event is fired when ProfileService sees an update to user profile (ids, traits, etc.)
     * information.
     */
    internal object Identified: Event()

    internal object PushTokenUpdated: Event()

}

fun interface ScreenViewedCallback {
    fun screenViewed(event: Event.ScreenViewed)
}

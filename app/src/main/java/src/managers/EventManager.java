package src.managers;


import com.squareup.otto.Bus;

/**
 * A Singleton class which provides a proper
 * way of transferring data between app's components.
 *
 * Usage (post an event):
 *
 * EventManager.geInstance().post(new TestEvent());
 *
 * Usage (consume an event):
 *
 * "@Subscribe"
 * public void onTestEventAvailable(TestEvent testEvent) {
 *     if (testEvent != null) {
 *         do something in here...
 *     }
 * }
 *
 */
public class EventManager {
    // <Singleton>
    private static final Bus busInstance = new Bus();

    public static Bus getInstance() {
        return busInstance;
    }

    private EventManager() {}
    // </Singleton>
}

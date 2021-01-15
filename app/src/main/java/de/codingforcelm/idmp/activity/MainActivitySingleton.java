package de.codingforcelm.idmp.activity;

/**
 * Singleton class for MainActivity
 */
public class MainActivitySingleton {

    private static volatile MainActivitySingleton INSTANCE;
    private MainActivity mainActivity;

    private MainActivitySingleton() {

    }

    /**
     * Returns MainActivitySingleton
     * @return Singleton Instance
     */
    public static MainActivitySingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainActivitySingleton();
        }
        return INSTANCE;
    }

    /**
     * Returns MainActivity
     * @return MainActivity
     */
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     * Sets MainActivity
     * @param mainActivity MainActivity
     */
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}

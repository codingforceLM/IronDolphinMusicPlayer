package de.codingforcelm.idmp;

public class MainActivitySingleton {

    private static volatile MainActivitySingleton INSTANCE;
    private MainActivity mainActivity;

    private MainActivitySingleton() {

    }

    public static MainActivitySingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainActivitySingleton();
        }
        return INSTANCE;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}

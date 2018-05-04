package independent_study.multiplayer.util;

public interface DispatchReceiver
{
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
}

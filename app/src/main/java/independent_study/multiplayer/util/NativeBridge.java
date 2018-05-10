package independent_study.multiplayer.util;

public final class NativeBridge
{
    private static NativeBridge nativeBridge;

    // Used to load the 'native-lib' library on application startup.
    static
    {
        System.loadLibrary("native-lib");
    }

    private NativeBridge()
    {
        //Nothing to Functionally Do Here
    }

    public static NativeBridge getInstance()
    {
        if(nativeBridge == null)
            nativeBridge = new NativeBridge();
        return nativeBridge;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}

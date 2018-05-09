package independent_study.multiplayer.sms;

import android.telephony.SmsManager;

import java.util.Locale;

public class TransmitterSMS
{
    private static TransmitterSMS transmitterSMS;
    private SmsManager smsManager;

    private TransmitterSMS()
    {
        smsManager = SmsManager.getDefault();
    }

    public static TransmitterSMS getInstance()
    {
        if(transmitterSMS  == null)
            transmitterSMS = new TransmitterSMS();
        return transmitterSMS;
    }

    public boolean sendSMS(long phoneNumber, String message)
    {
        if(phoneNumber < 0)
            return false;
        else if(phoneNumber > 99999999999L)
            return false;

        String phoneNumberText = String.format(Locale.US, "%010d", phoneNumber);
        smsManager.sendTextMessage(phoneNumberText, null, message, null, null);
        return true;
    }
}

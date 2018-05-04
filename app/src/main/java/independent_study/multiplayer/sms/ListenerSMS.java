package independent_study.multiplayer.sms;

import android.telephony.SmsMessage;

public interface ListenerSMS
{
    void onSMSReceived(SmsMessage message);
}

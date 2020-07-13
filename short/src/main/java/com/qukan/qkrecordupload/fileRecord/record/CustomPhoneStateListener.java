package com.qukan.qkrecordupload.fileRecord.record;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

class CustomPhoneStateListener extends PhoneStateListener {

   private Context mContext;

   public CustomPhoneStateListener(Context context) {
       mContext = context;
   }

   @Override
   public void onServiceStateChanged(ServiceState serviceState) {
       super.onServiceStateChanged(serviceState);
   }

   @Override
   public void onCallStateChanged(int state, String incomingNumber) {

       switch (state) {
           case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
               break;
           case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
               break;
           case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电，去电接通  但是没法区分
               break;
       }
   }
}

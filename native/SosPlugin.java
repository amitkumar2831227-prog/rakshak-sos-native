package com.rakshak.sos;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PermissionState;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.util.ArrayList;

@CapacitorPlugin(
    name = "SosNative",
    permissions = {
        @Permission(strings = { Manifest.permission.SEND_SMS }, alias = "sms"),
        @Permission(strings = { Manifest.permission.CALL_PHONE }, alias = "call")
    }
)
public class SosPlugin extends Plugin {

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (getPermissionState("sms") != PermissionState.GRANTED
                || getPermissionState("call") != PermissionState.GRANTED) {
            requestAllPermissions(call, "permsCallback");
        } else {
            permsCallback(call);
        }
    }

    @PermissionCallback
    private void permsCallback(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("sms", getPermissionState("sms").toString());
        ret.put("call", getPermissionState("call").toString());
        call.resolve(ret);
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("sms", getPermissionState("sms").toString());
        ret.put("call", getPermissionState("call").toString());
        call.resolve(ret);
    }

    @PluginMethod
    public void sendSms(PluginCall call) {
        String number = call.getString("number");
        String message = call.getString("message");

        if (number == null || message == null) {
            call.reject("number and message are required");
            return;
        }
        if (getPermissionState("sms") != PermissionState.GRANTED) {
            call.reject("SMS permission not granted");
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(number, null, parts, null, null);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to send SMS: " + e.getMessage());
        }
    }

    @PluginMethod
    public void makeCall(PluginCall call) {
        String number = call.getString("number");
        if (number == null) {
            call.reject("number is required");
            return;
        }
        if (getPermissionState("call") != PermissionState.GRANTED) {
            call.reject("Call permission not granted");
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to place call: " + e.getMessage());
        }
    }
}

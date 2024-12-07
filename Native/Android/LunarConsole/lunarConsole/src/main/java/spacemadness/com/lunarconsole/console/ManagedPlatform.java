package spacemadness.com.lunarconsole.console;

import android.app.Activity;
import android.view.View;

import com.unity3d.player.UnityPlayer;

import java.lang.reflect.Field;
import java.util.Map;

import spacemadness.com.lunarconsole.debug.Log;

import static spacemadness.com.lunarconsole.debug.Tags.PLUGIN;

// https://github.com/SpaceMadness/lunar-unity-console/issues/218#issuecomment-2226078485

public class ManagedPlatform implements Platform {
    private final UnityScriptMessenger scriptMessenger;

    public ManagedPlatform(String target, String method) {
        scriptMessenger = new UnityScriptMessenger(target, method);
    }

    @Override
    public View getTouchRecipientView() {
        Activity activity = UnityPlayer.currentActivity;
        if (activity == null) {
            Log.e(PLUGIN, "UnityPlayer.currentActivity is null");
            return null;
        }

        UnityPlayer unityPlayer = null;

        try {
            Field unityPlayerField = activity.getClass().getDeclaredField("mUnityPlayer");
            unityPlayerField.setAccessible(true);
            unityPlayer = (UnityPlayer) unityPlayerField.get(activity);
        } catch (Exception e) {
            Log.e(PLUGIN, "Error while getting UnityPlayer instance: %s", e);
        }

        if (unityPlayer == null) {
            Log.e(PLUGIN, "UnityPlayer instance is null");
            return null;
        }

        return unityPlayer.getFrameLayout();
    }

    @Override
    public void sendUnityScriptMessage(String name, Map<String, Object> data) {
        try {
            scriptMessenger.sendMessage(name, data);
        } catch (Exception e) {
            Log.e(PLUGIN, "Error while sending Unity script message: name=%s param=%s", name, data);
        }
    }
}
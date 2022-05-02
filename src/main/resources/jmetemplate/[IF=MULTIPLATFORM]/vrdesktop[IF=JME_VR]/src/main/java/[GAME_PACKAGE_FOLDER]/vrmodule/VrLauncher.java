package [GAME_PACKAGE].vrmodule;

import [GAME_PACKAGE].game.[GAME_NAME];
import com.jme3.app.LostFocusBehavior;
import com.jme3.app.VRAppState;
import com.jme3.app.VRConstants;
import com.jme3.app.VREnvironment;
import com.jme3.system.AppSettings;
/**
 * Used to launch a jme application in desktop VR environment
 *
 */
public class VrLauncher {
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.put(VRConstants.SETTING_VRAPI, VRConstants.SETTING_VRAPI_OPENVR_LWJGL_VALUE);
        VREnvironment env = new VREnvironment(settings);
        env.initialize();
        if (env.isInitialized()){
            VRAppState vrAppState = new VRAppState(settings, env);

            [GAME_NAME] app = new [GAME_NAME](vrAppState);
            app.setLostFocusBehavior(LostFocusBehavior.Disabled);
            app.setSettings(settings);
            app.setShowSettings(false);
            app.start();
        }
    }
}
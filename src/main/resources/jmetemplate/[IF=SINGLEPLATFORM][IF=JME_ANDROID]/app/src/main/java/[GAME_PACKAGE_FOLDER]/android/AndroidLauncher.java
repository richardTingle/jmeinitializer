package [GAME_PACKAGE].android;

import [GAME_PACKAGE].game.[GAME_NAME];
import com.jme3.app.AndroidHarness;

public class AndroidLauncher extends AndroidHarness {

    public AndroidLauncher() {
        appClass = [GAME_NAME].class.getCanonicalName();
    }
}
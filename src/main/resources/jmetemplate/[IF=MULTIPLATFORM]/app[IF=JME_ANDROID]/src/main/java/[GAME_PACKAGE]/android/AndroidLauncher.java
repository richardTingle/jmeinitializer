package [GAME_PACKAGE].android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.jme3.app.LegacyApplication;
import com.jme3.app.jmeSurfaceView.JmeSurfaceView;
import com.jme3.app.jmeSurfaceView.OnExceptionThrown;
import com.jme3.app.jmeSurfaceView.OnRendererCompleted;
import myGame.Game;
import com.jme3.system.AppSettings;

public class AndroidLauncher extends AndroidHarness {

    public AndroidLauncher() {
        appClass = [GAME_NAME].class.getCanonicalName();
    }
}
package com.example.androidmodule;

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

/**
 * Used to create an Android Activity, the main entry for {@link android.view.Choreographer} to render Ui-Components.
 *
 * @author pavl_g.
 */
public class AndroidLauncher extends AppCompatActivity implements OnRendererCompleted, OnExceptionThrown {
    private JmeSurfaceView jmeSurfaceView;
    private static LegacyApplication legacyApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gl_startGame();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }

    protected void gl_startGame(){
        final JmeSurfaceView gl_surfaceView = findViewById(R.id.glView);
        final Game game;
        //pass jme game to the GLContext
        if(legacyApplication == null) {
            game = new Game();
            legacyApplication = new Game();
            legacyApplication = game;
            gl_surfaceView.setLegacyApplication(game);
        }else{
            gl_surfaceView.setLegacyApplication(legacyApplication);
        }
        //set listeners
        gl_surfaceView.setOnRendererCompleted(this);
        gl_surfaceView.setOnExceptionThrown(this);

        gl_surfaceView.startRenderer(0);

        //test android views (native android Ui -- managed by Choreographer)
        final View button = findViewById(R.id.button);
        //invoking the call from the android context aka from the Choreographer thread not gl thread  :-))
        button.setOnClickListener((view)-> Snackbar.make(view, "Android View OnClick invoked", BaseTransientBottomBar.LENGTH_SHORT).show());
        //shallow copying the surface view to be managed by the life cycle
        this.jmeSurfaceView = gl_surfaceView;
    }

    @Override
    public void onExceptionThrown(Throwable e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void onRenderCompletion(LegacyApplication application, AppSettings appSettings) {
        System.out.println("Rendering completed : " + application.getClass().getName());
    }

    /**
     * Fired when the screen has/hasNo touch/mouse focus.
     * @param hasFocus specify whether the current screen has focus or not
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /*get the view from the current activity*/
        final View decorView = AndroidLauncher.this.getWindow().getDecorView();
        /*hide navigation bar, apply fullscreen, hide status bar, immersive sticky to disable the system bars(nav & status) from showing up when user wipes the screen*/
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //keep the static pointer live and destroy the old game
        jmeSurfaceView = null;
//        jmeSurfaceView.getLegacyApplication().stop(!jmeSurfaceView.isGLThreadPaused());
    }

    @Override
    protected void onPause() {
        super.onPause();
        jmeSurfaceView.loseFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jmeSurfaceView.gainFocus();
    }
}
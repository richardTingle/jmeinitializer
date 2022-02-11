package [GAME_PACKAGE];

import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.app.VRAppState;
import com.jme3.app.VRConstants;
import com.jme3.app.VREnvironment;
import com.jme3.app.state.AppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.onemillionworlds.tamarin.compatibility.ActionBasedOpenVrState;
import com.onemillionworlds.tamarin.vrhands.BoundHand;
import com.onemillionworlds.tamarin.vrhands.HandSide;
import com.onemillionworlds.tamarin.vrhands.VRHandsAppState;

import java.io.File;

public class [GAME_NAME] extends SimpleApplication{

    BoundHand boundHandLeft;
    BoundHand boundHandRight;

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

    public [GAME_NAME](AppState... appStates) {
        super(appStates);
    }

    @Override
    public void simpleInitApp(){
        [IF=TAMARIN]
        ActionBasedOpenVrState actionBasedOpenVrState = new ActionBasedOpenVrState();
        getStateManager().attach(actionBasedOpenVrState);
        actionBasedOpenVrState.registerActionManifest(new File("openVr/actionManifest.json").getAbsolutePath(), "/actions/main" );

        initialiseHands();

        if(actionBasedOpenVrState.getAnalogActionState( "/actions/main/in/trigger").x>0.5f){
            System.out.println("trigger");
        }
        if(actionBasedOpenVrState.getDigitalActionState( "/actions/main/in/turnLeft").state){
            System.out.println("turn left");
        }
        //etc for other actions
        [/IF=TAMARIN]
    }

[IF=TAMARIN]
    @Override
    public void simpleUpdate(float tpf){
        super.simpleUpdate(tpf);

        VRAppState vrAppState = getStateManager().getState(VRAppState.class);
        vrAppState.getLeftViewPort().setBackgroundColor(ColorRGBA.Brown);
        vrAppState.getRightViewPort().setBackgroundColor(ColorRGBA.Brown);

    }

    private void initialiseHands(){
        VRHandsAppState vrHandsAppState = new VRHandsAppState(assetManager, getStateManager().getState(ActionBasedOpenVrState.class));
        getStateManager().attach(vrHandsAppState);

        Spatial handLeft =assetManager.loadModel("Tamarin/Models/basicHands_left.j3o");
        boundHandLeft = vrHandsAppState.bindHandModel("/actions/main/in/HandPoseLeft", "/actions/main/in/HandSkeletonLeft", handLeft, HandSide.LEFT);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Tamarin/Textures/basicHands_left_referenceTexture.png"));

        boundHandLeft.setMaterial(mat);
        boundHandLeft.setGrabAction("/actions/main/in/grip", rootNode);

        Spatial rightHand =assetManager.loadModel("Tamarin/Models/basicHands_right.j3o");

        boundHandRight= vrHandsAppState.bindHandModel("/actions/main/in/HandPoseRight", "/actions/main/in/HandSkeletonRight", rightHand, HandSide.RIGHT);
        boundHandRight.setGrabAction("/actions/main/in/grip", rootNode);

        Material matRight = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matRight.setTexture("ColorMap", assetManager.loadTexture("Tamarin/Textures/basicHands_right_referenceTexture.png"));
        boundHandRight.setMaterial(matRight);
    }
[/IF=TAMARIN]
}

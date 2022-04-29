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
[IF=TAMARIN]
import com.onemillionworlds.tamarin.compatibility.ActionBasedOpenVrState;
import com.onemillionworlds.tamarin.vrhands.BoundHand;
import com.onemillionworlds.tamarin.vrhands.HandSpec;
import com.onemillionworlds.tamarin.vrhands.VRHandsAppState;
[/IF=TAMARIN]

import java.io.File;

public class [GAME_NAME] extends SimpleApplication{

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

        getStateManager().attach(new VRHandsAppState(handSpec()));

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

    /**
     * The hand spec describes the openVr actions that are bound to the hand graphics, as well as a grab action.
     * The hand model could also be changed here but the tamarin default is being used here
     */
    private HandSpec handSpec(){
        return HandSpec.builder(
                "/actions/main/in/HandPoseLeft",
                "/actions/main/in/HandSkeletonLeft",
                "/actions/main/in/HandPoseRight",
                "/actions/main/in/HandSkeletonRight")
            .postBindLeft(leftHand -> {
                leftHand.setGrabAction("/actions/main/in/grip", rootNode);
            })
            .postBindRight(rightHand -> {
                rightHand.setGrabAction("/actions/main/in/grip", rootNode);
            }).build();
    }

[/IF=TAMARIN]
}

package minecrafttransportsimulator.systems;

import org.lwjgl.input.Mouse;

import minecrafttransportsimulator.ClientProxy;
import minecrafttransportsimulator.MTS;
import minecrafttransportsimulator.guis.GUIPanelAircraft;
import minecrafttransportsimulator.guis.GUIPanelGround;
import minecrafttransportsimulator.guis.instances.GUIRadio;
import minecrafttransportsimulator.jsondefs.CoreConfigObject.JoystickConfig;
import minecrafttransportsimulator.jsondefs.CoreConfigObject.KeyboardConfig;
import minecrafttransportsimulator.packets.control.AileronPacket;
import minecrafttransportsimulator.packets.control.BrakePacket;
import minecrafttransportsimulator.packets.control.ElevatorPacket;
import minecrafttransportsimulator.packets.control.FlapPacket;
import minecrafttransportsimulator.packets.control.HornPacket;
import minecrafttransportsimulator.packets.control.ReverseThrustPacket;
import minecrafttransportsimulator.packets.control.RudderPacket;
import minecrafttransportsimulator.packets.control.ShiftPacket;
import minecrafttransportsimulator.packets.control.SteeringPacket;
import minecrafttransportsimulator.packets.control.ThrottlePacket;
import minecrafttransportsimulator.packets.control.TrimPacket;
import minecrafttransportsimulator.packets.parts.PacketPartGunSignal;
import minecrafttransportsimulator.vehicles.main.EntityVehicleE_Powered;
import minecrafttransportsimulator.vehicles.main.EntityVehicleF_Air;
import minecrafttransportsimulator.vehicles.main.EntityVehicleF_Ground;
import minecrafttransportsimulator.vehicles.parts.APart;
import minecrafttransportsimulator.vehicles.parts.APartGun;
import minecrafttransportsimulator.vehicles.parts.PartSeat;
import minecrafttransportsimulator.wrappers.WrapperGUI;
import minecrafttransportsimulator.wrappers.WrapperInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**Class that handles all control operations.
 * Keybinding lists are initiated during the {@link ClientProxy} init method.
 * 
 * @author don_bruce
 */
public final class ControlSystem{	
	private static final int NULL_COMPONENT = 999;	
	
	public static void init(){
		//Get config and init controls if they don't exist, or load them if they do.
		
		/*
		//Recall keybindings from config file.
		for(ControlsKeyboard control : ControlsKeyboard.values()){
			//control.button = ConfigSystem.config.get(KEYBOARD_CONFIG, control.buttonName, control.defaultButton).getInt();
		}
		for(ControlsJoystick control : ControlsJoystick.values()){
			control.joystickAssigned = ConfigSystem.config.get(JOYSTICK_CONFIG, control.buttonName + "_joystick", "").getString();
			control.joystickButton = ConfigSystem.config.get(JOYSTICK_CONFIG, control.buttonName + "_button", NULL_COMPONENT).getInt();
			if(control.isAxis){
				control.joystickMinTravel = ConfigSystem.config.get(JOYSTICK_CONFIG, control.buttonName + "_mintravel", -1D).getDouble();
				control.joystickMaxTravel = ConfigSystem.config.get(JOYSTICK_CONFIG, control.buttonName + "_maxtravel", 1D).getDouble();
				control.joystickInverted = ConfigSystem.config.get(JOYSTICK_CONFIG, control.buttonName + "_inverted", false).getBoolean();
			}
		}
		ConfigSystem.config.save();*/
		
	}

	
	public static void controlVehicle(EntityVehicleE_Powered vehicle, boolean isPlayerController){
		if(vehicle instanceof EntityVehicleF_Air){
			controlAircraft((EntityVehicleF_Air) vehicle, isPlayerController);
		}else if(vehicle instanceof EntityVehicleF_Ground){
			controlGroundVehicle((EntityVehicleF_Ground) vehicle, isPlayerController);
		}
	}
	
	private static void controlCamera(ControlsKeyboardDynamic dynamic, ControlsKeyboard zoomIn, ControlsKeyboard zoomOut, ControlsJoystick changeView){
		if(dynamic.isPressed()){
			if(CameraSystem.hudMode == 3){
				CameraSystem.hudMode = 0;
			}else{
				++CameraSystem.hudMode;
			}
		}else if(dynamic.mainControl.isPressed()){
			CameraSystem.changeCameraLock();
		}
		
		if(zoomIn.isPressed()){
			CameraSystem.changeCameraZoom(false);
		}
		if(zoomOut.isPressed()){
			CameraSystem.changeCameraZoom(true);
		}
		
		if(changeView.isPressed()){
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2){
				Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
			}else{
				++Minecraft.getMinecraft().gameSettings.thirdPersonView;
			}
		}
	}
	
	private static void rotateCamera(ControlsJoystick lookR, ControlsJoystick lookL, ControlsJoystick lookU, ControlsJoystick lookD, ControlsJoystick lookA){
		if(lookR.isPressed()){
			Minecraft.getMinecraft().player.rotationYaw+=3;
		}
		if(lookL.isPressed()){
			Minecraft.getMinecraft().player.rotationYaw-=3;
		}
		if(lookU.isPressed()){
			Minecraft.getMinecraft().player.rotationPitch-=3;
		}
		if(lookD.isPressed()){
			Minecraft.getMinecraft().player.rotationPitch+=3;
		}
		
		float pollData = lookA.getMultistateValue();
		if(pollData != 0){
			if(pollData >= 0.125F && pollData <= 0.375F){
				Minecraft.getMinecraft().player.rotationPitch+=3;
			}
			if(pollData >= 0.375F && pollData <= 0.625F){
				Minecraft.getMinecraft().player.rotationYaw+=3;
			}
			if(pollData >= 0.625F && pollData <= 0.875F){
				Minecraft.getMinecraft().player.rotationPitch-=3;
			}
			if(pollData >= 0.875F || pollData <= 0.125F){
				Minecraft.getMinecraft().player.rotationYaw-=3;
			}
		}
	}
	
	private static void controlBrake(ControlsKeyboardDynamic dynamic, ControlsJoystick analogBrake, ControlsJoystick pBrake, int entityID){
		if(joystickMap.containsKey(analogBrake.joystickAssigned) && analogBrake.joystickButton != NULL_COMPONENT){
			if(pBrake.isPressed()){
				MTS.MTSNet.sendToServer(new BrakePacket(entityID, (byte) 12));
			}else if(getJoystickAxisState(analogBrake, (short) 0) > 25){
				MTS.MTSNet.sendToServer(new BrakePacket(entityID, (byte) 11));
			}else{
				MTS.MTSNet.sendToServer(new BrakePacket(entityID, (byte) 2));
			}
		}else{
			if(dynamic.isPressed() || pBrake.isPressed()){
				MTS.MTSNet.sendToServer(new BrakePacket(entityID, (byte) 12));
			}else if(dynamic.mainControl.isPressed()){
				MTS.MTSNet.sendToServer(new BrakePacket(entityID, (byte) 11));
			}else{
				MTS.MTSNet.sendToServer(new BrakePacket(entityID, (byte) 2));
			}
		}
	}
	
	private static void controlGun(EntityVehicleE_Powered vehicle, ControlsKeyboard gun){
		PartSeat seat = vehicle.getSeatForRider(Minecraft.getMinecraft().player);
		if(seat != null){
			//If we are seated, attempt to control guns.
			//Only control guns our seat is a part of, or guns with no seats part of them.
			//First check our parent part.
			if(seat.parentPart instanceof APartGun){
				MTS.MTSNet.sendToServer(new PacketPartGunSignal((APartGun) seat.parentPart, Minecraft.getMinecraft().player.getEntityId(), gun.isPressed()));
			}
			//Now check subParts of our seat.
			for(APart subPart : seat.childParts){
				if(subPart instanceof APartGun){
					MTS.MTSNet.sendToServer(new PacketPartGunSignal((APartGun) subPart, Minecraft.getMinecraft().player.getEntityId(), gun.isPressed()));
				}
			}
			//If we are the vehicle controller, check for guns that don't have seats. 
			if(seat.isController){
				for(APart part : vehicle.getVehicleParts()){
					if(part instanceof APartGun){
						if(!(part.parentPart instanceof PartSeat)){
							boolean hasControllingSeats = false;
							for(APart subPart : part.childParts){
								if(subPart instanceof PartSeat){
									hasControllingSeats = true;
								}
							}
							if(!hasControllingSeats){
								MTS.MTSNet.sendToServer(new PacketPartGunSignal((APartGun) part, Minecraft.getMinecraft().player.getEntityId(), gun.isPressed()));
							}
						}
					}
				}
			}
		}
	}
	
	private static void controlRadio(EntityVehicleE_Powered vehicle, ControlsKeyboard radio){
		if(radio.isPressed()){
			if(Minecraft.getMinecraft().currentScreen == null){
				FMLCommonHandler.instance().showGuiScreen(new WrapperGUI(new GUIRadio(vehicle)));
			}
		}
	}
	
	private static void controlAircraft(EntityVehicleF_Air aircraft, boolean isPlayerController){
		controlCamera(ControlsKeyboardDynamic.AIRCRAFT_CHANGEHUD, ControlsKeyboard.AIRCRAFT_ZOOM_I, ControlsKeyboard.AIRCRAFT_ZOOM_O, ControlsJoystick.AIRCRAFT_CHANGEVIEW);
		rotateCamera(ControlsJoystick.AIRCRAFT_LOOK_R, ControlsJoystick.AIRCRAFT_LOOK_L, ControlsJoystick.AIRCRAFT_LOOK_U, ControlsJoystick.AIRCRAFT_LOOK_D, ControlsJoystick.AIRCRAFT_LOOK_A);
		controlRadio(aircraft, ControlsKeyboard.AIRCRAFT_RADIO);
		controlGun(aircraft, ControlsKeyboard.AIRCRAFT_GUN);
		if(!isPlayerController){
			return;
		}
		controlBrake(ControlsKeyboardDynamic.AIRCRAFT_PARK, ControlsJoystick.AIRCRAFT_BRAKE_ANALOG, ControlsJoystick.AIRCRAFT_PARK, aircraft.getEntityId());
		
		//Open or close the panel.
		if(ControlsKeyboard.AIRCRAFT_PANEL.isPressed()){
			if(Minecraft.getMinecraft().currentScreen == null){
				FMLCommonHandler.instance().showGuiScreen(new GUIPanelAircraft(aircraft));
			}else if(Minecraft.getMinecraft().currentScreen instanceof GUIPanelAircraft){
				Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
				Minecraft.getMinecraft().setIngameFocus();
			}
		}
		
		//Check for thrust reverse button.
		if(ControlsJoystick.AIRCRAFT_REVERSE.isPressed()){
			MTS.proxy.playSound(aircraft.getPositionVector(), MTS.MODID + ":panel_buzzer", 1.0F, 1.0F);
			MTS.MTSNet.sendToServer(new ReverseThrustPacket(aircraft.getEntityId(), !aircraft.reverseThrust));
		}
		
		//Increment or decrement throttle.
		if(joystickMap.containsKey(ControlsJoystick.AIRCRAFT_THROTTLE.joystickAssigned) && ControlsJoystick.AIRCRAFT_THROTTLE.joystickButton != NULL_COMPONENT){
			MTS.MTSNet.sendToServer(new ThrottlePacket(aircraft.getEntityId(), (byte) getJoystickAxisState(ControlsJoystick.AIRCRAFT_THROTTLE, (short) 0)));
		}else{
			if(ControlsKeyboard.AIRCRAFT_THROTTLE_U.isPressed()){
				MTS.MTSNet.sendToServer(new ThrottlePacket(aircraft.getEntityId(), Byte.MAX_VALUE));
			}
			if(ControlsKeyboard.AIRCRAFT_THROTTLE_D.isPressed()){
				MTS.MTSNet.sendToServer(new ThrottlePacket(aircraft.getEntityId(), Byte.MIN_VALUE));
			}
		}		
		
		//Check flaps.
		if(aircraft.pack.plane != null && aircraft.pack.plane.hasFlaps){
			if(ControlsKeyboard.AIRCRAFT_FLAPS_U.isPressed()){
				MTS.MTSNet.sendToServer(new FlapPacket(aircraft.getEntityId(), (byte) -50));
			}
			if(ControlsKeyboard.AIRCRAFT_FLAPS_D.isPressed()){
				MTS.MTSNet.sendToServer(new FlapPacket(aircraft.getEntityId(), (byte) 50));
			}
		}
		
		//Check yaw.
		if(joystickMap.containsKey(ControlsJoystick.AIRCRAFT_YAW.joystickAssigned) && ControlsJoystick.AIRCRAFT_YAW.joystickButton != NULL_COMPONENT){
			MTS.MTSNet.sendToServer(new RudderPacket(aircraft.getEntityId(), getJoystickAxisState(ControlsJoystick.AIRCRAFT_YAW, (short) 250)));
		}else{
			if(ControlsKeyboard.AIRCRAFT_YAW_R.isPressed()){
				MTS.MTSNet.sendToServer(new RudderPacket(aircraft.getEntityId(), true, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
			}
			if(ControlsKeyboard.AIRCRAFT_YAW_L.isPressed()){
				MTS.MTSNet.sendToServer(new RudderPacket(aircraft.getEntityId(), false, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
			}
		}
		if(ControlsJoystick.AIRCRAFT_TRIM_YAW_R.isPressed()){
			MTS.MTSNet.sendToServer(new TrimPacket(aircraft.getEntityId(), (byte) 10));
		}
		if(ControlsJoystick.AIRCRAFT_TRIM_YAW_L.isPressed()){
			MTS.MTSNet.sendToServer(new TrimPacket(aircraft.getEntityId(), (byte) 2));
		}
		
		//Check is mouse yoke is enabled.  If so do controls by mouse rather than buttons.
		if(ConfigSystem.configObject.client.mouseYoke.value){
			if(CameraSystem.lockedView && Minecraft.getMinecraft().currentScreen == null){
				//TODO add in mouse-yoke controls.  Perhaps make them better?
				//MTS.MTSNet.sendToServer(new AileronPacket(aircraft.getEntityId(), mousePosX));
				//MTS.MTSNet.sendToServer(new ElevatorPacket(aircraft.getEntityId(), mousePosY));
			}
		}else{
			//Check pitch.
			if(joystickMap.containsKey(ControlsJoystick.AIRCRAFT_PITCH.joystickAssigned) && ControlsJoystick.AIRCRAFT_PITCH.joystickButton != NULL_COMPONENT){
				MTS.MTSNet.sendToServer(new ElevatorPacket(aircraft.getEntityId(), getJoystickAxisState(ControlsJoystick.AIRCRAFT_PITCH, (short) 250)));
			}else{
				if(ControlsKeyboard.AIRCRAFT_PITCH_U.isPressed()){
					MTS.MTSNet.sendToServer(new ElevatorPacket(aircraft.getEntityId(), true, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
				}
				if(ControlsKeyboard.AIRCRAFT_PITCH_D.isPressed()){
					MTS.MTSNet.sendToServer(new ElevatorPacket(aircraft.getEntityId(), false, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
				}
			}
			if(ControlsJoystick.AIRCRAFT_TRIM_PITCH_U.isPressed()){
				MTS.MTSNet.sendToServer(new TrimPacket(aircraft.getEntityId(), (byte) 9));
			}
			if(ControlsJoystick.AIRCRAFT_TRIM_PITCH_D.isPressed()){
				MTS.MTSNet.sendToServer(new TrimPacket(aircraft.getEntityId(), (byte) 1));
			}
			
			//Check roll.
			if(joystickMap.containsKey(ControlsJoystick.AIRCRAFT_ROLL.joystickAssigned) && ControlsJoystick.AIRCRAFT_ROLL.joystickButton != NULL_COMPONENT){
				MTS.MTSNet.sendToServer(new AileronPacket(aircraft.getEntityId(), getJoystickAxisState(ControlsJoystick.AIRCRAFT_ROLL, (short) 250)));
			}else{
				if(ControlsKeyboard.AIRCRAFT_ROLL_R.isPressed()){
					MTS.MTSNet.sendToServer(new AileronPacket(aircraft.getEntityId(), true, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
				}
				if(ControlsKeyboard.AIRCRAFT_ROLL_L.isPressed()){
					MTS.MTSNet.sendToServer(new AileronPacket(aircraft.getEntityId(), false, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
				}
			}
			if(ControlsJoystick.AIRCRAFT_TRIM_ROLL_R.isPressed()){
				MTS.MTSNet.sendToServer(new TrimPacket(aircraft.getEntityId(), (byte) 8));
			}
			if(ControlsJoystick.AIRCRAFT_TRIM_ROLL_L.isPressed()){
				MTS.MTSNet.sendToServer(new TrimPacket(aircraft.getEntityId(), (byte) 0));
			}
		}
	}
	
	private static void controlGroundVehicle(EntityVehicleF_Ground powered, boolean isPlayerController){
		controlCamera(ControlsKeyboardDynamic.CAR_CHANGEHUD, ControlsKeyboard.CAR_ZOOM_I, ControlsKeyboard.CAR_ZOOM_O, ControlsJoystick.CAR_CHANGEVIEW);
		rotateCamera(ControlsJoystick.CAR_LOOK_R, ControlsJoystick.CAR_LOOK_L, ControlsJoystick.CAR_LOOK_U, ControlsJoystick.CAR_LOOK_D, ControlsJoystick.CAR_LOOK_A);
		controlRadio(powered, ControlsKeyboard.CAR_RADIO);
		controlGun(powered, ControlsKeyboard.CAR_GUN);
		if(!isPlayerController){
			return;
		}
		controlBrake(ControlsKeyboardDynamic.CAR_PARK, ControlsJoystick.CAR_BRAKE_ANALOG, ControlsJoystick.CAR_PARK, powered.getEntityId());
		
		//Open or close the panel.
		if(ControlsKeyboard.CAR_PANEL.isPressed()){
			if(Minecraft.getMinecraft().currentScreen == null){
				FMLCommonHandler.instance().showGuiScreen(new GUIPanelGround(powered));
			}else if(Minecraft.getMinecraft().currentScreen instanceof GUIPanelGround){
				Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
				Minecraft.getMinecraft().setIngameFocus();
			}
		}
		
		//Change gas to on or off.
		if(joystickMap.containsKey(ControlsJoystick.CAR_GAS.joystickAssigned) && ControlsJoystick.CAR_GAS.joystickButton != NULL_COMPONENT){
			MTS.MTSNet.sendToServer(new ThrottlePacket(powered.getEntityId(), (byte) getJoystickAxisState(ControlsJoystick.CAR_GAS, (short) 0)));
		}else{
			if(ControlsKeyboardDynamic.CAR_SLOW.isPressed()){
				MTS.MTSNet.sendToServer(new ThrottlePacket(powered.getEntityId(), (byte) 50));
			}else if(ControlsKeyboard.CAR_GAS.isPressed()){
				MTS.MTSNet.sendToServer(new ThrottlePacket(powered.getEntityId(), (byte) 100));
			}else{
				MTS.MTSNet.sendToServer(new ThrottlePacket(powered.getEntityId(), (byte) 0));
			}
		}
		
		//Check steering, turn signals, and lights.
		//Check is mouse yoke is enabled.  If so do controls by mouse rather than buttons.
		if(ConfigSystem.configObject.client.mouseYoke.value){
			if(CameraSystem.lockedView && Minecraft.getMinecraft().currentScreen == null){
				int dx = Mouse.getDX();
				if(Math.abs(dx) < 100){
					mousePosX = (short) Math.max(Math.min(mousePosX + dx*10, 450), -450);
				}
				MTS.MTSNet.sendToServer(new SteeringPacket(powered.getEntityId(), mousePosX));
			}
		}else{
			if(joystickMap.containsKey(ControlsJoystick.CAR_TURN.joystickAssigned) && ControlsJoystick.CAR_TURN.joystickButton != NULL_COMPONENT){
				MTS.MTSNet.sendToServer(new SteeringPacket(powered.getEntityId(), getJoystickAxisState(ControlsJoystick.CAR_TURN, (short) 450)));
			}else{
				boolean turningRight = ControlsKeyboard.CAR_TURN_R.isPressed();
				boolean turningLeft = ControlsKeyboard.CAR_TURN_L.isPressed();
				long currentTime = powered.world.getTotalWorldTime();
				if(turningRight && !turningLeft){
					MTS.MTSNet.sendToServer(new SteeringPacket(powered.getEntityId(), true, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
				}else if(turningLeft && !turningRight){
					MTS.MTSNet.sendToServer(new SteeringPacket(powered.getEntityId(), false, ConfigSystem.configObject.client.controlSurfaceCooldown.value.shortValue()));
				}
			}
		}
		
		//Check if we are shifting.
		if(ControlsKeyboard.CAR_SHIFT_U.isPressed()){
			MTS.MTSNet.sendToServer(new ShiftPacket(powered.getEntityId(), true));
		}
		if(ControlsKeyboard.CAR_SHIFT_D.isPressed()){
			MTS.MTSNet.sendToServer(new ShiftPacket(powered.getEntityId(), false));
		}
		
		//Check if horn button is pressed.
		if(ControlsKeyboard.CAR_HORN.isPressed()){
			MTS.MTSNet.sendToServer(new HornPacket(powered.getEntityId(), true));
		}else{
			MTS.MTSNet.sendToServer(new HornPacket(powered.getEntityId(), false));
		}
	}

	/**List of enums representing all controls present.  Add new controls by adding their enum values here, and
	 * setting their default values in {@link WrapperInput#getDefaultKeyCode(ControlsKeyboard)}.  This split is done
	 * to keep the keyboard-specific code out of this class.
	 *
	 * @author don_bruce
	 */
	public enum ControlsKeyboard{
		AIRCRAFT_MOD(ControlsJoystick.AIRCRAFT_MOD, false),
		AIRCRAFT_CAMLOCK(ControlsJoystick.AIRCRAFT_CAMLOCK, true),
		AIRCRAFT_YAW_R(ControlsJoystick.AIRCRAFT_YAW, false),
		AIRCRAFT_YAW_L(ControlsJoystick.AIRCRAFT_YAW, false),
		AIRCRAFT_PITCH_U(ControlsJoystick.AIRCRAFT_PITCH, false),
		AIRCRAFT_PITCH_D(ControlsJoystick.AIRCRAFT_PITCH, false),
		AIRCRAFT_ROLL_R( ControlsJoystick.AIRCRAFT_ROLL, false),
		AIRCRAFT_ROLL_L(ControlsJoystick.AIRCRAFT_ROLL, false),
		AIRCRAFT_THROTTLE_U(ControlsJoystick.AIRCRAFT_THROTTLE, false),
		AIRCRAFT_THROTTLE_D(ControlsJoystick.AIRCRAFT_THROTTLE, false),
		AIRCRAFT_FLAPS_U(ControlsJoystick.AIRCRAFT_FLAPS_U, true),
		AIRCRAFT_FLAPS_D(ControlsJoystick.AIRCRAFT_FLAPS_D, true),
		AIRCRAFT_BRAKE(ControlsJoystick.AIRCRAFT_BRAKE, false),
		AIRCRAFT_PANEL(ControlsJoystick.AIRCRAFT_PANEL, true),
		AIRCRAFT_RADIO(ControlsJoystick.AIRCRAFT_RADIO, false),
		AIRCRAFT_GUN(ControlsJoystick.AIRCRAFT_GUN, false),
		AIRCRAFT_ZOOM_I(ControlsJoystick.AIRCRAFT_ZOOM_I, true),
		AIRCRAFT_ZOOM_O(ControlsJoystick.AIRCRAFT_ZOOM_O, true),
		
		CAR_MOD(ControlsJoystick.CAR_MOD, false),
		CAR_CAMLOCK(ControlsJoystick.CAR_CAMLOCK, true),
		CAR_TURN_R(ControlsJoystick.CAR_TURN, false),
		CAR_TURN_L(ControlsJoystick.CAR_TURN, false),
		CAR_GAS(ControlsJoystick.CAR_GAS, false),
		CAR_BRAKE(ControlsJoystick.CAR_BRAKE, false),
		CAR_PANEL(ControlsJoystick.CAR_PANEL, true),
		CAR_SHIFT_U(ControlsJoystick.CAR_SHIFT_U, true),
		CAR_SHIFT_D(ControlsJoystick.CAR_SHIFT_D, true),
		CAR_HORN(ControlsJoystick.CAR_HORN, false),
		CAR_RADIO(ControlsJoystick.CAR_RADIO, true),
		CAR_GUN(ControlsJoystick.CAR_GUN, false),
		CAR_ZOOM_I(ControlsJoystick.CAR_ZOOM_I, true),
		CAR_ZOOM_O(ControlsJoystick.CAR_ZOOM_O, true);
		
		
		public final boolean isMomentary;
		public final String systemName;
		public final String translatedName;
		public final KeyboardConfig config;
		private final ControlsJoystick linkedJoystick;
		
		private boolean wasPressedLastCall;
		
		private ControlsKeyboard(ControlsJoystick linkedJoystick, boolean isMomentary){
			this.linkedJoystick = linkedJoystick;
			this.isMomentary = isMomentary;
			this.systemName = this.name().toLowerCase().replaceFirst("_", ".");
			this.translatedName = WrapperGUI.translate("input." + systemName);
			if(ConfigSystem.configObject.controls.keyboard.containsKey(systemName)){
				this.config = ConfigSystem.configObject.controls.keyboard.get(systemName);
			}else{
				this.config = new KeyboardConfig();
				this.config.keyCode = WrapperInput.getDefaultKeyCode(this);
			}
		}
		
		/**
		 *  Returns true if the given key is currently pressed.  If we
		 *  have a joystick in-use, and keyboard override is enabled, 
		 *  and the corresponding joystick input is bound, we
		 *  need to return false here no matter our actual state.
		 */
		public boolean isPressed(){
			if(ConfigSystem.configObject.client.keyboardOverride.value && linkedJoystick.config.joystickName != null){
				return false;
			}else{
				if(isMomentary){
					if(wasPressedLastCall){
						wasPressedLastCall = WrapperInput.isKeyPressed(config.keyCode); 
						return false;
					}else{
						wasPressedLastCall = WrapperInput.isKeyPressed(config.keyCode);
						return wasPressedLastCall;
					}
				}else{
					return WrapperInput.isKeyPressed(config.keyCode);
				}
			}
		}
		
		/*
		public boolean isPressed(){
			//Check to see if there is a working joystick assigned to this control.
			if(joystickMap.containsKey(this.linkedJoystickControl.joystickAssigned)){
				//Need to check to see if this axis is currently bound.
				if(this.linkedJoystickControl.joystickButton != NULL_COMPONENT){
					//Joystick control is bound and presumably functional.  If we are overriding the keyboard we must return this value.
					//Check to make sure this isn't mapped to an axis first.  If so, don't check for joystick values.
					boolean pressed = false;
					if(!this.linkedJoystickControl.isAxis){
						pressed = this.linkedJoystickControl.isPressed();
					}
					if(pressed || ConfigSystem.configObject.client.keyboardOverride.value){
						return pressed;
					}
				}
			}
			return this.isMomentary ? getTrueButtonState(pressedKeyboardButtons, this, Keyboard.isKeyDown(this.button)) : Keyboard.isKeyDown(this.button);
		}*/
	}
	
	public enum ControlsJoystick{
		AIRCRAFT_MOD(false, false),
		AIRCRAFT_CAMLOCK(false, true),
		AIRCRAFT_YAW(true, false),
		AIRCRAFT_PITCH(true, false),
		AIRCRAFT_ROLL(true, false),
		AIRCRAFT_THROTTLE(true, false),
		AIRCRAFT_FLAPS_U(false, true),
		AIRCRAFT_FLAPS_D(false, true),
		AIRCRAFT_BRAKE(false, false),
		AIRCRAFT_BRAKE_ANALOG(true, false),
		AIRCRAFT_PANEL(false, true),
		AIRCRAFT_PARK(false, true),
		AIRCRAFT_RADIO(false, true),
		AIRCRAFT_GUN(false, false),
		AIRCRAFT_ZOOM_I(false, true),
		AIRCRAFT_ZOOM_O(false, true),
		AIRCRAFT_CHANGEVIEW(false, true),
		AIRCRAFT_LOOK_L(false, false),
		AIRCRAFT_LOOK_R(false, false),
		AIRCRAFT_LOOK_U(false, false),
		AIRCRAFT_LOOK_D(false, false),
		AIRCRAFT_LOOK_A(false, false),
		AIRCRAFT_TRIM_YAW_R(false, true),
		AIRCRAFT_TRIM_YAW_L(false, true),
		AIRCRAFT_TRIM_PITCH_U(false, true),
		AIRCRAFT_TRIM_PITCH_D(false, true),
		AIRCRAFT_TRIM_ROLL_R(false, true),
		AIRCRAFT_TRIM_ROLL_L(false, true),
		AIRCRAFT_REVERSE(false, true),
		
		
		CAR_MOD(false, false),
		CAR_CAMLOCK(false, true),
		CAR_TURN(true, false),
		CAR_GAS(true, false),
		CAR_BRAKE(false, false),
		CAR_BRAKE_ANALOG(true, false),
		CAR_PANEL(false, true),
		CAR_SHIFT_U(false, true),
		CAR_SHIFT_D(false, true),
		CAR_HORN(false, false),
		CAR_PARK(false, true),
		CAR_RADIO(false, false),
		CAR_GUN(false, false),
		CAR_ZOOM_I(false, true),
		CAR_ZOOM_O(false, true),
		CAR_CHANGEVIEW(false, true),
		CAR_LOOK_L(false, false),
		CAR_LOOK_R(false, false),
		CAR_LOOK_U(false, false),
		CAR_LOOK_D(false, false),
		CAR_LOOK_A(false, false);
		
		
		public final boolean isAxis;
		public final boolean isMomentary;
		public final String systemName;
		public final String translatedName;
		public final JoystickConfig config;
		
		private boolean wasPressedLastCall;
		
		private ControlsJoystick(boolean isAxis, boolean isMomentary){
			this.isAxis=isAxis;
			this.isMomentary=isMomentary;
			this.systemName = this.name().toLowerCase().replaceFirst("_", ".");
			this.translatedName = WrapperGUI.translate("input." + systemName);
			if(ConfigSystem.configObject.controls.joystick.containsKey(systemName)){
				this.config = ConfigSystem.configObject.controls.joystick.get(systemName);
			}else{
				this.config = new JoystickConfig();
			}
		}
		
		public boolean isPressed(){
			if(isMomentary){
				if(wasPressedLastCall){
					wasPressedLastCall = WrapperInput.getJoystickValue(config.joystickName, config.buttonIndex) > 0; 
					return false;
				}else{
					wasPressedLastCall = WrapperInput.getJoystickValue(config.joystickName, config.buttonIndex) > 0;
					return wasPressedLastCall;
				}
			}else{
				return WrapperInput.getJoystickValue(config.joystickName, config.buttonIndex) > 0;
			}
		}
		
		private float getMultistateValue(){
			return WrapperInput.getJoystickValue(config.joystickName, config.buttonIndex);
		}
		
		//Return type is short to allow for easier packet transmission.
		private short getAxisState(short pollBounds){
			float pollValue = getMultistateValue();
			if(Math.abs(pollValue) > ConfigSystem.configObject.client.joystickDeadZone.value || pollBounds == 0){
				//Clamp the poll value to the defined axis bounds set during config to prevent over and under-runs.
				pollValue = (float) Math.max(config.axisMinTravel, pollValue);
				pollValue = (float) Math.min(config.axisMaxTravel, pollValue);				
				
				//If we don't need to normalize the axis, return it as-is.  Otherwise do a normalization from 0-1.
				if(pollBounds != 0){
					return (short) (config.invertedAxis ? (-pollBounds*pollValue) : (pollBounds*pollValue));
				}else{
					//Divide the poll value plus the min bounds by the span to get it in the range of 0-1.
					pollValue = (float) ((pollValue - config.axisMinTravel)/(config.axisMinTravel - config.axisMinTravel));
					
					//If axis is inverted, invert poll.
					if(config.invertedAxis){
						pollValue = 1 - pollValue;
					}
					
					//Now return this value in a range from 0-100.
					return (short) (pollValue*100);
				}
			}else{
				return 0;
			}
		}
		
		public void setControl(String joystickName, int buttonIndex){
			config.joystickName = joystickName;
			config.buttonIndex = buttonIndex;
			ConfigSystem.saveToDisk();
		}
		
		public void setAxisControl(String joystickName, int buttonIndex, double axisMinTravel, double axisMaxTravel, boolean invertedAxis){
			setControl(joystickName, buttonIndex);
			config.axisMinTravel = axisMinTravel;
			config.axisMaxTravel = axisMaxTravel;
			config.invertedAxis = invertedAxis;
			ConfigSystem.saveToDisk();
		}
		
		public void clearControl(){
			setControl("", NULL_COMPONENT);
		}
	}
	
	public enum ControlsKeyboardDynamic{
		AIRCRAFT_CHANGEHUD(ControlsKeyboard.AIRCRAFT_CAMLOCK, ControlsKeyboard.AIRCRAFT_MOD),
		AIRCRAFT_PARK(ControlsKeyboard.AIRCRAFT_BRAKE, ControlsKeyboard.AIRCRAFT_MOD),
		
		CAR_CHANGEHUD(ControlsKeyboard.CAR_CAMLOCK, ControlsKeyboard.CAR_MOD),
		CAR_PARK(ControlsKeyboard.CAR_BRAKE, ControlsKeyboard.CAR_MOD),
		CAR_SLOW(ControlsKeyboard.CAR_GAS, ControlsKeyboard.CAR_MOD);		
		
		public final String translatedName;
		public final ControlsKeyboard mainControl;
		public final ControlsKeyboard modControl;
		
		private ControlsKeyboardDynamic(ControlsKeyboard mainControl, ControlsKeyboard modControl){
			this.translatedName = WrapperGUI.translate("input." + name().toLowerCase().replaceFirst("_", "."));
			this.mainControl = mainControl;
			this.modControl = modControl;
		}
		
		public boolean isPressed(){
			return this.modControl.isPressed() ? this.mainControl.isPressed() : false;
		}
	}
}

package minecraftflightsimulator.guis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import minecraftflightsimulator.systems.ConfigSystem;
import minecraftflightsimulator.systems.ControlSystem;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

public class GUIConfig extends GuiScreen{
	private static final ResourceLocation background = new ResourceLocation("mfs", "textures/guis/wide_blank.png");
	
	private final int xSize = 256;
	private final int ySize = 192;
	private final int offset = 17;
	
	private boolean changedThisTick;
	private int guiLeft;
	private int guiTop;
	private GUILevels guiLevel;
	private int scrollSpot = 0;
	private int joystickComponentId;
	private String controlName;
	private Controller[] joysticks;
	private Component[] joystickComponents;
	
	private GuiButton configButton;
	private GuiButton keyboardButton1;
	private GuiButton keyboardButton2;
	private GuiButton joystickButton;
	
	private GuiButton throttleKillsButton;
	private GuiButton seaLevelOffsetButton;
	private GuiButton electricStartButton;
	private GuiTextField joystickForceFactor;
	private GuiTextField controlSurfaceCooldown;
	private GuiTextField joystickDeadZone;
	
	private GuiButton upButton;
	private GuiButton downButton;
	private GuiButton confirmButton;
	private GuiButton cancelButton;
	private GuiButton clearButton;
	private GuiTextField maxTextBox;
	private GuiTextField minTextBox;
	
	private List<GuiButton> configureButtons = new ArrayList<GuiButton>();
	private List<GuiButton> joystickButtons = new ArrayList<GuiButton>();
	private List<GuiButton> joystickConfigureButtons = new ArrayList<GuiButton>();
	private List<GuiButton> analogAssignButtons = new ArrayList<GuiButton>();
	private List<GuiButton> digitalAssignButtons = new ArrayList<GuiButton>();
	private Map<String, GuiTextField> configureBoxes = new HashMap<String, GuiTextField>();
	private Map<String, GuiTextField> keyboard1Boxes = new HashMap<String, GuiTextField>();
	private Map<String, GuiTextField> keyboard2Boxes = new HashMap<String, GuiTextField>();
	
	public GUIConfig(){
		this.allowUserInput=true;
	}
	
	@Override 
	public void initGui(){
		guiLeft = (this.width - this.xSize)/2;
		guiTop = (this.height - this.ySize)/2;
		buttonList.add(configButton = new GuiButton(0, guiLeft + 10, guiTop - 20, 50, 20, "Config"));
		buttonList.add(keyboardButton1 = new GuiButton(0, guiLeft + 60, guiTop - 20, 70, 20, "Keyboard1"));
		buttonList.add(keyboardButton2 = new GuiButton(0, guiLeft + 130, guiTop - 20, 70, 20, "Keyboard2"));
		buttonList.add(joystickButton = new GuiButton(0, guiLeft + 200, guiTop - 20, 55, 20, "Joystick"));
		guiLevel = GUILevels.KEYBOARD1;
		initConfigControls();
		initKeyboard1Controls();
		initKeyboard2Controls();
		initJoysticks();
		initJoystickButtonList();
		initJoystickControls();
		setButtonStatesByLevel();
	}
	
	private void initConfigControls(){
		int line = 0;
		int xOffset = 140;
		buttonList.add(throttleKillsButton = new GuiButton(0, guiLeft+xOffset, guiTop+10+(line++)*20, 60, 20, String.valueOf(ConfigSystem.getBooleanConfig("ThrottleKills"))));
		buttonList.add(seaLevelOffsetButton = new GuiButton(0, guiLeft+xOffset, guiTop+10+(line++)*20, 60, 20, String.valueOf(ConfigSystem.getBooleanConfig("SeaLevelOffset"))));
		buttonList.add(electricStartButton = new GuiButton(0, guiLeft+xOffset, guiTop+10+(line++)*20, 60, 20, String.valueOf(ConfigSystem.getBooleanConfig("ElectricStart"))));
		configureButtons.add(throttleKillsButton);
		configureButtons.add(seaLevelOffsetButton);
		configureButtons.add(electricStartButton);
	}
	
	private void initKeyboard1Controls(){
		int line = 0;
		int xOffset = 80;
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.PITCH.keyboardIncrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.PITCH.keyboardDecrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.ROLL.keyboardIncrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.ROLL.keyboardDecrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.YAW.keyboardIncrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.YAW.keyboardDecrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.THROTTLE.keyboardIncrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.THROTTLE.keyboardDecrementName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.FLAPS_U.keyboardName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.FLAPS_D.keyboardName);
		
		line = 0;
		xOffset = 200;
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.BRAKE.keyboardName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.PANEL.keyboardName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.ZOOM_I.keyboardName);
		createKeyBox1At(guiLeft+xOffset, guiTop+10+(line++)*offset, ControlSystem.controls.ZOOM_O.keyboardName);
	}
	
	private void initKeyboard2Controls(){
		int line = 0;
		int xOffset = 80;
		keyboard2Boxes.put(ControlSystem.controls.CAM.keyboardName, new GuiTextField(fontRendererObj, guiLeft+xOffset, guiTop+10+(line++)*offset, 60, 15));
		keyboard2Boxes.put(ControlSystem.controls.MOD.keyboardName, new GuiTextField(fontRendererObj, guiLeft+xOffset, guiTop+10+(line++)*offset, 60, 15));
	}
	
	private void initJoysticks(){
		joysticks = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(int i=0;i<joysticks.length;i++){
			joystickButtons.add(new GuiButton(0, guiLeft + 5, guiTop + 40 + 15*i, 240, 15, ""));
			buttonList.add(joystickButtons.get(i));
			joystickButtons.get(i).enabled = false;
		}
	}
	
	private void initJoystickButtonList(){
		for(int i=0; i<9; ++i){
			joystickConfigureButtons.add(new GuiButton(0, guiLeft+5, guiTop+40+15*i, 215, 15, ""));
			buttonList.add(joystickConfigureButtons.get(i));
			joystickConfigureButtons.get(i).enabled = false;
		}
	}
	
	private void initJoystickControls(){
		buttonList.add(upButton = new GuiButton(0, guiLeft + 225, guiTop + 40, 20, 20, "/\\"));
		buttonList.add(downButton = new GuiButton(0, guiLeft + 225, guiTop + 155, 20, 20, "\\/"));
		buttonList.add(confirmButton = new GuiButton(0, guiLeft + 25, guiTop + 160, 100, 20, "Confirm"));
		buttonList.add(cancelButton = new GuiButton(0, guiLeft + 125, guiTop + 160, 100, 20, "Cancel"));
		buttonList.add(clearButton = new GuiButton(0, guiLeft + 25, guiTop + 160, 100, 20, "Clear Assignment"));
		
		createAssignmentButtonAt(guiLeft + 85, guiTop + 40, ControlSystem.controls.PITCH.joystickName, analogAssignButtons);
		createAssignmentButtonAt(guiLeft + 85, guiTop + 60, ControlSystem.controls.ROLL.joystickName, analogAssignButtons);
		createAssignmentButtonAt(guiLeft + 85, guiTop + 80, ControlSystem.controls.YAW.joystickName, analogAssignButtons);
		createAssignmentButtonAt(guiLeft + 85, guiTop + 100,ControlSystem.controls.THROTTLE.joystickName, analogAssignButtons);
		
		createAssignmentButtonAt(guiLeft + 5, guiTop + 30, ControlSystem.controls.FLAPS_U.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 5, guiTop + 50, ControlSystem.controls.FLAPS_D.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 5, guiTop + 70, ControlSystem.controls.BRAKE.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 5, guiTop + 90,ControlSystem.controls.PANEL.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 5, guiTop + 110,ControlSystem.controls.MOD.joystickName, digitalAssignButtons);

		createAssignmentButtonAt(guiLeft + 85, guiTop + 30,ControlSystem.controls.ZOOM_I.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 85, guiTop + 50,ControlSystem.controls.ZOOM_O.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 85, guiTop + 70,ControlSystem.controls.CAM.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 85, guiTop + 90,ControlSystem.controls.CHANGEVIEW.joystickName, digitalAssignButtons);
		
		createAssignmentButtonAt(guiLeft + 165, guiTop + 30,ControlSystem.controls.LOOK_L.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 165, guiTop + 50,ControlSystem.controls.LOOK_R.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 165, guiTop + 70,ControlSystem.controls.LOOK_U.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 165, guiTop + 90,ControlSystem.controls.LOOK_D.joystickName, digitalAssignButtons);
		createAssignmentButtonAt(guiLeft + 165, guiTop + 110,ControlSystem.controls.LOOK_ALL.joystickName, digitalAssignButtons);
		
		maxTextBox = new GuiTextField(fontRendererObj, guiLeft+40, guiTop+60, 160, 15);
		minTextBox = new GuiTextField(fontRendererObj, guiLeft+40, guiTop+90, 160, 15);
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks){
		changedThisTick = false;
		this.mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		configButton.drawButton(mc, mouseX, mouseY);
		keyboardButton1.drawButton(mc, mouseX, mouseY);
		keyboardButton2.drawButton(mc, mouseX, mouseY);
		joystickButton.drawButton(mc, mouseX, mouseY);
		if(guiLevel.equals(GUILevels.CONFIG)){
			drawConfigScreen(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.KEYBOARD1)){
			drawKeyboardScreen1(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.KEYBOARD2)){
			drawKeyboardScreen2(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.JS_SELECT)){
			drawJoystickSelectionScreen(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.JS_BUTTON)){
			drawJoystickButtonScreen(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.JS_DIGITAL)){
			drawJoystickDigitalScreen(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.JS_ANALOG)){
			drawJoystickAnalogScreen(mouseX, mouseY);
		}else if(guiLevel.equals(GUILevels.JS_CALIBRATION)){
			drawJoystickCalibrationScreen(mouseX, mouseY);
		}
	}
	
	private static boolean isPointInRegion(int minX, int maxX, int minY, int maxY, int mouseX, int mouseY){
		return mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY;
	}
	
	private void drawConfigScreen(int mouseX, int mouseY){
		int line = 0;
		fontRendererObj.drawStringWithShadow("Throttle Kills:", guiLeft+10, guiTop+15+(line++)*20, Color.WHITE.getRGB());
		fontRendererObj.drawStringWithShadow("Sea Level Offset:", guiLeft+10, guiTop+15+(line++)*20, Color.WHITE.getRGB());
		fontRendererObj.drawStringWithShadow("Electric Start:", guiLeft+10, guiTop+15+(line++)*20, Color.WHITE.getRGB());
		
		throttleKillsButton.drawButton(mc, mouseX, mouseY);
		seaLevelOffsetButton.drawButton(mc, mouseX, mouseY);
		electricStartButton.drawButton(mc, mouseX, mouseY);
		
		if(isPointInRegion(throttleKillsButton.xPosition, throttleKillsButton.xPosition + throttleKillsButton.width, throttleKillsButton.yPosition, throttleKillsButton.yPosition + throttleKillsButton.height, mouseX, mouseY)){
			drawHoveringText(Arrays.asList(new String[] {"Can setting a joystick throttle", "to zero kill the engine?"}), mouseX, mouseY, fontRendererObj);
		}else if(isPointInRegion(seaLevelOffsetButton.xPosition, seaLevelOffsetButton.xPosition + seaLevelOffsetButton.width, seaLevelOffsetButton.yPosition, seaLevelOffsetButton.yPosition + seaLevelOffsetButton.height, mouseX, mouseY)){
			drawHoveringText(Arrays.asList(new String[] {"Does altimeter display 0", "at Y=64 instead of Y=0?"}), mouseX, mouseY, fontRendererObj);
		}else if(isPointInRegion(electricStartButton.xPosition, electricStartButton.xPosition + electricStartButton.width, electricStartButton.yPosition, electricStartButton.yPosition + electricStartButton.height, mouseX, mouseY)){
			drawHoveringText(Arrays.asList(new String[] {"Enable electric starter?", "If disabled players must", "start engines by hand."}), mouseX, mouseY, fontRendererObj);
		}
		
	}
	
	private void drawKeyboardScreen1(int mouseX, int mouseY){
		int line = 0;
		for(Entry<String, GuiTextField> entry : keyboard1Boxes.entrySet()){
			entry.getValue().setText(ControlSystem.getKeyboardKeyname(entry.getKey()));
			fontRendererObj.drawStringWithShadow(entry.getKey().substring(0, entry.getKey().length() - 3) + ":", entry.getValue().xPosition - 70, entry.getValue().yPosition + 2, Color.WHITE.getRGB());
			if(entry.getValue().isFocused()){
				entry.getValue().setText("");
			}
			entry.getValue().drawTextBox();
		}
	}
	
	private void drawKeyboardScreen2(int mouseX, int mouseY){
		int line = 0;
		int xOffset = 10;
		for(Entry<String, GuiTextField> entry : keyboard2Boxes.entrySet()){
			entry.getValue().setText(ControlSystem.getKeyboardKeyname(entry.getKey()));
			fontRendererObj.drawStringWithShadow(entry.getKey().substring(0, entry.getKey().length() - 3) + ":", entry.getValue().xPosition - 70, entry.getValue().yPosition + 2, Color.WHITE.getRGB());
			if(entry.getValue().isFocused()){
				entry.getValue().setText("");
			}
			entry.getValue().drawTextBox();
			++line;
		}
		int line2 = line;
		fontRendererObj.drawStringWithShadow("RollTrimL:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.ROLL.keyboardDecrementName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		fontRendererObj.drawStringWithShadow("PitchTrimD:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.PITCH.keyboardDecrementName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		fontRendererObj.drawStringWithShadow("YawTrimL:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.YAW.keyboardDecrementName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		++line;
		fontRendererObj.drawStringWithShadow("ParkBrake:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.BRAKE.keyboardName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		fontRendererObj.drawStringWithShadow("HUDMode:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.CAM.keyboardName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		
		xOffset = 130;
		line = line2;
		fontRendererObj.drawStringWithShadow("RollTrimR:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.ROLL.keyboardIncrementName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		fontRendererObj.drawStringWithShadow("PitchTrimU:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.PITCH.keyboardIncrementName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
		fontRendererObj.drawStringWithShadow("YawTrimR:", guiLeft+xOffset, guiTop+15+(line)*offset, Color.WHITE.getRGB());
		fontRendererObj.drawString(ControlSystem.getKeyboardKeyname(ControlSystem.controls.MOD.keyboardName) + "+" +  ControlSystem.getKeyboardKeyname(ControlSystem.controls.YAW.keyboardIncrementName), guiLeft+xOffset+60, guiTop+15+(line++)*offset, Color.BLACK.getRGB());
	}
	
	private void drawJoystickSelectionScreen(int mouseX, int mouseY){
		fontRendererObj.drawString("Chose a joystick:", guiLeft+10, guiTop+10, Color.BLACK.getRGB());
		fontRendererObj.drawString("Name:", guiLeft+10, guiTop+25, Color.BLACK.getRGB());
		fontRendererObj.drawString("Type:", guiLeft+140, guiTop+25, Color.BLACK.getRGB());
		fontRendererObj.drawString("Rumble:", guiLeft+200, guiTop+25, Color.BLACK.getRGB());
		for(int i=0; i<joysticks.length; ++i){
			joystickButtons.get(i).drawButton(mc, mouseX, mouseY);
			fontRendererObj.drawString(joysticks[i].getName().substring(0, joysticks[i].getName().length() > 20 ? 20 : joysticks[i].getName().length()), guiLeft+10, guiTop+44+15*i, Color.WHITE.getRGB());
			fontRendererObj.drawString(joysticks[i].getType().toString(), guiLeft+140, guiTop+44+15*i, Color.WHITE.getRGB());
			fontRendererObj.drawString(joysticks[i].getRumblers().length > 0 ? "Yes" : "No", guiLeft+200, guiTop+44+15*i, Color.WHITE.getRGB());
		}
	}
	
	private void drawJoystickButtonScreen(int mouseX, int mouseY){
		upButton.drawButton(mc, mouseX, mouseY);
		downButton.drawButton(mc, mouseX, mouseY);
		fontRendererObj.drawString("Now we need to map buttons.", guiLeft+10, guiTop+10, Color.BLACK.getRGB());
		fontRendererObj.drawString("#", guiLeft+10, guiTop+25, Color.BLACK.getRGB());
		fontRendererObj.drawString("Name:", guiLeft+25, guiTop+25, Color.BLACK.getRGB());
		fontRendererObj.drawString("Analog:", guiLeft+90, guiTop+25, Color.BLACK.getRGB());
		fontRendererObj.drawString("Assigned to:", guiLeft+140, guiTop+25, Color.BLACK.getRGB());
		for(int i=0; i<9 && i<joystickComponents.length && i+scrollSpot<joystickComponents.length; ++i){
			joystickConfigureButtons.get(i).drawButton(mc, mouseX, mouseY);
			fontRendererObj.drawString(String.valueOf(i+scrollSpot+1), guiLeft+10, guiTop+44+15*i, Color.WHITE.getRGB());
			fontRendererObj.drawString(joystickComponents[i+scrollSpot].getName().substring(0, joystickComponents[i+scrollSpot].getName().length() > 15 ? 15 : joystickComponents[i+scrollSpot].getName().length()), guiLeft+25, guiTop+44+15*i, Color.WHITE.getRGB());
			fontRendererObj.drawString(joystickComponents[i+scrollSpot].isAnalog() ? "Yes" : "No", guiLeft+100, guiTop+44+15*i, Color.WHITE.getRGB());
			fontRendererObj.drawString(ControlSystem.getJoystickControlName(i+scrollSpot), guiLeft+140, guiTop+44+15*i, Color.WHITE.getRGB());
		}
	}
	
	private void drawJoystickDigitalScreen(int mouseX, int mouseY){
		fontRendererObj.drawString("Choose what gets mapped to this button.", guiLeft+10, guiTop+10, Color.BLACK.getRGB());
		fontRendererObj.drawString("This DIGITAL button can control:", guiLeft+10, guiTop+20, Color.BLACK.getRGB());
		for(GuiButton button : digitalAssignButtons){
			button.drawButton(mc, mouseX, mouseY);
		}
		cancelButton.drawButton(mc, mouseX, mouseY);
		clearButton.drawButton(mc, mouseX, mouseY);
	}
	
	private void drawJoystickAnalogScreen(int mouseX, int mouseY){
		fontRendererObj.drawString("Choose what gets mapped to this button.", guiLeft+10, guiTop+10, Color.BLACK.getRGB());
		fontRendererObj.drawString("This ANALOG button can control:", guiLeft+10, guiTop+20, Color.BLACK.getRGB());
		for(GuiButton button : analogAssignButtons){
			button.drawButton(mc, mouseX, mouseY);
		}
		cancelButton.drawButton(mc, mouseX, mouseY);
		clearButton.drawButton(mc, mouseX, mouseY);
	}
	
	private void drawJoystickCalibrationScreen(int mouseX, int mouseY){
		fontRendererObj.drawString("Move the axis until the numbers stop changing.", guiLeft+10, guiTop+10, Color.BLACK.getRGB());
		fontRendererObj.drawString("Then hit confirm to save settings.", guiLeft+10, guiTop+20, Color.BLACK.getRGB());
		ControlSystem.getJoystick().poll();
		if(joystickComponents[joystickComponentId].getPollData() > 0){
			maxTextBox.setText(String.valueOf(Math.max(Double.valueOf(maxTextBox.getText()), joystickComponents[joystickComponentId].getPollData())));
		}else{
			minTextBox.setText(String.valueOf(Math.min(Double.valueOf(minTextBox.getText()), joystickComponents[joystickComponentId].getPollData())));
		}
		maxTextBox.drawTextBox();
		minTextBox.drawTextBox();
		confirmButton.drawButton(mc, mouseX, mouseY);
		cancelButton.drawButton(mc, mouseX, mouseY);
	}
	
	@Override
    protected void actionPerformed(GuiButton buttonClicked){
		super.actionPerformed(buttonClicked);
		if(changedThisTick){
			return;
		}else if(buttonClicked.equals(configButton)){
			guiLevel = GUILevels.CONFIG;
		}else if(buttonClicked.equals(keyboardButton1)){
			guiLevel = GUILevels.KEYBOARD1;
		}else if(buttonClicked.equals(keyboardButton2)){
			guiLevel = GUILevels.KEYBOARD2;
		}else if(buttonClicked.equals(joystickButton)){
			guiLevel = GUILevels.JS_SELECT;
		}else if(buttonClicked.equals(throttleKillsButton)){
			ConfigSystem.setClientConfig("ThrottleKills", !Boolean.valueOf(throttleKillsButton.displayString));
			throttleKillsButton.displayString = String.valueOf(ConfigSystem.getBooleanConfig("ThrottleKills"));
		}else if(buttonClicked.equals(seaLevelOffsetButton)){
			ConfigSystem.setClientConfig("SeaLevelOffset", !Boolean.valueOf(seaLevelOffsetButton.displayString));
			seaLevelOffsetButton.displayString = String.valueOf(ConfigSystem.getBooleanConfig("SeaLevelOffset"));
		}else if(buttonClicked.equals(electricStartButton)){
			ConfigSystem.setClientConfig("ElectricStart", !Boolean.valueOf(electricStartButton.displayString));
			electricStartButton.displayString = String.valueOf(ConfigSystem.getBooleanConfig("ElectricStart"));
		}else if(joystickButtons.contains(buttonClicked)){
			guiLevel = GUILevels.JS_BUTTON;
			ControlSystem.setJoystick(joysticks[joystickButtons.indexOf(buttonClicked)]);
			joystickComponents = ControlSystem.getJoystick().getComponents();
		}else if(joystickConfigureButtons.contains(buttonClicked)){
			joystickComponentId = joystickConfigureButtons.indexOf(buttonClicked) + scrollSpot;
			guiLevel = joystickComponents[joystickComponentId].isAnalog() ? GUILevels.JS_ANALOG : GUILevels.JS_DIGITAL;
		}else if(digitalAssignButtons.contains(buttonClicked)){
			guiLevel = GUILevels.JS_BUTTON;
			ControlSystem.setJoystickControl(buttonClicked.displayString, joystickComponentId);
		}else if(analogAssignButtons.contains(buttonClicked)){
			guiLevel = GUILevels.JS_CALIBRATION;
			controlName = buttonClicked.displayString;
		}else if(buttonClicked.equals(clearButton)){
			if(guiLevel.equals(GUILevels.JS_ANALOG)){
				ControlSystem.setAxisBounds(ControlSystem.getJoystickControlName(joystickComponentId), -1, 1);
			}
			guiLevel = GUILevels.JS_BUTTON;
			ControlSystem.setJoystickControl(ControlSystem.getJoystickControlName(joystickComponentId), ControlSystem.getNullComponent());
		}else if(buttonClicked.equals(upButton)){
			scrollSpot = Math.max(scrollSpot - 9, 0);
		}else if(buttonClicked.equals(downButton)){
			scrollSpot = Math.min(scrollSpot + 9, joystickComponents.length - joystickComponents.length%9);
		}else if(buttonClicked.equals(confirmButton)){
			guiLevel = GUILevels.JS_BUTTON;
			ControlSystem.setAxisBounds(controlName, Double.valueOf(minTextBox.getText()), Double.valueOf(maxTextBox.getText()));
			ControlSystem.setJoystickControl(controlName, joystickComponentId);
		}else if(buttonClicked.equals(cancelButton)){
			guiLevel = GUILevels.JS_BUTTON;
		}
		setButtonStatesByLevel();
		changedThisTick = true;
	}

	private void setButtonStatesByLevel(){
		for(GuiButton button : configureButtons){
			button.enabled = guiLevel.equals(GUILevels.CONFIG);
		}
		for(GuiTextField box : keyboard1Boxes.values()){
			box.setEnabled(guiLevel.equals(GUILevels.KEYBOARD1));
		}
		for(GuiTextField box : keyboard2Boxes.values()){
			box.setEnabled(guiLevel.equals(GUILevels.KEYBOARD2));
		}
		for(GuiButton button : joystickButtons){
			button.enabled = guiLevel.equals(GUILevels.JS_SELECT);
		}
		for(GuiButton button : joystickConfigureButtons){
			button.enabled = guiLevel.equals(GUILevels.JS_BUTTON);
		}
		for(GuiButton button : digitalAssignButtons){
			button.enabled = guiLevel.equals(GUILevels.JS_DIGITAL);
		}
		for(GuiButton button : analogAssignButtons){
			button.enabled = guiLevel.equals(GUILevels.JS_ANALOG);
		}
		
		upButton.enabled = guiLevel.equals(GUILevels.JS_BUTTON);
		downButton.enabled = guiLevel.equals(GUILevels.JS_BUTTON);
		configButton.enabled = !guiLevel.equals(GUILevels.CONFIG);
		keyboardButton1.enabled = !guiLevel.equals(GUILevels.KEYBOARD1);
		keyboardButton2.enabled = !guiLevel.equals(GUILevels.KEYBOARD2);
		joystickButton.enabled = !guiLevel.equals(GUILevels.JS_SELECT);
		cancelButton.enabled = guiLevel.equals(GUILevels.JS_ANALOG) || guiLevel.equals(GUILevels.JS_DIGITAL) || guiLevel.equals(GUILevels.JS_CALIBRATION);
		clearButton.enabled = guiLevel.equals(GUILevels.JS_ANALOG) || guiLevel.equals(GUILevels.JS_DIGITAL);
		confirmButton.enabled = guiLevel.equals(GUILevels.JS_CALIBRATION);
		maxTextBox.setVisible(guiLevel.equals(GUILevels.JS_CALIBRATION));
		minTextBox.setVisible(guiLevel.equals(GUILevels.JS_CALIBRATION));
		maxTextBox.setText("0");
		minTextBox.setText("0");
	}
	
    @Override
    protected void mouseClicked(int x, int y, int button){
    	super.mouseClicked(x, y, button);
    	for(GuiTextField box : keyboard1Boxes.values()){
    		if(box.getVisible()){
    			box.mouseClicked(x, y, button);
    		}
    	}
    	for(GuiTextField box : keyboard2Boxes.values()){
    		if(box.getVisible()){
    			box.mouseClicked(x, y, button);
    		}
    	}
    }
	
    @Override
    protected void keyTyped(char key, int bytecode){
    	super.keyTyped(key, bytecode);
    	if(bytecode==1){
            return;
        }
    	for(Entry<String, GuiTextField> entry : keyboard1Boxes.entrySet()){
    		if(entry.getValue().isFocused()){
    			entry.getValue().setText(Keyboard.getKeyName(bytecode));
    			ControlSystem.setKeyboardKey(entry.getKey(), bytecode);
    			entry.getValue().setFocused(false);
    			return;
    		}
    	}
    	for(Entry<String, GuiTextField> entry : keyboard2Boxes.entrySet()){
    		if(entry.getValue().isFocused()){
    			entry.getValue().setText(Keyboard.getKeyName(bytecode));
    			ControlSystem.setKeyboardKey(entry.getKey(), bytecode);
    			entry.getValue().setFocused(false);
    			return;
    		}
    	}
    }
    
    private GuiButton createAssignmentButtonAt(int posX, int posY, String name, List<GuiButton> listToAddTo){
    	GuiButton button = new GuiButton(0, posX, posY, 80, 20, name);
    	buttonList.add(button);
    	listToAddTo.add(button);
    	button.enabled = false;
    	return button;
    }
    
	private GuiTextField createKeyBox1At(int posX, int posY, String keyname){
		GuiTextField box = new GuiTextField(fontRendererObj, posX, posY, 40, 15);
		keyboard1Boxes.put(keyname, box);
		return box;
	}
	
	private enum GUILevels{
		CONFIG,
		KEYBOARD1,
		KEYBOARD2,
		JS_SELECT,
		JS_BUTTON,
		JS_DIGITAL,
		JS_ANALOG,
		JS_CALIBRATION;
	}
}
package minecrafttransportsimulator.blocks.tileentities.instances;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import minecrafttransportsimulator.baseclasses.Point3d;
import minecrafttransportsimulator.baseclasses.Point3i;
import minecrafttransportsimulator.mcinterface.IWrapperNBT;
import minecrafttransportsimulator.mcinterface.IWrapperWorld;
import minecrafttransportsimulator.sound.IRadioProvider;
import minecrafttransportsimulator.sound.Radio;
import minecrafttransportsimulator.sound.SoundInstance;

/**Radio tile entity.  Contains saved radio data and is responsible
 * for handling all radio calls to the block.
 *
 * @author don_bruce
 */
public class TileEntityRadio extends TileEntityDecor implements IRadioProvider{
	//Internal radio variables.
	private final Radio radio;
	private final FloatBuffer soundPosition;
	private final Point3d soundVelocity = new Point3d(0D, 0D, 0D);
	
	public TileEntityRadio(IWrapperWorld world, Point3i position, IWrapperNBT data){
		super(world, position, data);
		this.soundPosition = ByteBuffer.allocateDirect(3*Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		soundPosition.put(position.x);
		soundPosition.put(position.y);
		soundPosition.put(position.z);
		soundPosition.flip();
		this.radio = new Radio(this, data);
	}
	
	@Override
	public void remove(){
		super.remove();
		radio.stop();
	}
	
	@Override
	public void save(IWrapperNBT data){
		super.save(data);
		radio.save(data);
	}

	@Override
	public void startSounds(){}

	@Override
	public void updateProviderSound(SoundInstance sound){}

	@Override
	public FloatBuffer getProviderPosition(){
		return soundPosition;
	}

	@Override
	public Point3d getProviderVelocity(){
		return soundVelocity;
	}

	@Override
	public IWrapperWorld getProviderWorld(){
		return world;
	}

	@Override
	public Radio getRadio(){
		return radio;
	}
}

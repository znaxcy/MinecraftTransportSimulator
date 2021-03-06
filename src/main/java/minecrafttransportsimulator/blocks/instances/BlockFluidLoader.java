package minecrafttransportsimulator.blocks.instances;

import minecrafttransportsimulator.baseclasses.Point3i;
import minecrafttransportsimulator.blocks.components.ABlockBaseDecor;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityFluidLoader;
import minecrafttransportsimulator.mcinterface.IWrapperNBT;
import minecrafttransportsimulator.mcinterface.IWrapperPlayer;
import minecrafttransportsimulator.mcinterface.IWrapperWorld;
import minecrafttransportsimulator.packets.instances.PacketPlayerChatMessage;

public class BlockFluidLoader extends ABlockBaseDecor<TileEntityFluidLoader>{
	
	public BlockFluidLoader(){
		super();
	}
	
	@Override
	public boolean onClicked(IWrapperWorld world, Point3i point, Axis axis, IWrapperPlayer player){
		//Only check right-clicks on the server.
		if(!world.isClient()){
			TileEntityFluidLoader loader = (TileEntityFluidLoader) world.getTileEntity(point);
			loader.unloadMode = !loader.unloadMode;
			player.sendPacket(new PacketPlayerChatMessage(loader.unloadMode ? "interact.loader.unload" : "interact.loader.load"));
		}
		return true;
	}
	
    @Override
	public TileEntityFluidLoader createTileEntity(IWrapperWorld world, Point3i position, IWrapperNBT data){
		return new TileEntityFluidLoader(world, position, data);
	}

	@Override
	public Class<TileEntityFluidLoader> getTileEntityClass(){
		return TileEntityFluidLoader.class;
	}
}

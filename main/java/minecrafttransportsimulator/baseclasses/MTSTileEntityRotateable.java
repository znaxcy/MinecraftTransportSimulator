package minecrafttransportsimulator.baseclasses;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
	
public abstract class MTSTileEntityRotateable extends BlockContainer{
	
    public MTSTileEntityRotateable(Material material){
		super(material);
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        float yaw = entity.rotationYaw;
        while(yaw < 0){
            yaw += 360;
        }
        ((MTSTileEntity) world.getTileEntity(pos)).rotation = Math.round(yaw%360/45) == 8 ? 0 : (byte) Math.round(yaw%360/45);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return getTileEntity();
	}	

    public abstract MTSTileEntity getTileEntity();
}
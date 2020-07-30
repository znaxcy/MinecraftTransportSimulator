package mcinterface;

import java.util.Iterator;

import javax.annotation.Nullable;

import minecrafttransportsimulator.MTS;
import minecrafttransportsimulator.baseclasses.BoundingBox;
import minecrafttransportsimulator.baseclasses.Damage;
import minecrafttransportsimulator.baseclasses.Point3d;
import minecrafttransportsimulator.baseclasses.VehicleAxisAlignedBB;
import minecrafttransportsimulator.packets.instances.PacketEntityCSHandshake;
import minecrafttransportsimulator.rendering.components.LightType;
import minecrafttransportsimulator.systems.ConfigSystem;
import minecrafttransportsimulator.vehicles.main.AEntityBase;
import minecrafttransportsimulator.vehicles.main.EntityVehicleF_Physics;
import minecrafttransportsimulator.vehicles.parts.APart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**Builder for a basic MC Entity class.  This builder allows us to create a new entity
 * class that we can control that doesn't have the wonky systems the MC entities have, such
 * as no roll axis, a single hitbox, and tons of immutable objects that get thrown away every update.
 * Constructor takes a class of {@link AEntityBase}} to construct, but NOT an instance.  This is because
 * we can't create our entity instance at the same time MC creates its instance as we might not yet have NBT
 * data.  Instead, we simply hold on to the class and construct it whenever we get called to do so.
 *
 * @author don_bruce
 */
@Mod.EventBusSubscriber
public class BuilderEntity extends Entity{
	AEntityBase entity;
	
	private boolean requestDataFromServer;
	/**Last saved explosion position (used for damage calcs).**/
	private static Point3d lastExplosionPosition;
	/**Position where we have spawned a fake light.  Used for shader compatibility.**/
	private BlockPos fakeLightPosition;
	
	private WrapperAABBCollective interactionBoxes;
	private WrapperAABBCollective collisionBoxes;
	
	public BuilderEntity(World world){
		super(world);
	}
    
    @Override
    public void onEntityUpdate(){
    	//If our entity isn't null, update it and our position.
    	if(entity != null){
    		//First forward the update call.
    		entity.update();
    		
    		//Update AABBs.
    		//We need to update a wrapper class here as normal entities only allow a single collision box.
    		//We also need to know if we need to increase the max world collision bounds to detect this entity.
    		double furthestWidthRadius = 0;
    		double furthestHeightRadius = 0;
    		for(BoundingBox box : entity.interactionBoxes){
    			furthestWidthRadius = (float) Math.max(furthestWidthRadius, box.localCenter.x + box.widthRadius);
    			furthestHeightRadius = (float) Math.max(furthestHeightRadius, box.localCenter.y + box.heightRadius);
    			furthestWidthRadius = (float) Math.max(furthestWidthRadius, box.localCenter.z + box.depthRadius);
    		}
    		this.setSize((float) furthestWidthRadius*2F, (float) furthestHeightRadius*2F);
    		interactionBoxes = new WrapperAABBCollective(entity, entity.interactionBoxes);
    		collisionBoxes = new WrapperAABBCollective(entity, entity.collisionBoxes);
    		
    		//Make sure the collision bounds for MC are big enough to collide with this entity.
			if(World.MAX_ENTITY_RADIUS < furthestWidthRadius || World.MAX_ENTITY_RADIUS < furthestHeightRadius){
				World.MAX_ENTITY_RADIUS = Math.max(furthestWidthRadius, furthestHeightRadius);
			}
    		
			//Set the new position and rotation.
    		setPosition(entity.position.x, entity.position.y, entity.position.z);
    		rotationYaw = (float) -entity.angles.y;
    		rotationPitch = (float) entity.angles.x;
    		
    		//Check that riders are still present prior to updating them.
    		//This handles dismounting of riders from entities in a non-event-driven way.
    		//We do this because other mods and Sponge like to screw up the events...
    		Iterator<WrapperEntity> riderIterator = entity.ridersToLocations.keySet().iterator();
    		while(riderIterator.hasNext()){
    			WrapperEntity rider = riderIterator.next();
    			if(!this.equals(rider.entity.getRidingEntity())){
    				entity.removeRider(rider, riderIterator);
    			}
    		}
    		
    		//Update riders once we have updated the main entity and ensured all the riders are still present.
    		entity.updateRiders();
    		
    		//Update fake block lighting.  This helps with shaders as they sometimes refuse to light things up.
    		if(world.isRemote){
    			if(entity.isLitUp()){
					BlockPos newPos = getPosition();
					//Check to see if we need to place a light.
					if(!newPos.equals(fakeLightPosition)){
						//If our prior position is not null, remove that block.
						if(fakeLightPosition != null){
							world.setBlockToAir(fakeLightPosition);
							world.checkLight(fakeLightPosition);
							fakeLightPosition = null;
						}
						//Set block in world and update pos.  Only do this if the block is air.
						if(world.isAirBlock(newPos)){
							world.setBlockState(newPos, BuilderBlockFakeLight.instance.getDefaultState());
							world.checkLight(newPos);
							fakeLightPosition = newPos;
						}
					}
    			}else if(fakeLightPosition != null){
    				//Lights are off, turn off fake light.
    				world.setBlockToAir(fakeLightPosition);
    				world.checkLight(fakeLightPosition);
    				fakeLightPosition = null;
    			}
    		}
    	}else{
    		//No entity.  Wait for NBT to be loaded to create it.
    		//If we are on a client, ensure we sent a packet to the server to request it.
    		///Although we could call this in the constructor, Minecraft changes the
    		//entity IDs after spawning and that fouls things up.
    		//To accommodate this, we request a packet whenever the entityID changes.
    		if(requestDataFromServer){
    			InterfaceNetwork.sendToServer(new PacketEntityCSHandshake(this.getEntityId(), null));
    			requestDataFromServer = false;
    		}
    	}
    }
    
	@Override
	public void setDead(){
		super.setDead();
		//Get rid of the fake light before we kill ourselves.
		if(fakeLightPosition != null){
			world.setBlockToAir(fakeLightPosition);
		}
	}
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount){
		if(!world.isRemote && entity != null){
			if(source.getImmediateSource() != null){
				Entity attacker = source.getImmediateSource();
				WrapperPlayer playerSource = source.getTrueSource() instanceof EntityPlayer ? new WrapperPlayer((EntityPlayer) source.getTrueSource()) : null;
				Damage damage = null;
				
				//Check the damage at the current position of the attacker.
				Point3d attackerPosition = new Point3d(attacker.posX, attacker.posY, attacker.posZ);
				for(BoundingBox box : entity.collisionBoxes){
					if(box.isPointInside(attackerPosition)){
						damage = new Damage(source.damageType, amount, box, playerSource);
						break;
					}
				}
				
				if(damage == null){
					//Check the theoretical position of the entity should it have moved.
					//Some projectiles may call their attacking code before updating their positions.
					attackerPosition.add(attacker.motionX, attacker.motionY, attacker.motionZ);
					for(BoundingBox box : entity.collisionBoxes){
						if(box.isPointInside(attackerPosition)){
							damage = new Damage(source.damageType, amount, box, playerSource);
							break;
						}
					}
				}
				
				//If we have damage on a point, attack it now.
				if(damage != null){
					entity.attack(damage);
				} 
			}else if(lastExplosionPosition != null && source.isExplosion()){
				//We encountered an explosion.  These don't have entities linked to them, despite TNT being a TE.
				//Note that explosions, unlike other damage sources, can hit multiple collision boxes on an entity at once.
				for(BoundingBox box : entity.collisionBoxes){
					if(box.isPointInside(lastExplosionPosition)){
						entity.attack(new Damage(source.damageType, amount, box, null).setExplosive());
					}
				}	
			}
		}
		return true;
    }
    
    @Override
	public AxisAlignedBB getEntityBoundingBox(){
		//Override this to make interaction checks work with the multiple collision points.
		//We return the collision and interaction boxes here as we need a bounding box large enough to encompass both.
		return interactionBoxes != null ? interactionBoxes : super.getEntityBoundingBox();
	}
	
	@Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(){
		//Override this to make collision checks work with the multiple collision points.
		//We only return collision boxes here as we don't want the player to collide with interaction boxes.
		return collisionBoxes != null ? collisionBoxes : super.getCollisionBoundingBox();
    }
    
    @Override
    public void setEntityId(int id){
    	super.setEntityId(id);
    	//If we are setting our ID on a client, request NBT data from the server to load the rest of our properties.
    	//We do this on our next update tick, as we may not yet be spawned at this point.
    	requestDataFromServer = world.isRemote;
    }
    
    @Override
    public boolean startRiding(Entity mcEntity, boolean force){
    	//Forward this call to the entity if this is a force riding.
    	//In this case, we're re-loading riders and need to put them
    	//in their proper locations.
    	if(force){
    		entity.addRider(new WrapperEntity(mcEntity), null);
    	}
    	return super.startRiding(mcEntity, force);
    }
    
    @Override
	public boolean canBeCollidedWith(){
		//This gets overridden to allow players to interact with this entity.
		return true;
	}
	
	@Override
	public boolean canRiderInteract(){
		//Return true here to allow player to interact with this entity while riding.
        return true;
    }

    @Override
    public void setPositionAndRotationDirect(double posX, double posY, double posZ, float yaw, float pitch, int posRotationIncrements, boolean teleport){
    	//Overridden due to stupid tracker behavior.
    	//Client-side render changes calls put in its place.
    	setRenderDistanceWeight(100);
    	this.ignoreFrustumCheck = true;
    }
    
    @Override
    public boolean shouldRenderInPass(int pass){
        //Need to render in pass 1 to render transparent things in the world like light beams.
    	return true;
    }
			
    @Override
	public void readFromNBT(NBTTagCompound tag){
    	//FIXME make this be called once on the server once this entity is spawned to kick-off the loading process.
		super.readFromNBT(tag);
		//Build this entity from NBT.  But only do so if the NBT has all the data we need.
		//We can tell this if we have a special bit set that only gets set if we've saved before.
		if(tag.getBoolean("previouslySaved")){
			if(entity != null){
				//FIXME see if this occurs frequently.
				MTS.MTSLog.error("ERROR: Loading vehicle after it has already been loaded once.  Things may go badly!");
			}
			entity = new EntityVehicleF_Physics(new WrapperWorld(world));
		}
	}
    
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		//Write in a special bit to tell us we are loading saved NBT in future calls.
		tag.setBoolean("previouslySaved", true);
		
		//Forward on saving call to entity, if it exists.
		if(entity != null){
			entity.save(new WrapperNBT(tag));
		}
		return tag;
	}
	
	/**
	 * We need to use explosion events here as we don't know where explosions occur in the world.
	 * This results in them being position-less, so we can't get the collision box they hit for damage.
	 * Whenever we have an explosion detonated in the world, save it's position.  We can then use it
	 * in {@link #attackEntityFrom(DamageSource, float)} to tell the system which part to attack.
	 */
	@SubscribeEvent
	public static void on(ExplosionEvent.Detonate event){
		if(!event.getWorld().isRemote){
			lastExplosionPosition = new Point3d(event.getExplosion().getPosition().x, event.getExplosion().getPosition().y, event.getExplosion().getPosition().z);
		}
	}
	
	//Junk methods, forced to pull in.
	protected void entityInit(){}
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_){}
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_){}
}

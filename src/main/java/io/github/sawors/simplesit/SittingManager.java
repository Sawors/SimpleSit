package io.github.sawors.simplesit;

import io.papermc.paper.math.Rotations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Objects;

public class SittingManager implements Listener{
    
    public static NamespacedKey utilityKey = new NamespacedKey(SimpleSit.getPlugin(),"seat");
    
    /**
     * Used to check if a player is currently seated on a seat managed by this plugin.
     *
     * @param player the player to check if they are seated
     * @return weather or not the player is currently seated
     */
    public static boolean isPlayerSitting(Player player){
        return player.isInsideVehicle() && player.getVehicle() != null && isSeatEntity(player.getVehicle());
    }
    
    /**
     * Used to check for an entity if it is a seat created by this plugin.
     *
     * @param entity the entity to check
     * @return weather or not this entity is a seat created by this plugin
     */
    public static boolean isSeatEntity(Entity entity){
        return Objects.equals(entity.getPersistentDataContainer().get(utilityKey,PersistentDataType.STRING),getEntityIdentifier());
    }
    
    @EventHandler
    protected static void playerSit(PlayerInteractEvent event){
        final Block b = event.getClickedBlock();
        final Player p = event.getPlayer();
        if(
                b != null
                        && (
                        (b.getBlockData() instanceof Stairs str
                                && str.getShape().equals(Stairs.Shape.STRAIGHT))
                                && str.getHalf().equals(Bisected.Half.BOTTOM)
                                ||
                                (b.getBlockData() instanceof Slab slb
                                        && slb.getType().equals(Slab.Type.BOTTOM)
                                )
                )
                        && !p.isSneaking()
                        && event.getAction().isRightClick()
                        && event.getHand() != null
                        && event.getHand().equals(EquipmentSlot.HAND)
                        && p.getInventory().getItemInMainHand().getType().equals(Material.AIR)
                        && p.hasPermission("sit.sit")
        ){
            if(p.isJumping() || p.isSwimming() || p.isFlying() || p.isClimbing() || p.isBlocking() || p.isRiptiding() || p.isSprinting()){
                p.sendActionBar(Component.text("you must be standing still to sit").color(NamedTextColor.RED));
            } else {
                // All conditions for player sitting matched
                final Block sideX1 = b.getRelative(1,0,0);
                final Block sideX2 = b.getRelative(-1,0,0);
                final Block sideZ1 = b.getRelative(0,0,1);
                final Block sideZ2 = b.getRelative(0,0,-1);
                Axis axis = null;
                if((sideX1.getBlockData() instanceof WallSign || sideX2.getBlockData() instanceof WallSign)){
                    axis = Axis.X;
                } else if ((sideZ1.getBlockData() instanceof WallSign || sideZ2.getBlockData() instanceof WallSign)){
                    axis = Axis.Z;
                }
                if(axis != null){
                    sitPlayerOnBlock(p,b,axis);
                }
            }
            
            
        }
    }
    
    
    /**
     * Used to make a player sit on top of a block.
     * @param player the player to make sit
     * @param seat the block on top of which the player will sit
     * @param axis the orientation of the sitting position. This is mainly used for the player's legs orientation
     */
    protected static void sitPlayerOnBlock(Player player, Block seat, Axis axis){
        if(!seat.getType().isSolid()) return;
        PlayerSitEvent event = new PlayerSitEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        final Vector reference = new Vector(0,0,1);
        final Vector offset = seat.getBlockData() instanceof Stairs stairs ?
                stairs.getFacing().getDirection().multiply(-.20).add(new Vector(0,-0.5,0))
                : new Vector(0,0,0);
        final float yaw = seat.getBlockData() instanceof Stairs stairs
                ?
                (stairs.getFacing().getDirection().getX() < 0
                        ? -(float) Math.toDegrees(stairs.getFacing().getOppositeFace().getDirection().angle(reference))
                        : (float) Math.toDegrees(stairs.getFacing().getOppositeFace().getDirection().angle(reference)))
                : axis.equals(Axis.X) ? 0 : 90;
        //seat.getBoundingBox().getHeight()-0.95
        Location sitLoc = seat.getLocation();
        BoundingBox box = seat.getBoundingBox();
        sitLoc.setY(box.getCenterY());
        sitLoc.add(.5,(box.getHeight()/2)-0.95,.5);
        ArmorStand seatEntity = (ArmorStand) seat.getWorld().spawnEntity(
                sitLoc.add(offset),
                EntityType.ARMOR_STAND,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                e -> {
                    if(e instanceof ArmorStand armorStand){
                        armorStand.setInvisible(true);
                        armorStand.setGravity(false);
                        armorStand.setSmall(true);
                        armorStand.setArms(false);
                        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING);
                        armorStand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
                        armorStand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
                        armorStand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
                        armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
                        armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
                        armorStand.getPersistentDataContainer().set(utilityKey, PersistentDataType.STRING, getEntityIdentifier());
                        final Location location = armorStand.getLocation();
                        location.setYaw(yaw);
                        armorStand.teleport(location);
                        armorStand.setBodyYaw(yaw);
                        armorStand.setHeadRotations(Rotations.ofDegrees(0,yaw,0));
                    }
                }
        );
        final Location rotate = player.getLocation();
        rotate.setYaw(yaw);
        player.teleport(rotate);
        new BukkitRunnable(){
            @Override
            public void run() {
                seatEntity.addPassenger(player);
            }
        }.runTask(SimpleSit.getPlugin());
    }
    
    /**
     * Make the player sit on top of the block they are currently standing on. This will work only if the player is standing on top of a solid block.
     * @param player the player to make sit
     */
    public static void sitPlayer(Player player){
        final float yaw = player.getLocation().getYaw();
        sitPlayerOnBlock(player,player.getLocation().add(0,-.1,0).getBlock(), yaw >= 315 || yaw < 45 || (yaw >= 135 && yaw < 225) ? Axis.X : Axis.Z);
    }
    
    @EventHandler
    protected static void removeSeatEntityOnDismount(EntityDismountEvent event){
        if(isSeatEntity(event.getDismounted())){
            if(event.getEntity() instanceof Player player){
                PlayerLeaveSitEvent leaveEvent = new PlayerLeaveSitEvent(player);
                Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
                if(leaveEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
            }
            destroySeat(event.getDismounted());
        }
    }
    
    @EventHandler
    protected static void removeSeatEntityOnServerStart(PluginEnableEvent event){
        if(event.getPlugin().equals(SimpleSit.getPlugin())){
            for(World world : Bukkit.getWorlds()){
                for(Entity e : world.getEntitiesByClass(ArmorStand.class)){
                    // Don't worry, the check to see if the entity is a valid seat is done in the method ! This will fail silently if the entity is not a seat.
                    destroySeat(e);
                }
            }
        }
    }
    
    public static void destroySeat(Entity seat){
        if(isSeatEntity(seat)){
            seat.remove();
            for(Entity passenger : seat.getPassengers()){
                passenger.teleport(passenger.getLocation().add(0,1,0));
            }
        }
    }
    
    /**
     *
     * @return the identifier used by this plugin to mark seats
     */
    protected static String getEntityIdentifier() {
        return "seat";
    }
}

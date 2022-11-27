package iskallia.vault.world.vault.logic.objective.raid;

import iskallia.vault.block.VaultRaidControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.FluidPlaceBlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;

public class RaidEventListener {
   public static void onDeath(LivingDeathEvent event) {
      LivingEntity died = event.getEntityLiving();
      Level world = died.getCommandSenderWorld();
      BlockPos at = died.blockPosition();
      ActiveRaid raid = getRaidAt(world, at);
      if (raid != null) {
         raid.getActiveEntities().remove(died.getUUID());
         if (raid.getActiveEntities().isEmpty() && raid.hasNextWave()) {
            raid.setStartDelay(100);
         }
      }
   }

   public static void onSpawn(SummonAidEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getSummoner().blockPosition())) {
         event.setResult(Result.DENY);
      }
   }

   public static void onBreak(BreakEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
      }
   }

   public static void onPlace(EntityPlaceEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
      }
   }

   public static void onFluidPlace(FluidPlaceBlockEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
      }
   }

   public static void onInteract(PlayerInteractEvent event) {
      if (event instanceof RightClickBlock) {
         BlockState interacted = event.getWorld().getBlockState(event.getPos());
         if (interacted.getBlock() instanceof VaultRaidControllerBlock) {
            return;
         }
      }

      ItemStack interacted = event.getItemStack();
      if (event instanceof RightClickItem) {
         UseAnim action = interacted.getUseAnimation();
         if (action == UseAnim.EAT || action == UseAnim.DRINK) {
            return;
         }

         if (isWhitelistedItem(interacted)) {
            return;
         }
      }

      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
         event.setCancellationResult(InteractionResult.FAIL);
      }
   }

   private static boolean isWhitelistedItem(ItemStack interacted) {
      if (interacted.isEmpty()) {
         return false;
      } else {
         ResourceLocation key = interacted.getItem().getRegistryName();
         if (key.getNamespace().equals("dankstorage") && key.getPath().startsWith("dank_")) {
            return true;
         } else {
            return key.toString().equals("quark:pickarang") || key.toString().equals("quark:flamerang") ? true : key.getNamespace().equals("simplybackpacks");
         }
      }
   }

   private static boolean isInLockedRaidRoom(LevelAccessor world, BlockPos pos) {
      return false;
   }

   private static ActiveRaid getRaidAt(LevelAccessor world, BlockPos pos) {
      return null;
   }
}

package iskallia.vault.world.vault.logic.objective.raid;

import iskallia.vault.block.VaultRaidControllerBlock;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultRaidRoom;
import iskallia.vault.world.vault.logic.objective.raid.modifier.DamageTakenModifier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.FluidPlaceBlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class RaidEventListener {
   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.func_130014_f_();
      if (!world.func_201670_d()) {
         if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)entity;
            VaultRaid vault = VaultRaidData.get(sPlayer.func_71121_q()).getAt(sPlayer.func_71121_q(), sPlayer.func_233580_cy_());
            if (vault != null) {
               ActiveRaid raid = vault.getActiveRaid();
               if (raid != null && raid.isPlayerInRaid(sPlayer)) {
                  float dmg = event.getAmount();
                  dmg = (float)(
                     dmg
                        * (
                           1.0
                              + vault.getActiveObjective(RaidChallengeObjective.class)
                                 .map(
                                    raidObjective -> raidObjective.getModifiersOfType(DamageTakenModifier.class)
                                       .values()
                                       .stream()
                                       .mapToDouble(Float::doubleValue)
                                       .sum()
                                 )
                                 .orElse(0.0)
                        )
                  );
                  event.setAmount(dmg);
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onDeath(LivingDeathEvent event) {
      LivingEntity died = event.getEntityLiving();
      World world = died.func_130014_f_();
      BlockPos at = died.func_233580_cy_();
      ActiveRaid raid = getRaidAt(world, at);
      if (raid != null) {
         raid.getActiveEntities().remove(died.func_110124_au());
         if (raid.getActiveEntities().isEmpty() && raid.hasNextWave()) {
            raid.setStartDelay(100);
         }
      }
   }

   @SubscribeEvent
   public static void onSpawn(SummonAidEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getSummoner().func_233580_cy_())) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent
   public static void onBreak(BreakEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onPlace(EntityPlaceEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onFluidPlace(FluidPlaceBlockEvent event) {
      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onInteract(PlayerInteractEvent event) {
      if (event instanceof RightClickBlock) {
         BlockState interacted = event.getWorld().func_180495_p(event.getPos());
         if (interacted.func_177230_c() instanceof VaultRaidControllerBlock) {
            return;
         }
      }

      ItemStack interacted = event.getItemStack();
      if (event instanceof RightClickItem) {
         UseAction action = interacted.func_77975_n();
         if (action == UseAction.EAT || action == UseAction.DRINK) {
            return;
         }

         if (isWhitelistedItem(interacted)) {
            return;
         }
      }

      if (isInLockedRaidRoom(event.getWorld(), event.getPos())) {
         event.setCanceled(true);
         event.setCancellationResult(ActionResultType.FAIL);
      }
   }

   private static boolean isWhitelistedItem(ItemStack interacted) {
      if (interacted.func_190926_b()) {
         return false;
      } else {
         ResourceLocation key = interacted.func_77973_b().getRegistryName();
         if (key.func_110624_b().equals("dankstorage") && key.func_110623_a().startsWith("dank_")) {
            return true;
         } else {
            return key.toString().equals("quark:pickarang") || key.toString().equals("quark:flamerang") ? true : key.func_110624_b().equals("simplybackpacks");
         }
      }
   }

   private static boolean isInLockedRaidRoom(IWorld world, BlockPos pos) {
      if (!world.func_201670_d() && world instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)world;
         VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, pos);
         if (vault == null) {
            return false;
         } else {
            VaultRaidRoom room = vault.getGenerator().getPiecesAt(pos, VaultRaidRoom.class).stream().findFirst().orElse(null);
            return room == null ? false : !room.isRaidFinished();
         }
      } else {
         return false;
      }
   }

   private static ActiveRaid getRaidAt(IWorld world, BlockPos pos) {
      if (!world.func_201670_d() && world instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)world;
         VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, pos);
         return vault == null ? null : vault.getActiveRaid();
      } else {
         return null;
      }
   }
}

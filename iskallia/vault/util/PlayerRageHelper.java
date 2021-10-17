package iskallia.vault.util;

import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.RageSyncMessage;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.archetype.BarbaricTalent;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class PlayerRageHelper {
   public static final int MAX_RAGE = 100;
   public static final int RAGE_DEGEN_INTERVAL = 10;
   private static final Map<UUID, Integer> lastAttackTick = new HashMap<>();
   private static final Map<UUID, Integer> currentRage = new HashMap<>();
   private static final Map<UUID, PlayerDamageHelper.DamageMultiplier> multiplierMap = new HashMap<>();
   private static int clientRageInfo = 0;

   @SubscribeEvent
   public static void onChangeDim(EntityTravelToDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayerEntity) {
         if (event.getDimension().equals(Vault.VAULT_KEY)) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
            lastAttackTick.remove(player.func_110124_au());
            setCurrentRage(player, 0);
         }
      }
   }

   @SubscribeEvent
   public static void onGainRage(LivingHurtEvent event) {
      World world = event.getEntityLiving().func_130014_f_();
      if (!world.func_201670_d()) {
         Entity source = event.getSource().func_76346_g();
         if (source instanceof ServerPlayerEntity) {
            ServerPlayerEntity attacker = (ServerPlayerEntity)source;
            UUID playerUUID = attacker.func_110124_au();
            int lastAttack = lastAttackTick.getOrDefault(playerUUID, 0);
            if (lastAttack <= attacker.field_70173_aa - 10) {
               TalentTree tree = PlayerTalentsData.get(attacker.func_71121_q()).getTalents(attacker);
               TalentNode<BarbaricTalent> node = tree.getNodeOf(ModConfigs.TALENTS.BARBARIC);
               if (node.isLearned()) {
                  int rage = getCurrentRage(playerUUID, LogicalSide.SERVER);
                  setCurrentRage(attacker, rage + node.getTalent().getRagePerAttack());
                  refreshDamageBuff(attacker, node.getTalent().getDamageMultiplierPerRage());
                  lastAttackTick.put(playerUUID, attacker.field_70173_aa);
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayerEntity) {
         lastAttackTick.remove(event.getEntityLiving().func_110124_au());
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.phase != Phase.START && event.player instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)event.player;
         UUID playerUUID = sPlayer.func_110124_au();
         int rage = getCurrentRage(sPlayer, LogicalSide.SERVER);
         if (rage <= 0) {
            removeExistingDamageBuff(sPlayer);
         } else if (!canGenerateRage(sPlayer)) {
            setCurrentRage(sPlayer, 0);
            removeExistingDamageBuff(sPlayer);
         } else {
            TalentTree tree = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
            BarbaricTalent talent = tree.getNodeOf(ModConfigs.TALENTS.BARBARIC).getTalent();
            int lastTick = lastAttackTick.getOrDefault(playerUUID, 0);
            if (lastTick < sPlayer.field_70173_aa - talent.getRageDegenTickDelay() && sPlayer.field_70173_aa % 10 == 0) {
               setCurrentRage(sPlayer, rage - 1);
               refreshDamageBuff(sPlayer, talent.getDamageMultiplierPerRage());
            }
         }
      }
   }

   private static void setCurrentRage(ServerPlayerEntity player, int rage) {
      rage = MathHelper.func_76125_a(rage, 0, 100);
      currentRage.put(player.func_110124_au(), rage);
      ModNetwork.CHANNEL.sendTo(new RageSyncMessage(rage), player.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static int getCurrentRage(PlayerEntity player, LogicalSide side) {
      return getCurrentRage(player.func_110124_au(), side);
   }

   public static int getCurrentRage(UUID playerUUID, LogicalSide side) {
      return side.isServer() ? currentRage.getOrDefault(playerUUID, 0) : clientRageInfo;
   }

   private static boolean canGenerateRage(ServerPlayerEntity sPlayer) {
      TalentTree tree = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
      return tree.hasLearnedNode(ModConfigs.TALENTS.BARBARIC);
   }

   private static void refreshDamageBuff(ServerPlayerEntity sPlayer, float dmgMultiplier) {
      UUID playerUUID = sPlayer.func_110124_au();
      int rage = getCurrentRage(playerUUID, LogicalSide.SERVER);
      removeExistingDamageBuff(sPlayer);
      multiplierMap.put(playerUUID, PlayerDamageHelper.applyMultiplier(sPlayer, rage * dmgMultiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY));
   }

   private static void removeExistingDamageBuff(ServerPlayerEntity player) {
      PlayerDamageHelper.DamageMultiplier existing = multiplierMap.get(player.func_110124_au());
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void clearClientCache() {
      clientRageInfo = 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static void receiveRageUpdate(RageSyncMessage msg) {
      clientRageInfo = msg.getRage();
   }
}

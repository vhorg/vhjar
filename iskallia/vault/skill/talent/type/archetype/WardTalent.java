package iskallia.vault.skill.talent.type.archetype;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.util.calc.AbsorptionHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class WardTalent extends ArchetypeTalent {
   private static final Map<UUID, Long> lastAttackedTick = new HashMap<>();
   @Expose
   protected int startRegenAfterCombatSeconds;
   @Expose
   protected EffectTalent fullAbsorptionEffect;
   @Expose
   protected float additionalParryChance;

   public WardTalent(int cost, int startRegenAfterCombatSeconds, EffectTalent fullAbsorptionEffect, float additionalParryChance) {
      super(cost);
      this.startRegenAfterCombatSeconds = startRegenAfterCombatSeconds;
      this.fullAbsorptionEffect = fullAbsorptionEffect;
      this.additionalParryChance = additionalParryChance;
   }

   public int getStartRegenAfterCombatSeconds() {
      return this.startRegenAfterCombatSeconds;
   }

   @Nullable
   public EffectTalent getFullAbsorptionEffect() {
      return this.fullAbsorptionEffect;
   }

   public float getAdditionalParryChance() {
      return this.additionalParryChance;
   }

   public static boolean isGrantedFullAbsorptionEffect(ServerWorld world, PlayerEntity sPlayer) {
      TalentTree tree = PlayerTalentsData.get(world).getTalents(sPlayer);
      if (tree.hasLearnedNode(ModConfigs.TALENTS.WARD)) {
         float max = AbsorptionHelper.getMaxAbsorption(sPlayer);
         return sPlayer.func_110139_bj() / max >= 0.9F;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingDamageEvent event) {
      LivingEntity attacked = event.getEntityLiving();
      if (!attacked.func_130014_f_().func_201670_d() && attacked instanceof ServerPlayerEntity) {
         lastAttackedTick.put(attacked.func_110124_au(), attacked.func_184102_h().func_241755_D_().func_82737_E());
      }
   }

   @SubscribeEvent
   public static void onChangeDim(EntityTravelToDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayerEntity) {
         if (event.getDimension().equals(Vault.VAULT_KEY)) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
            player.func_110149_m(0.0F);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START && event.side.isServer() && event.player.field_70173_aa % 20 == 0) {
         if (event.player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)event.player;
            UUID playerUUID = sPlayer.func_110124_au();
            float maxAbsorption = AbsorptionHelper.getMaxAbsorption(sPlayer);
            if (!(maxAbsorption <= 0.1F)) {
               TalentTree tree = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
               int startSeconds = tree.getLearnedNodes(WardTalent.class)
                  .stream()
                  .mapToInt(node -> node.getTalent().getStartRegenAfterCombatSeconds())
                  .min()
                  .orElse(-1);
               if (startSeconds >= 0) {
                  if (lastAttackedTick.containsKey(playerUUID)) {
                     long lastAttacked = lastAttackedTick.get(playerUUID);
                     long current = sPlayer.func_184102_h().func_241755_D_().func_82737_E();
                     if (lastAttacked >= current - startSeconds * 20) {
                        return;
                     }
                  }

                  float absorption = sPlayer.func_110139_bj();
                  if (absorption < maxAbsorption) {
                     sPlayer.func_110149_m(Math.min(absorption + 2.0F, maxAbsorption));
                  }
               }
            }
         }
      }
   }
}

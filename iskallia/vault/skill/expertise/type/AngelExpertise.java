package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TickingSkill;
import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.ServerVaults;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class AngelExpertise extends LearnableSkill implements TickingSkill {
   private static final String ANGEL_DISABLED_TAG = VaultMod.sId("angel_disabled");

   public AngelExpertise(int unlockLevel, int learnPointCost, int regretPointCost) {
      super(unlockLevel, learnPointCost, regretPointCost);
   }

   public AngelExpertise() {
   }

   public static void toggleAngel(ServerPlayer player) {
      if (player.getTags().contains(ANGEL_DISABLED_TAG)) {
         player.getTags().remove(ANGEL_DISABLED_TAG);
      } else {
         player.getTags().add(ANGEL_DISABLED_TAG);
      }
   }

   private boolean isAngelSwitchedOff(Player player) {
      return player.getTags().contains(ANGEL_DISABLED_TAG);
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(Player.class).ifPresent(player -> {
         if (!player.isSpectator() && !player.isCreative()) {
            removeCreativeFlight(player);
            player.onUpdateAbilities();
         }
      });
   }

   private static void removeCreativeFlight(Player player) {
      player.getAbilities().mayfly = false;
      player.getAbilities().flying = false;
      player.removeEffect(ModEffects.ANGEL);
   }

   private static void giveCreativeFlight(Player player) {
      player.getAbilities().mayfly = true;
      ModEffects.ANGEL.addTo(player, 0);
   }

   @Override
   public void onTick(SkillContext context) {
      if (this.isUnlocked()) {
         context.getSource()
            .as(Player.class)
            .ifPresent(
               player -> {
                  if (!player.isSpectator()
                     && !player.isCreative()
                     && (
                        ServerVaults.get(player.level).isPresent()
                           || this.isAngelSwitchedOff(player)
                           || VHSmpUtil.isArenaWorld(player.level)
                           || !ModBlocks.ANGEL_BLOCK.isInRange(player)
                     )) {
                     if (player.getAbilities().mayfly && player.getAbilities().flying) {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
                     }

                     removeCreativeFlight(player);
                  } else if (!player.getAbilities().mayfly) {
                     giveCreativeFlight(player);
                  }

                  player.onUpdateAbilities();
               }
            );
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt();
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson();
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
   }

   public static class AngelEffect extends ToggleAbilityEffect {
      public AngelEffect(int color, ResourceLocation resourceLocation) {
         super(ManaShieldAbility.class, color, resourceLocation);
      }
   }
}

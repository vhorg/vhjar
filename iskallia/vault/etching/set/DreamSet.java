package iskallia.vault.etching.set;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

public class DreamSet extends EtchingSet<DreamSet.Config> implements GearAttributeSet, EffectSet {
   private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("6abda41d-0564-4666-ac0d-0a24230a8b90");

   public DreamSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<DreamSet.Config> getConfigClass() {
      return DreamSet.Config.class;
   }

   public DreamSet.Config getDefaultConfig() {
      return new DreamSet.Config(MobEffects.DIG_SPEED.getRegistryName(), 2, 0.5F, 0.1F, 0.25F, 0.1F);
   }

   @Override
   public List<EffectSet.GrantedEffect> getGrantedEffects() {
      EffectSet.GrantedEffect effect = this.getConfig().createGrantedEffect();
      return (List<EffectSet.GrantedEffect>)(effect != null ? Lists.newArrayList(new EffectSet.GrantedEffect[]{effect}) : Collections.emptyList());
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      DreamSet.Config cfg = this.getConfig();
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{
            new VaultGearAttributeInstance<>(ModGearAttributes.RESISTANCE, cfg.getIncreasedResistance()),
            new VaultGearAttributeInstance<>(ModGearAttributes.ITEM_RARITY, cfg.getIncreasedItemRarity()),
            new VaultGearAttributeInstance<>(ModGearAttributes.BLOCK, cfg.getIncreasedBlockChance())
         }
      );
   }

   @Override
   public void tick(ServerPlayer player) {
      super.tick(player);
      if (PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID) == null) {
         float dmgMultiplier = this.getConfig().getIncreasedDamage();
         PlayerDamageHelper.applyMultiplier(DAMAGE_MULTIPLIER_ID, player, dmgMultiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY);
      }
   }

   @Override
   public void remove(ServerPlayer player) {
      super.remove(player);
      PlayerDamageHelper.removeMultiplier(player, DAMAGE_MULTIPLIER_ID);
   }

   public static class Config extends EffectSet.EffectConfig {
      @Expose
      private float increasedDamage;
      @Expose
      private float increasedResistance;
      @Expose
      private float increasedItemRarity;
      @Expose
      private float increasedBlockChance;

      public Config(
         ResourceLocation effect, int addedAmplifier, float increasedDamage, float increasedResistance, float increasedItemRarity, float increasedBlockChance
      ) {
         super(effect, addedAmplifier);
         this.increasedDamage = increasedDamage;
         this.increasedResistance = increasedResistance;
         this.increasedItemRarity = increasedItemRarity;
         this.increasedBlockChance = increasedBlockChance;
      }

      public float getIncreasedDamage() {
         return this.increasedDamage;
      }

      public float getIncreasedResistance() {
         return this.increasedResistance;
      }

      public float getIncreasedItemRarity() {
         return this.increasedItemRarity;
      }

      public float getIncreasedBlockChance() {
         return this.increasedBlockChance;
      }
   }
}

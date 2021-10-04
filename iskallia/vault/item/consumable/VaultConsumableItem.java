package iskallia.vault.item.consumable;

import iskallia.vault.config.entry.ConsumableEffect;
import iskallia.vault.config.entry.ConsumableEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.calc.AbsorptionHelper;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Food.Builder;
import net.minecraft.item.Item.Properties;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultConsumableItem extends Item {
   public static Food VAULT_FOOD = new Builder().func_221454_a(0.0F).func_221456_a(0).func_221457_c().func_221455_b().func_221453_d();

   public VaultConsumableItem(ResourceLocation id) {
      super(new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_221540_a(VAULT_FOOD).func_200917_a(64));
      this.setRegistryName(id);
   }

   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      ResourceLocation itemId = stack.func_77973_b().getRegistryName();
      if (ModConfigs.CONSUMABLES != null && itemId != null) {
         List<String> text = ModConfigs.CONSUMABLES.getDescriptionFor(itemId.toString());
         if (text != null) {
            for (String s : text) {
               tooltip.add(new StringTextComponent(s));
            }

            super.func_77624_a(stack, worldIn, tooltip, flagIn);
         }
      }
   }

   public ItemStack func_77654_b(ItemStack stack, World world, LivingEntity entityLiving) {
      if (entityLiving instanceof ServerPlayerEntity) {
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)entityLiving;
         ResourceLocation itemId = stack.func_77973_b().getRegistryName();
         ConsumableEntry entry = ModConfigs.CONSUMABLES.get(itemId.toString());
         if (entry.isPowerup() && sPlayer.func_70660_b(ModEffects.VAULT_POWERUP) != null) {
            return stack;
         }

         if (entry.shouldAddAbsorption()) {
            TalentTree talents = PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer);
            if (talents.hasLearnedNode(ModConfigs.TALENTS.WARD) || talents.hasLearnedNode(ModConfigs.TALENTS.BARBARIC)) {
               return stack;
            }

            float targetAbsorption = sPlayer.func_110139_bj() + entry.getAbsorptionAmount();
            if (targetAbsorption > AbsorptionHelper.getMaxAbsorption(sPlayer)) {
               return stack;
            }

            sPlayer.func_110149_m(targetAbsorption);
         }

         for (ConsumableEffect setting : entry.getEffects()) {
            ResourceLocation id = ResourceLocation.func_208304_a(setting.getEffectId());
            Effect effect = (Effect)ForgeRegistries.POTIONS.getValue(id);
            if (id != null && effect != null) {
               EffectInstance effectInstance = sPlayer.func_70660_b(effect);
               if (effectInstance == null) {
                  if (entry.isPowerup()) {
                     this.applyEffect(sPlayer, effect, setting);
                     this.applyPowerup(sPlayer, setting);
                  } else {
                     this.applyEffect(sPlayer, effect, setting);
                  }
               } else if (entry.isPowerup()) {
                  this.addEffect(sPlayer, effectInstance, setting);
                  this.applyPowerup(sPlayer, setting);
               } else if (effectInstance.func_76458_c() < setting.getAmplifier()) {
                  this.applyEffect(sPlayer, effect, setting);
               } else if (effectInstance.func_76459_b() < setting.getDuration()) {
                  this.addEffectDuration(sPlayer, effectInstance, setting.getDuration());
               }
            }
         }
      }

      return super.func_77654_b(stack, world, entityLiving);
   }

   private void addEffect(PlayerEntity player, EffectInstance effectInstance, ConsumableEffect effect) {
      player.func_195063_d(effectInstance.func_188419_a());
      EffectInstance newEffect = new EffectInstance(
         effectInstance.func_188419_a(),
         effect.getDuration(),
         effectInstance.func_76458_c() + effect.getAmplifier(),
         effectInstance.func_82720_e(),
         effectInstance.func_188418_e(),
         effectInstance.func_205348_f()
      );
      player.func_195064_c(newEffect);
   }

   private void applyPowerup(PlayerEntity player, ConsumableEffect effect) {
      EffectInstance powerup = new EffectInstance(
         ModEffects.VAULT_POWERUP, effect.getDuration(), 0, effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()
      );
      player.func_195064_c(powerup);
   }

   private void applyEffect(PlayerEntity player, Effect effect, ConsumableEffect setting) {
      EffectInstance newEffect = new EffectInstance(
         effect, setting.getDuration(), setting.getAmplifier() - 1, setting.isAmbient(), setting.shouldShowParticles(), setting.shouldShowIcon()
      );
      player.func_195064_c(newEffect);
   }

   private void addEffectDuration(PlayerEntity player, EffectInstance effectInstance, int newDuration) {
      player.func_195063_d(effectInstance.func_188419_a());
      EffectInstance newEffect = new EffectInstance(
         effectInstance.func_188419_a(),
         newDuration,
         effectInstance.func_76458_c(),
         effectInstance.func_82720_e(),
         effectInstance.func_188418_e(),
         effectInstance.func_205348_f()
      );
      player.func_195064_c(newEffect);
   }
}

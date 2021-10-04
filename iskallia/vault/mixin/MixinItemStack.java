package iskallia.vault.mixin;

import iskallia.vault.config.DurabilityConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.UnbreakableTalent;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {ItemStack.class},
   priority = 1001
)
public abstract class MixinItemStack {
   @Shadow
   public abstract boolean func_77984_f();

   @Shadow
   public abstract int func_77952_i();

   @Shadow
   public abstract void func_196085_b(int var1);

   @Shadow
   public abstract int func_77958_k();

   @Shadow
   public abstract ItemStack func_77946_l();

   @Shadow
   public abstract Item func_77973_b();

   @Overwrite
   public boolean func_96631_a(int damage, Random rand, @Nullable ServerPlayerEntity damager) {
      if (!this.func_77984_f()) {
         return false;
      } else {
         if (damage > 0) {
            int unbreakingLevel = EnchantmentHelper.func_77506_a(Enchantments.field_185307_s, (ItemStack)this);
            if (damager != null) {
               TalentTree abilities = PlayerTalentsData.get(damager.func_71121_q()).getTalents(damager);

               for (UnbreakableTalent talent : abilities.getTalents(UnbreakableTalent.class)) {
                  unbreakingLevel = (int)(unbreakingLevel + talent.getExtraUnbreaking());
               }
            }

            int damageNegation = 0;
            boolean isArmor = ((ItemStack)this).func_77973_b() instanceof ArmorItem;
            DurabilityConfig cfg = ModConfigs.DURBILITY;
            float chance = isArmor ? cfg.getArmorDurabilityIgnoreChance(unbreakingLevel) : cfg.getDurabilityIgnoreChance(unbreakingLevel);

            for (int k = 0; unbreakingLevel > 0 && k < damage; k++) {
               if (rand.nextFloat() < chance) {
                  damageNegation++;
               }
            }

            damage -= damageNegation;
            if (damage <= 0) {
               return false;
            }
         }

         if (damager != null && damage != 0) {
            CriteriaTriggers.field_193132_s.func_193158_a(damager, (ItemStack)this, this.func_77952_i() + damage);
         }

         int absDamage = this.func_77952_i() + damage;
         this.func_196085_b(absDamage);
         return absDamage >= this.func_77958_k();
      }
   }

   @Inject(
      method = {"getDisplayName"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void useGearRarity(CallbackInfoReturnable<ITextComponent> ci) {
      if (this.func_77973_b() instanceof VaultGear) {
         ItemStack itemStack = this.func_77946_l();
         VaultGear.State state = ModAttributes.GEAR_STATE.getOrDefault(itemStack, VaultGear.State.UNIDENTIFIED).getValue(itemStack);
         VaultGear.Rarity rarity = ModAttributes.GEAR_RARITY.getOrDefault(itemStack, VaultGear.Rarity.COMMON).getValue(itemStack);
         if (state != VaultGear.State.UNIDENTIFIED) {
            IFormattableTextComponent returnValue = (IFormattableTextComponent)ci.getReturnValue();
            Style style = returnValue.func_150256_b().func_240718_a_(rarity.getColor());
            ci.setReturnValue(returnValue.func_230530_a_(style));
         }
      }
   }
}

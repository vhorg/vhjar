package iskallia.vault.mixin;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.UnbreakableTalent;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
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
   public boolean func_96631_a(int amount, Random rand, @Nullable ServerPlayerEntity damager) {
      if (!this.func_77984_f()) {
         return false;
      } else {
         if (amount > 0) {
            int i = EnchantmentHelper.func_77506_a(Enchantments.field_185307_s, (ItemStack)this);
            if (damager != null) {
               TalentTree abilities = PlayerTalentsData.get(damager.func_71121_q()).getTalents(damager);

               for (TalentNode<?> node : abilities.getNodes()) {
                  if (node.getTalent() instanceof UnbreakableTalent) {
                     UnbreakableTalent talent = (UnbreakableTalent)node.getTalent();
                     i = (int)(i + talent.getExtraUnbreaking());
                  }
               }
            }

            int j = 0;

            for (int k = 0; i > 0 && k < amount; k++) {
               if (UnbreakingEnchantment.func_92097_a((ItemStack)this, i, rand)) {
                  j++;
               }
            }

            amount -= j;
            if (amount <= 0) {
               return false;
            }
         }

         if (damager != null && amount != 0) {
            CriteriaTriggers.field_193132_s.func_193158_a(damager, (ItemStack)this, this.func_77952_i() + amount);
         }

         int l = this.func_77952_i() + amount;
         this.func_196085_b(l);
         return l >= this.func_77958_k();
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
            ci.setReturnValue(returnValue.func_240699_a_(rarity.color));
         }
      }
   }
}

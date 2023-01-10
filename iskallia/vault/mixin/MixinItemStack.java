package iskallia.vault.mixin;

import iskallia.vault.config.DurabilityConfig;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.IConditionalDamageable;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {ItemStack.class},
   priority = 1001
)
public abstract class MixinItemStack {
   @Shadow
   @Final
   @Deprecated
   private Item item;

   @Shadow
   public abstract int getMaxDamage();

   @Shadow
   public abstract ItemStack copy();

   @Shadow
   public abstract Item getItem();

   @Shadow
   public abstract boolean isDamageableItem();

   @Shadow
   public abstract int getDamageValue();

   @Shadow
   public abstract void setDamageValue(int var1);

   @Overwrite
   public boolean hurt(int damage, Random rand, @Nullable ServerPlayer damager) {
      if (!this.isDamageableItem()) {
         return false;
      } else if (this.item == Items.ELYTRA && new Random().nextInt(5) == 0) {
         return false;
      } else {
         if (damage > 0) {
            if (this.getItem() instanceof IConditionalDamageable cd && cd.isImmuneToDamage((ItemStack)this, damager)) {
               return false;
            }

            int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, (ItemStack)this);
            int durabilityNegation = 0;
            boolean isArmor = ((ItemStack)this).getItem() instanceof ArmorItem;
            DurabilityConfig cfg = ModConfigs.DURBILITY;
            float chance = isArmor ? cfg.getArmorDurabilityIgnoreChance(unbreaking) : cfg.getDurabilityIgnoreChance(unbreaking);

            for (int k = 0; unbreaking > 0 && k < damage; k++) {
               if (rand.nextFloat() < chance) {
                  durabilityNegation++;
               }
            }

            int wearReduction = 0;
            if (damager != null) {
               AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(damager);
               float wearReductionChance = snapshot.getAttributeValue(ModGearAttributes.DURABILITY_WEAR_REDUCTION, VaultGearAttributeTypeMerger.floatSum());

               for (int kx = 0; kx < damage; kx++) {
                  if (rand.nextFloat() < wearReductionChance) {
                     wearReduction++;
                  }
               }
            }

            damage -= durabilityNegation;
            damage -= wearReduction;
            if (damage <= 0) {
               return false;
            }
         }

         if (damager != null && damage != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(damager, (ItemStack)this, this.getDamageValue() + damage);
         }

         int absDamage = this.getDamageValue() + damage;
         this.setDamageValue(absDamage);
         return absDamage >= this.getMaxDamage();
      }
   }

   @Inject(
      method = {"getDisplayName"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void useGearRarity(CallbackInfoReturnable<Component> ci) {
      if (this.getItem() instanceof VaultGearItem) {
         ItemStack itemStack = this.copy();
         VaultGearData data = VaultGearData.read(itemStack);
         VaultGearState state = data.getState();
         if (state != VaultGearState.UNIDENTIFIED) {
            MutableComponent returnValue = (MutableComponent)ci.getReturnValue();
            Style style = returnValue.getStyle().withColor(data.getRarity().getColor());
            ci.setReturnValue(returnValue.setStyle(style));
         }
      }
   }

   @Redirect(
      method = {"getTooltipLines"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"
      )
   )
   public boolean alwaysShowDamageTooltip(ItemStack stack) {
      if (stack.getItem() instanceof VaultGearItem) {
         VaultGearData data = VaultGearData.read(stack);
         if (data.getState() == VaultGearState.IDENTIFIED) {
            return true;
         }
      }

      return stack.isDamaged();
   }

   @Inject(
      method = {"getHoverName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getGearHoverName(CallbackInfoReturnable<Component> cir) {
      ItemStack thisInstance = (ItemStack)this;
      if (thisInstance.getItem() instanceof VaultGearItem) {
         CompoundTag compoundtag = thisInstance.getTagElement("display");
         if (compoundtag != null && compoundtag.contains("Name", 8)) {
            try {
               MutableComponent component = Serializer.fromJson(compoundtag.getString("Name"));
               if (component != null) {
                  VaultGearData gearData = VaultGearData.read(thisInstance);
                  cir.setReturnValue(component.withStyle(Style.EMPTY.withColor(gearData.getRarity().getColor())));
                  return;
               }

               compoundtag.remove("Name");
            } catch (Exception var6) {
               compoundtag.remove("Name");
            }
         }
      }
   }
}

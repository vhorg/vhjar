package iskallia.vault.mixin;

import iskallia.vault.Vault;
import iskallia.vault.container.inventory.ShardPouchContainer;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.InventorySnapshotData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
import iskallia.vault.world.vault.modifier.DurabilityDamageModifier;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerInventory.class})
public class MixinPlayerInventory implements InventorySnapshotData.InventoryAccessor {
   @Shadow
   @Final
   public PlayerEntity field_70458_d;
   @Shadow
   @Final
   private List<NonNullList<ItemStack>> field_184440_g;

   @ModifyArg(
      method = {"func_234563_a_"},
      index = 0,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"
      )
   )
   public int limitMaxArmorDamage(int damageAmount) {
      if (this.field_70458_d.field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
         damageAmount = Math.min(damageAmount, 5);
      }

      if (this.field_70458_d.func_130014_f_() instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)this.field_70458_d.func_130014_f_();
         VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, this.field_70458_d.func_233580_cy_());
         if (vault != null) {
            for (VaultAttributeInfluence influence : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
               if (influence.getType() == VaultAttributeInfluence.Type.DURABILITY_DAMAGE && !influence.isMultiplicative()) {
                  damageAmount = (int)(damageAmount + influence.getValue());
               }
            }

            for (DurabilityDamageModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(this.field_70458_d), DurabilityDamageModifier.class)) {
               damageAmount = (int)(damageAmount * modifier.getDurabilityDamageTakenMultiplier());
            }

            for (VaultAttributeInfluence influencex : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
               if (influencex.getType() == VaultAttributeInfluence.Type.DURABILITY_DAMAGE && influencex.isMultiplicative()) {
                  damageAmount = (int)(damageAmount * influencex.getValue());
               }
            }
         }
      }

      return damageAmount;
   }

   @Inject(
      method = {"addItemStackToInventory"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void interceptItemAddition(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (stack.func_77973_b() == ModItems.SOUL_SHARD) {
         if (!(this.field_70458_d.field_71070_bA instanceof ShardPouchContainer)) {
            PlayerInventory thisInventory = (PlayerInventory)this;
            ItemStack pouchStack = ItemStack.field_190927_a;

            for (int slot = 0; slot < thisInventory.func_70302_i_(); slot++) {
               ItemStack invStack = thisInventory.func_70301_a(slot);
               if (invStack.func_77973_b() instanceof ItemShardPouch) {
                  pouchStack = invStack;
                  break;
               }
            }

            if (!pouchStack.func_190926_b()) {
               pouchStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                  ItemStack remainder = handler.insertItem(0, stack, false);
                  stack.func_190920_e(remainder.func_190916_E());
                  if (stack.func_190926_b()) {
                     cir.setReturnValue(true);
                  }
               });
            }
         }
      }
   }

   @Override
   public int getSize() {
      return this.field_184440_g.stream().mapToInt(NonNullList::size).sum();
   }
}

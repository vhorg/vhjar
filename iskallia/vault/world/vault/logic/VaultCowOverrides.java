package iskallia.vault.world.vault.logic;

import iskallia.vault.entity.AggressiveCowEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.vault.VaultRaid;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public class VaultCowOverrides {
   private static final UUID DAMAGE_NERF_MULTIPLIER = UUID.fromString("384df991-f603-344c-a090-3693adfa984a");
   public static final String ENTITY_TAG = "replaced_entity";

   public static void setupVault(VaultRaid vault) {
      vault.getEvents().add(VaultRaid.REPLACE_WITH_COW);
   }

   public static AggressiveCowEntity replaceVaultEntity(LivingEntity spawned, ServerWorld world) {
      AggressiveCowEntity override = (AggressiveCowEntity)ModEntities.AGGRESSIVE_COW.func_200721_a(world);
      AttributeModifierManager mgr = override.func_233645_dx_();

      for (ModifiableAttributeInstance instance : spawned.func_233645_dx_().func_233778_a_()) {
         if (mgr.func_233790_b_(instance.func_111123_a())) {
            override.func_110148_a(instance.func_111123_a()).func_111128_a(instance.func_111125_b());
         }
      }

      mgr.func_233779_a_(Attributes.field_233823_f_)
         .func_233769_c_(new AttributeModifier(DAMAGE_NERF_MULTIPLIER, "Scaling Damage Reduction", 0.2, Operation.MULTIPLY_TOTAL));
      if (spawned instanceof MobEntity) {
         for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack has = override.func_184582_a(slot);
            if (!has.func_190926_b()) {
               spawned.func_184201_a(slot, has.func_77946_l());
            } else {
               spawned.func_184201_a(slot, ItemStack.field_190927_a);
            }
         }
      }

      override.func_184211_a("replaced_entity");
      return override;
   }
}

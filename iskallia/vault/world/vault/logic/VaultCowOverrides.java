package iskallia.vault.world.vault.logic;

import iskallia.vault.entity.LegacyEntityScaler;
import iskallia.vault.entity.entity.AggressiveCowEntity;
import iskallia.vault.entity.entity.EtchingVendorEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.vault.VaultRaid;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultCowOverrides {
   private static final UUID DAMAGE_NERF_MULTIPLIER = UUID.fromString("384df991-f603-344c-a090-3693adfa984a");
   public static boolean forceSpecialVault = false;
   public static final String ENTITY_TAG = "replaced_entity";

   public static void setupVault(VaultRaid vault) {
      vault.getEvents().add(VaultRaid.REPLACE_WITH_COW);
   }

   @Nullable
   public static AggressiveCowEntity replaceVaultEntity(VaultRaid vault, LivingEntity spawned, ServerLevel world) {
      if (!(spawned instanceof Silverfish) && !(spawned instanceof EtchingVendorEntity) && !(spawned instanceof EternalEntity)) {
         LegacyEntityScaler.scaleVaultEntity(vault, spawned);
         AggressiveCowEntity override = (AggressiveCowEntity)ModEntities.AGGRESSIVE_COW.create(world);
         AttributeMap mgr = override.getAttributes();

         for (Attribute attr : ForgeRegistries.ATTRIBUTES) {
            if (spawned.getAttributes().hasAttribute(attr) && mgr.hasAttribute(attr)) {
               override.getAttribute(attr).setBaseValue(spawned.getAttributeValue(attr));
            }
         }

         mgr.getInstance(Attributes.ATTACK_DAMAGE)
            .addPermanentModifier(new AttributeModifier(DAMAGE_NERF_MULTIPLIER, "Scaling Damage Reduction", 0.4, Operation.MULTIPLY_TOTAL));
         if (spawned instanceof Mob) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
               ItemStack has = override.getItemBySlot(slot);
               if (!has.isEmpty()) {
                  spawned.setItemSlot(slot, has.copy());
               } else {
                  spawned.setItemSlot(slot, ItemStack.EMPTY);
               }
            }
         }

         override.addTag("replaced_entity");
         return override;
      } else {
         spawned.addTag("replaced_entity");
         return null;
      }
   }
}

package iskallia.vault.entity.eternal;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public interface EternalDataAccess {
   UUID getId();

   long getSeed();

   int getLevel();

   int getMaxLevel();

   String getName();

   boolean isAlive();

   boolean isAncient();

   Map<EquipmentSlotType, ItemStack> getEquipment();

   Map<Attribute, Float> getEntityAttributes();

   @Nullable
   String getAbilityName();

   default Random getSeededRand() {
      long seed = this.getSeed();
      seed ^= this.getId().getMostSignificantBits();
      seed ^= this.getId().getLeastSignificantBits();
      return new Random(seed);
   }
}

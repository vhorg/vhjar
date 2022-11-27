package iskallia.vault.entity.eternal;

import iskallia.vault.world.data.EternalsData;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;

public interface EternalDataAccess {
   UUID getId();

   long getSeed();

   int getLevel();

   int getMaxLevel();

   String getName();

   boolean isAlive();

   boolean isAncient();

   EternalsData.EternalVariant getVariant();

   boolean isUsingPlayerSkin();

   Map<EquipmentSlot, ItemStack> getEquipment();

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

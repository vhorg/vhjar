package iskallia.vault.world.legacy.raid;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.common.util.INBTSerializable;

public class ArenaScoreboard implements INBTSerializable<CompoundTag> {
   private final ArenaRaid raid;
   private Map<String, Float> damageMap = new HashMap<>();

   public ArenaScoreboard(ArenaRaid raid) {
      this.raid = raid;
   }

   public Map<String, Float> get() {
      return ImmutableMap.copyOf(this.damageMap);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag nameList = new ListTag();
      ListTag amountList = new ListTag();
      this.damageMap.forEach((name, amount) -> {
         nameList.add(StringTag.valueOf(name));
         amountList.add(FloatTag.valueOf(amount));
      });
      nbt.put("NameList", nameList);
      nbt.put("AmountList", amountList);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.damageMap.clear();
      ListTag nameList = nbt.getList("NameList", 8);
      ListTag amountList = nbt.getList("AmountList", 5);
      if (nameList.size() != amountList.size()) {
         throw new IllegalStateException("Map doesn't have the same amount of keys as values");
      } else {
         for (int i = 0; i < nameList.size(); i++) {
            this.damageMap.put(nameList.getString(i), amountList.getFloat(i));
         }
      }
   }
}

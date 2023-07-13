package iskallia.vault.entity.champion;

import net.minecraft.nbt.CompoundTag;

public class ChampionAffixBase implements IChampionAffix {
   private final String type;
   private final String name;

   public ChampionAffixBase(String type, String name) {
      this.type = type;
      this.name = name;
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag ret = new CompoundTag();
      ret.putString("type", this.type);
      ret.putString("name", this.name);
      return ret;
   }

   @Override
   public String getType() {
      return this.type;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public static String deserializeType(CompoundTag tag) {
      return tag.getString("type");
   }

   public static String deserializeName(CompoundTag tag) {
      return tag.getString("name");
   }
}

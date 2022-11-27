package iskallia.vault.world.vault.logic.objective.ancient;

import java.util.Objects;
import net.minecraft.nbt.CompoundTag;

public class AncientEternalReference {
   private final String name;

   public AncientEternalReference(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putString("name", this.name);
      return tag;
   }

   public static AncientEternalReference deserialize(CompoundTag tag) {
      return new AncientEternalReference(tag.getString("name"));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AncientEternalReference that = (AncientEternalReference)o;
         return Objects.equals(this.name, that.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name);
   }
}

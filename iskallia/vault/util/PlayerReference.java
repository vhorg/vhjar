package iskallia.vault.util;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerReference {
   private final UUID id;
   private final String name;

   public PlayerReference(Player player) {
      this(player.getUUID(), player.getGameProfile().getName());
   }

   public PlayerReference(UUID id, String name) {
      this.id = id;
      this.name = name;
   }

   public PlayerReference(CompoundTag tag) {
      this.id = tag.getUUID("id");
      this.name = tag.getString("name");
   }

   public UUID getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putUUID("id", this.id);
      tag.putString("name", this.name);
      return tag;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PlayerReference that = (PlayerReference)o;
         return Objects.equals(this.id, that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}

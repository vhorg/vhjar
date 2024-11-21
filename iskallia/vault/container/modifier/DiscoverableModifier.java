package iskallia.vault.container.modifier;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public record DiscoverableModifier(Item item, ResourceLocation modifierId, boolean discovered) {
   public static Optional<DiscoverableModifier> deserialize(CompoundTag tag) {
      String itemKey = tag.getString("item");
      Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
      if (item == null) {
         return Optional.empty();
      } else {
         boolean discovered = tag.contains("discovered") && tag.getBoolean("discovered");
         ResourceLocation modifier = new ResourceLocation(tag.getString("modifier"));
         return Optional.of(new DiscoverableModifier(item, modifier, discovered));
      }
   }

   public static DiscoverableModifier deserialize(FriendlyByteBuf buf) {
      return new DiscoverableModifier((Item)buf.readRegistryId(), buf.readResourceLocation(), buf.readBoolean());
   }

   public void serialize(CompoundTag tag) {
      tag.putString("item", this.item.getRegistryName().toString());
      tag.putString("modifier", this.modifierId.toString());
      tag.putBoolean("discovered", this.discovered);
   }

   public void serialize(FriendlyByteBuf buf) {
      buf.writeRegistryId(this.item);
      buf.writeResourceLocation(this.modifierId);
      buf.writeBoolean(this.discovered);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DiscoverableModifier modifier = (DiscoverableModifier)o;
         return Objects.equals(this.item, modifier.item) && Objects.equals(this.modifierId, modifier.modifierId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.item, this.modifierId);
   }
}

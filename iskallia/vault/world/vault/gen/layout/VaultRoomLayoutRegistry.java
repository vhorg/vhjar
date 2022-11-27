package iskallia.vault.world.vault.gen.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class VaultRoomLayoutRegistry {
   private static final Map<ResourceLocation, Supplier<VaultRoomLayoutGenerator>> layoutRegistry = new HashMap<>();

   public static void init() {
      layoutRegistry.put(SingularVaultRoomLayout.ID, SingularVaultRoomLayout::new);
      layoutRegistry.put(LineRoomLayout.ID, LineRoomLayout::new);
      layoutRegistry.put(DiamondRoomLayout.ID, DiamondRoomLayout::new);
      layoutRegistry.put(SquareRoomLayout.ID, SquareRoomLayout::new);
      layoutRegistry.put(CircleRoomLayout.ID, CircleRoomLayout::new);
      layoutRegistry.put(TriangleRoomLayout.ID, TriangleRoomLayout::new);
      layoutRegistry.put(SpiralRoomLayout.ID, SpiralRoomLayout::new);
      layoutRegistry.put(DebugVaultLayout.ID, DebugVaultLayout::new);
      layoutRegistry.put(DenseDiamondRoomLayout.ID, DenseDiamondRoomLayout::new);
      layoutRegistry.put(DenseSquareRoomLayout.ID, DenseSquareRoomLayout::new);
   }

   @Nullable
   public static VaultRoomLayoutGenerator getLayoutGenerator(ResourceLocation id) {
      return layoutRegistry.containsKey(id) ? layoutRegistry.get(id).get() : null;
   }

   @Nullable
   public static VaultRoomLayoutGenerator deserialize(CompoundTag tag) {
      if (!tag.contains("Id", 8)) {
         return null;
      } else {
         VaultRoomLayoutGenerator layout = getLayoutGenerator(new ResourceLocation(tag.getString("Id")));
         if (layout == null) {
            return null;
         } else {
            layout.deserialize(tag.getCompound("Data"));
            layout.generateLayout();
            return layout;
         }
      }
   }

   public static CompoundTag serialize(VaultRoomLayoutGenerator roomLayout) {
      CompoundTag layoutTag = new CompoundTag();
      layoutTag.putString("Id", roomLayout.getId().toString());
      layoutTag.put("Data", roomLayout.serialize());
      return layoutTag;
   }
}

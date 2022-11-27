package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.VaultMod;
import java.awt.geom.Point2D.Float;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

public class CircleRoomLayout extends ConnectedRoomGenerator {
   public static final ResourceLocation ID = VaultMod.id("circle");
   private int size;

   public CircleRoomLayout() {
      this(11);
   }

   public CircleRoomLayout(int size) {
      super(ID);
      this.size = size;
   }

   @Override
   public void setSize(int size) {
      this.size = size;
   }

   @Override
   public VaultRoomLayoutGenerator.Layout generateLayout() {
      VaultRoomLayoutGenerator.Layout layout = new VaultRoomLayoutGenerator.Layout();
      if (this.size % 2 == 0) {
         throw new IllegalArgumentException("Cannot generate vault circle shape with even size!");
      } else {
         this.calculateRooms(layout, this.size);
         this.connectRooms(layout, this.size);
         return layout;
      }
   }

   private void calculateRooms(VaultRoomLayoutGenerator.Layout layout, int size) {
      Float center = new Float(0.5F, 0.5F);
      int halfSize = size / 2;

      for (int x = -halfSize; x <= halfSize; x++) {
         for (int z = -halfSize; z <= halfSize; z++) {
            Float roomPos = new Float(x + 0.5F, z + 0.5F);
            if (center.distance(roomPos) <= halfSize) {
               layout.putRoom(new Vec3i(x, 0, z));
            }
         }
      }
   }
}

package iskallia.vault.world.vault.gen.layout;

import iskallia.vault.Vault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

public class SpiralRoomLayout extends VaultRoomLayoutGenerator {
   public static final ResourceLocation ID = Vault.id("spiral");
   private int size;

   public SpiralRoomLayout() {
      this(11);
   }

   public SpiralRoomLayout(int size) {
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
      int x = 0;
      int y = 0;
      int dx = 0;
      int dy = -1;
      VaultRoomLayoutGenerator.Room previousRoom = null;

      for (int i = 0; i < this.size * this.size; i++) {
         if (-this.size / 2 <= x && x <= this.size / 2 && -this.size / 2 <= y && y <= this.size / 2) {
            VaultRoomLayoutGenerator.Room room = new VaultRoomLayoutGenerator.Room(new Vector3i(x, 0, y));
            layout.putRoom(room);
            if (previousRoom != null) {
               layout.addTunnel(new VaultRoomLayoutGenerator.Tunnel(previousRoom, room));
            }

            previousRoom = room;
         }

         if (x == y || x < 0 && x == -y || x > 0 && x == 1 - y) {
            int temp = dx;
            dx = -dy;
            dy = temp;
         }

         x += dx;
         y += dy;
      }

      return layout;
   }

   @Override
   protected void deserialize(CompoundNBT tag) {
      super.deserialize(tag);
      if (tag.func_150297_b("size", 3)) {
         this.size = tag.func_74762_e("size");
      }
   }

   @Override
   protected CompoundNBT serialize() {
      CompoundNBT tag = super.serialize();
      tag.func_74768_a("size", this.size);
      return tag;
   }
}

package iskallia.vault.core.vault.pylon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

public abstract class TickingPylonBuff<C extends PylonBuff.Config<?>> extends PylonBuff<C> {
   protected int tick = 0;

   public TickingPylonBuff(C config) {
      super(config);
   }

   @Override
   public void onTick(MinecraftServer server) {
      this.tick++;
   }

   @Override
   public void write(CompoundTag object) {
      super.write(object);
      object.putInt("tick", this.tick);
   }

   @Override
   public void read(CompoundTag object) {
      super.read(object);
      this.tick = object.getInt("tick");
   }
}

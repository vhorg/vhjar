package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonObject;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.time.modifier.PylonExtension;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

public class TimePylonBuff extends PylonBuff<TimePylonBuff.Config> {
   public TimePylonBuff(TimePylonBuff.Config config) {
      super(config);
   }

   @Override
   public boolean isDone() {
      return true;
   }

   @Override
   public void onAdd(MinecraftServer server) {
      this.getPlayer(server)
         .ifPresent(
            player -> ServerVaults.get(player.level)
               .ifPresent(vault -> vault.ifPresent(Vault.CLOCK, clock -> clock.addModifier(new PylonExtension(player, this.config.ticks))))
         );
   }

   public static class Config extends PylonBuff.Config<TimePylonBuff> {
      private int ticks;

      public TimePylonBuff build() {
         return new TimePylonBuff(this);
      }

      @Override
      public void write(JsonObject object) {
         object.addProperty("type", "time");
         object.addProperty("ticks", this.ticks);
      }

      @Override
      public void read(JsonObject object) {
         this.ticks = object.get("ticks").getAsInt();
      }

      @Override
      public void write(CompoundTag object) {
         object.putString("type", "time");
         object.putFloat("ticks", this.ticks);
      }

      @Override
      public void read(CompoundTag object) {
         this.ticks = object.getInt("ticks");
      }
   }
}

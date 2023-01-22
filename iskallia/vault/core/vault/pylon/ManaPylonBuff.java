package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonObject;
import iskallia.vault.mana.Mana;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

public class ManaPylonBuff extends PylonBuff<ManaPylonBuff.Config> {
   public ManaPylonBuff(ManaPylonBuff.Config config) {
      super(config);
   }

   @Override
   public boolean isDone() {
      return true;
   }

   @Override
   public void onAdd(MinecraftServer server) {
      this.getPlayer(server).ifPresent(player -> {
         float current = Mana.get(player);
         float total = Mana.getMax(player);
         Mana.set(player, this.config.missingManaPercent * (total - current) + current);
      });
   }

   public static class Config extends PylonBuff.Config<ManaPylonBuff> {
      private float missingManaPercent;

      public ManaPylonBuff build() {
         return new ManaPylonBuff(this);
      }

      @Override
      public void write(JsonObject object) {
         object.addProperty("type", "mana");
         object.addProperty("missingManaPercent", this.missingManaPercent);
      }

      @Override
      public void read(JsonObject object) {
         this.missingManaPercent = object.get("missingManaPercent").getAsFloat();
      }

      @Override
      public void write(CompoundTag object) {
         object.putString("type", "mana");
         object.putFloat("missingManaPercent", this.missingManaPercent);
      }

      @Override
      public void read(CompoundTag object) {
         this.missingManaPercent = object.getFloat("missingManaPercent");
      }
   }
}

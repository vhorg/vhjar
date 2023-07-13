package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonObject;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

public class StatPylonBuff extends TickingPylonBuff<StatPylonBuff.Config> {
   public StatPylonBuff(StatPylonBuff.Config config) {
      super(config);
   }

   @Override
   public boolean isDone() {
      return super.isDone() || this.tick >= this.config.duration;
   }

   @Override
   public void initServer(MinecraftServer server) {
      CommonEvents.PLAYER_STAT.register(this.uuid, data -> {
         if (this.playerUuid.equals(data.getEntity().getUUID())) {
            if (data.getStat() == this.config.stat) {
               data.setValue(data.getValue() + this.config.addend);
            }
         }
      });
   }

   @Override
   public void releaseServer() {
      CommonEvents.PLAYER_STAT.release(this.uuid);
   }

   public static class Config extends PylonBuff.Config<StatPylonBuff> {
      private PlayerStat stat;
      private float addend;
      private int duration;

      @Override
      public int getDuration() {
         return this.duration;
      }

      public StatPylonBuff build() {
         return new StatPylonBuff(this);
      }

      @Override
      public void write(JsonObject object) {
         object.addProperty("type", "stat");
         object.addProperty("stat", this.stat.name());
         object.addProperty("addend", this.addend);
         object.addProperty("duration", this.duration);
      }

      @Override
      public void read(JsonObject object) {
         this.stat = Enum.valueOf(PlayerStat.class, object.get("stat").getAsString());
         this.addend = object.get("addend").getAsInt();
         this.duration = object.get("duration").getAsInt();
      }

      @Override
      public void write(CompoundTag object) {
         object.putString("type", "stat");
         object.putString("stat", this.stat.name());
         object.putFloat("addend", this.addend);
         object.putInt("duration", this.duration);
      }

      @Override
      public void read(CompoundTag object) {
         this.stat = Enum.valueOf(PlayerStat.class, object.getString("stat"));
         this.addend = object.getFloat("addend");
         this.duration = object.getInt("duration");
      }
   }
}

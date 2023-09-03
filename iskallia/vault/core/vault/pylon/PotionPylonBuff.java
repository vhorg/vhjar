package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonObject;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

public class PotionPylonBuff extends PylonBuff<PotionPylonBuff.Config> {
   public PotionPylonBuff(PotionPylonBuff.Config config) {
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
               .ifPresent(vault -> BottleItem.getActive(vault, player).ifPresent(bottle -> BottleItem.addCharges(bottle, this.config.charges)))
         );
   }

   public static class Config extends PylonBuff.Config<PotionPylonBuff> {
      private int charges;

      public PotionPylonBuff build() {
         return new PotionPylonBuff(this);
      }

      @Override
      protected void write(JsonObject object) {
         object.addProperty("type", "potion");
         object.addProperty("charges", this.charges);
      }

      @Override
      protected void read(JsonObject object) {
         this.charges = object.get("charges").getAsInt();
      }

      @Override
      protected void write(CompoundTag object) {
         object.putString("type", "potion");
         object.putInt("charges", this.charges);
      }

      @Override
      protected void read(CompoundTag object) {
         this.charges = object.getInt("charges");
      }
   }
}

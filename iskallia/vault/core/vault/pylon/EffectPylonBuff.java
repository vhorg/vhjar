package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonObject;
import iskallia.vault.core.event.CommonEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectPylonBuff extends TickingPylonBuff<EffectPylonBuff.Config> {
   public EffectPylonBuff(EffectPylonBuff.Config config) {
      super(config);
   }

   @Override
   public boolean isDone() {
      return super.isDone() || this.tick >= this.config.duration;
   }

   @Override
   public void initServer(MinecraftServer server) {
      CommonEvents.GRANTED_EFFECT.register(this.uuid, data -> {
         if (this.playerUuid.equals(data.getPlayer().getUUID())) {
            if (data.getFilter().test(this.config.effect)) {
               data.getEffects().addAmplifier(this.config.effect, this.config.amplifier);
            }
         }
      });
   }

   @Override
   public void releaseServer() {
      CommonEvents.GRANTED_EFFECT.release(this.uuid);
   }

   public static class Config extends PylonBuff.Config<EffectPylonBuff> {
      private MobEffect effect;
      private int amplifier;
      private int duration;

      @Override
      public int getDuration() {
         return this.duration;
      }

      public EffectPylonBuff build() {
         return new EffectPylonBuff(this);
      }

      @Override
      public void write(JsonObject object) {
         object.addProperty("type", "effect");
         object.addProperty("effect", this.effect.getRegistryName().toString());
         object.addProperty("amplifier", this.amplifier);
         object.addProperty("duration", this.duration);
      }

      @Override
      public void read(JsonObject object) {
         this.effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(object.get("effect").getAsString()));
         this.amplifier = object.get("amplifier").getAsInt();
         this.duration = object.get("duration").getAsInt();
      }

      @Override
      public void write(CompoundTag object) {
         object.putString("type", "effect");
         object.putString("effect", this.effect.getRegistryName().toString());
         object.putInt("amplifier", this.amplifier);
         object.putInt("duration", this.duration);
      }

      @Override
      public void read(CompoundTag object) {
         this.effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(object.getString("effect")));
         this.amplifier = object.getInt("amplifier");
         this.duration = object.getInt("duration");
      }
   }
}

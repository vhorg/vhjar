package iskallia.vault.core.vault.pylon;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributePylonBuff extends TickingPylonBuff<AttributePylonBuff.Config> {
   public AttributePylonBuff(AttributePylonBuff.Config config) {
      super(config);
   }

   @Override
   public boolean isDone() {
      return super.isDone() || this.tick >= this.config.duration;
   }

   @Override
   public void onTick(MinecraftServer server) {
      super.onTick(server);
      this.getPlayer(server).ifPresent(player -> {
         AttributeModifier modifier = new AttributeModifier(this.uuid, "Pylon Buff", this.config.amount, this.config.operation);
         AttributeInstance attribute = player.getAttribute(this.config.attribute);
         if (attribute != null && !attribute.hasModifier(modifier)) {
            attribute.addTransientModifier(modifier);
         }
      });
   }

   @Override
   public void onRemove(MinecraftServer server) {
      this.getPlayer(server).ifPresent(player -> {
         AttributeInstance attribute = player.getAttribute(this.config.attribute);
         if (attribute != null) {
            attribute.removeModifier(this.uuid);
         }
      });
   }

   public static class Config extends PylonBuff.Config<AttributePylonBuff> {
      private Attribute attribute;
      private double amount;
      private Operation operation;
      private int duration;

      @Override
      public int getDuration() {
         return this.duration;
      }

      public AttributePylonBuff build() {
         return new AttributePylonBuff(this);
      }

      @Override
      public void write(JsonObject object) {
         object.addProperty("type", "attribute");
         object.addProperty("attribute", this.attribute.getRegistryName().toString());
         object.addProperty("amount", this.amount);
         object.addProperty("operation", this.operation.name());
         object.addProperty("duration", this.duration);
      }

      @Override
      public void read(JsonObject object) {
         this.attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(object.get("attribute").getAsString()));
         this.amount = object.get("duration").getAsDouble();
         this.operation = Enum.valueOf(Operation.class, object.get("duration").getAsString());
         this.duration = object.get("duration").getAsInt();
      }

      @Override
      public void write(CompoundTag object) {
         object.putString("type", "attribute");
         object.putString("attribute", this.attribute.getRegistryName().toString());
         object.putDouble("amount", this.amount);
         object.putString("operation", this.operation.name());
         object.putInt("duration", this.duration);
      }

      @Override
      public void read(CompoundTag object) {
         this.attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(object.getString("attribute")));
         this.amount = object.getDouble("amount");
         this.operation = Enum.valueOf(Operation.class, object.getString("operation"));
         this.duration = object.getInt("duration");
      }
   }
}

package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicCircleLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ClassicCircleCrystalLayout extends ClassicInfiniteCrystalLayout {
   protected int radius;

   protected ClassicCircleCrystalLayout() {
   }

   public ClassicCircleCrystalLayout(int tunnelSpan, int radius) {
      super(tunnelSpan);
      this.radius = radius;
   }

   @Override
   public void configure(Vault vault) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicCircleLayout(this.tunnelSpan, this.radius));
         }
      });
   }

   @Override
   public Component getName() {
      return new TextComponent("Circle").withStyle(ChatFormatting.GREEN);
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putString("type", "circle");
      nbt.putInt("radius", this.radius);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.radius = nbt.getInt("radius");
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = super.serializeJson();
      object.addProperty("type", "circle");
      object.addProperty("radius", this.radius);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject object) {
      super.deserializeJson(object);
      this.radius = object.get("radius").getAsInt();
   }
}

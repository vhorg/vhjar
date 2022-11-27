package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicSpiralLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.Rotation;

public class ClassicSpiralCrystalLayout extends ClassicInfiniteCrystalLayout {
   protected int halfLength;
   protected Rotation rotation;

   protected ClassicSpiralCrystalLayout() {
   }

   public ClassicSpiralCrystalLayout(int tunnelSpan, int halfLength, Rotation rotation) {
      super(tunnelSpan);
      this.halfLength = halfLength;
      this.rotation = rotation;
   }

   @Override
   public void configure(Vault vault) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicSpiralLayout(this.tunnelSpan, this.halfLength, this.rotation));
         }
      });
   }

   @Override
   public Component getName() {
      return new TextComponent("Spiral").withStyle(ChatFormatting.BLUE);
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putString("type", "spiral");
      nbt.putInt("half_length", this.halfLength);
      nbt.putString("rotation", this.rotation.name());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.halfLength = nbt.getInt("half_length");
      this.rotation = Enum.valueOf(Rotation.class, nbt.getString("rotation"));
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = super.serializeJson();
      object.addProperty("type", "spiral");
      object.addProperty("half_length", this.halfLength);
      object.addProperty("rotation", this.rotation.name());
      return object;
   }

   @Override
   public void deserializeJson(JsonObject object) {
      super.deserializeJson(object);
      this.halfLength = object.get("half_length").getAsInt();
      this.rotation = (Rotation)Rotation.valueOf(Rotation.class, object.get("rotation").getAsString());
   }
}

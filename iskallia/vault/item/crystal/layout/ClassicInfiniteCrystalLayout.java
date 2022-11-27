package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ClassicInfiniteCrystalLayout extends CrystalLayout {
   protected int tunnelSpan;

   protected ClassicInfiniteCrystalLayout() {
   }

   public ClassicInfiniteCrystalLayout(int tunnelSpan) {
      this.tunnelSpan = tunnelSpan;
   }

   @Override
   public void configure(Vault vault) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicInfiniteLayout(this.tunnelSpan));
         }
      });
   }

   @Override
   public Component getName() {
      return new TextComponent("Infinite").withStyle(ChatFormatting.RED);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "infinite");
      nbt.putInt("tunnel_span", this.tunnelSpan);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.tunnelSpan = nbt.getInt("tunnel_span");
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "infinite");
      object.addProperty("tunnel_span", this.tunnelSpan);
      return object;
   }

   @Override
   public void deserializeJson(JsonObject object) {
      this.tunnelSpan = object.get("tunnel_span").getAsInt();
   }
}

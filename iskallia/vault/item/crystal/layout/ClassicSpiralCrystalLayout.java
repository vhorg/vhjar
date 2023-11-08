package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicSpiralLayout;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Rotation;

public class ClassicSpiralCrystalLayout extends ClassicInfiniteCrystalLayout {
   protected int halfLength;
   protected Rotation rotation;

   public ClassicSpiralCrystalLayout() {
   }

   public ClassicSpiralCrystalLayout(int tunnelSpan, int halfLength, Rotation rotation) {
      super(tunnelSpan);
      this.halfLength = halfLength;
      this.rotation = rotation;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicSpiralLayout(this.tunnelSpan, this.halfLength, this.rotation));
         }
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Layout: ").append(new TextComponent("Spiral").withStyle(ChatFormatting.BLUE)));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         nbt.putInt("half_length", this.halfLength);
         nbt.putString("rotation", this.rotation.name());
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.halfLength = nbt.getInt("half_length");
      this.rotation = Enum.valueOf(Rotation.class, nbt.getString("rotation"));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         json.addProperty("half_length", this.halfLength);
         json.addProperty("rotation", this.rotation.name());
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.halfLength = json.get("half_length").getAsInt();
      this.rotation = (Rotation)Rotation.valueOf(Rotation.class, json.get("rotation").getAsString());
   }
}

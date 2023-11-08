package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicCircleLayout;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class ClassicCircleCrystalLayout extends ClassicInfiniteCrystalLayout {
   protected int radius;

   public ClassicCircleCrystalLayout() {
   }

   public ClassicCircleCrystalLayout(int tunnelSpan, int radius) {
      super(tunnelSpan);
      this.radius = radius;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicCircleLayout(this.tunnelSpan, this.radius));
         }
      });
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Layout: ").append(new TextComponent("Circle").withStyle(ChatFormatting.GREEN)));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         nbt.putInt("radius", this.radius);
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = nbt.getInt("radius");
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         json.addProperty("radius", this.radius);
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = json.get("radius").getAsInt();
   }
}

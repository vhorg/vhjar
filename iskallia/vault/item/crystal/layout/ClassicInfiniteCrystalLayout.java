package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class ClassicInfiniteCrystalLayout extends CrystalLayout {
   protected int tunnelSpan;

   public ClassicInfiniteCrystalLayout() {
   }

   public ClassicInfiniteCrystalLayout(int tunnelSpan) {
      this.tunnelSpan = tunnelSpan;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.getOptional(Vault.WORLD).map(world -> world.get(WorldManager.GENERATOR)).ifPresent(generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicInfiniteLayout(this.tunnelSpan));
         }
      });
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Layout: ").append(new TextComponent("Infinite").withStyle(ChatFormatting.RED)));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("tunnel_span", this.tunnelSpan);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.tunnelSpan = nbt.getInt("tunnel_span");
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      json.addProperty("tunnel_span", this.tunnelSpan);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.tunnelSpan = json.get("tunnel_span").getAsInt();
   }
}

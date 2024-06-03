package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPresetLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.template.data.DirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.item.crystal.layout.preset.PoolTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class HeraldCrystalLayout extends ClassicInfiniteCrystalLayout {
   public HeraldCrystalLayout() {
      super(1);
   }

   public StructurePreset getPreset() {
      return new StructurePreset()
         .put(RegionPos.ORIGIN, new PoolTemplatePreset(new TemplatePool().addLeaf(new DirectTemplateEntry(VaultMod.id("vault/starts/herald")), 1.0)));
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      if (vault.has(Vault.WORLD)) {
         vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
            if (generator instanceof GridGenerator grid) {
               grid.set(GridGenerator.CELL_X, Integer.valueOf(512));
               grid.set(GridGenerator.CELL_Z, Integer.valueOf(512));
               grid.set(GridGenerator.LAYOUT, new ClassicPresetLayout(this.tunnelSpan, this.getPreset()).set(VaultLayout.FILL_AIR));
            }
         });
      }
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Layout: ").append(new TextComponent("Preset").withStyle(Style.EMPTY.withColor(13882323))));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      return Optional.of(nbt);
   }

   @Override
   public void readNbt(CompoundTag nbt) {
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      return Optional.of(json);
   }

   @Override
   public void readJson(JsonObject json) {
   }
}

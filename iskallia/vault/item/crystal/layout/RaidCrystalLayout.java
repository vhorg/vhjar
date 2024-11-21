package iskallia.vault.item.crystal.layout;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPresetLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.item.crystal.layout.preset.PoolTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;

public class RaidCrystalLayout extends ClassicInfiniteCrystalLayout {
   public RaidCrystalLayout() {
      super(1);
   }

   public StructurePreset getPreset() {
      return new StructurePreset()
         .put(RegionPos.ORIGIN, new PoolTemplatePreset(VaultLayout.PieceType.START_SOUTH))
         .put(RegionPos.ORIGIN.add(0, 1), new PoolTemplatePreset(VaultLayout.PieceType.TUNNEL_Z))
         .put(RegionPos.ORIGIN.add(0, 2), new PoolTemplatePreset(VaultLayout.PieceType.ROOM));
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      vault.get(Vault.WORLD).set(WorldManager.FACING, Direction.SOUTH);
      vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
         if (generator instanceof GridGenerator grid) {
            grid.set(GridGenerator.LAYOUT, new ClassicPresetLayout(1, this.getPreset()));
         }
      });
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

package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.template.data.TemplatePool;
import iskallia.vault.item.crystal.layout.preset.PoolTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import iskallia.vault.item.crystal.layout.preset.TemplatePreset;
import net.minecraft.server.TickTask;
import net.minecraft.world.level.ServerLevelAccessor;

public class ClassicPresetLayout extends ClassicInfiniteLayout {
   public static final SupplierKey<GridLayout> KEY = SupplierKey.of("classic_preset_vault", GridLayout.class).with(Version.v1_19, ClassicPresetLayout::new);
   public static final FieldRegistry FIELDS = ClassicInfiniteLayout.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<StructurePreset> PRESET = FieldKey.of("preset", StructurePreset.class)
      .with(Version.v1_19, Adapters.of(StructurePreset::new, false), DISK.all())
      .register(FIELDS);

   protected ClassicPresetLayout() {
   }

   public ClassicPresetLayout(int tunnelSpan, StructurePreset preset) {
      super(tunnelSpan);
      this.set(PRESET, preset);
   }

   @Override
   public SupplierKey<GridLayout> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public VaultLayout.PieceType getType(Vault vault, RegionPos region) {
      return region.getX() == 0 && region.getZ() == 0 ? VaultLayout.PieceType.ROOM : super.getType(vault, region);
   }

   @Override
   public TemplatePool getTemplatePool(VaultLayout.PieceType type, Vault vault, RegionPos region, RandomSource random) {
      return this.get(PRESET)
         .get(region)
         .map(preset -> preset instanceof PoolTemplatePreset poolTemplatePreset ? poolTemplatePreset.getPool() : null)
         .orElse(null);
   }

   public void append(Vault vault, ServerLevelAccessor world, RegionPos region, TemplatePreset preset) {
      this.get(PRESET).put(region, preset);
      VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
      if (generator instanceof GridGenerator gridGenerator && world.getServer() != null) {
         world.getServer().tell(new TickTask(world.getServer().getTickCount() + 1, () -> gridGenerator.generate(vault, world, region)));
      }
   }

   public boolean hasGenerated(RegionPos region) {
      return this.get(PRESET).contains(region);
   }
}

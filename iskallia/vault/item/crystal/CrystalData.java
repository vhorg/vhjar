package iskallia.vault.item.crystal;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.item.crystal.layout.ArchitectCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicCircleCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicInfiniteCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicPolygonCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicSpiralCrystalLayout;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.layout.NullCrystalLayout;
import iskallia.vault.item.crystal.model.AugmentCrystalModel;
import iskallia.vault.item.crystal.model.ChaosCrystalModel;
import iskallia.vault.item.crystal.model.CompoundCrystalModel;
import iskallia.vault.item.crystal.model.CrystalModel;
import iskallia.vault.item.crystal.model.GrayscaleCrystalModel;
import iskallia.vault.item.crystal.model.NullCrystalModel;
import iskallia.vault.item.crystal.model.RainbowCrystalModel;
import iskallia.vault.item.crystal.model.RawCrystalModel;
import iskallia.vault.item.crystal.objective.BossCrystalObjective;
import iskallia.vault.item.crystal.objective.CakeCrystalObjective;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.item.crystal.objective.ElixirCrystalObjective;
import iskallia.vault.item.crystal.objective.EmptyCrystalObjective;
import iskallia.vault.item.crystal.objective.MonolithCrystalObjective;
import iskallia.vault.item.crystal.objective.NullCrystalObjective;
import iskallia.vault.item.crystal.objective.ScavengerCrystalObjective;
import iskallia.vault.item.crystal.objective.SpeedrunCrystalObjective;
import iskallia.vault.item.crystal.theme.CrystalTheme;
import iskallia.vault.item.crystal.theme.NullCrystalTheme;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.item.crystal.theme.ValueCrystalTheme;
import iskallia.vault.item.crystal.time.CrystalTime;
import iskallia.vault.item.crystal.time.NullCrystalTime;
import iskallia.vault.item.crystal.time.PoolCrystalTime;
import iskallia.vault.item.crystal.time.ValueCrystalTime;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public class CrystalData implements ISerializable<CompoundTag, JsonObject> {
   public static final String NBT_KEY = "CrystalData";
   public static EnumAdapter<CrystalVersion> VERSION = Adapters.ofEnum(CrystalVersion.class, EnumAdapter.Mode.ORDINAL);
   public static TypeSupplierAdapter<CrystalModel> MODEL = new TypeSupplierAdapter<CrystalModel>("type", false)
      .<TypeSupplierAdapter<CompoundCrystalModel>>register("null", NullCrystalModel.class, () -> NullCrystalModel.INSTANCE)
      .<TypeSupplierAdapter<RainbowCrystalModel>>register("compound", CompoundCrystalModel.class, CompoundCrystalModel::new)
      .<TypeSupplierAdapter<RawCrystalModel>>register("rainbow", RainbowCrystalModel.class, RainbowCrystalModel::new)
      .<TypeSupplierAdapter<ChaosCrystalModel>>register("raw", RawCrystalModel.class, RawCrystalModel::new)
      .<TypeSupplierAdapter<GrayscaleCrystalModel>>register("chaos", ChaosCrystalModel.class, ChaosCrystalModel::new)
      .<TypeSupplierAdapter<AugmentCrystalModel>>register("grayscale", GrayscaleCrystalModel.class, GrayscaleCrystalModel::new)
      .register("augment", AugmentCrystalModel.class, AugmentCrystalModel::new);
   public static TypeSupplierAdapter<CrystalTheme> THEME = new TypeSupplierAdapter<CrystalTheme>("type", false)
      .<TypeSupplierAdapter<ValueCrystalTheme>>register("null", NullCrystalTheme.class, () -> NullCrystalTheme.INSTANCE)
      .<TypeSupplierAdapter<PoolCrystalTheme>>register("value", ValueCrystalTheme.class, ValueCrystalTheme::new)
      .register("pool", PoolCrystalTheme.class, PoolCrystalTheme::new);
   public static TypeSupplierAdapter<CrystalLayout> LAYOUT = new TypeSupplierAdapter<CrystalLayout>("type", false)
      .<TypeSupplierAdapter<ClassicInfiniteCrystalLayout>>register("null", NullCrystalLayout.class, () -> NullCrystalLayout.INSTANCE)
      .<TypeSupplierAdapter<ClassicCircleCrystalLayout>>register("infinite", ClassicInfiniteCrystalLayout.class, ClassicInfiniteCrystalLayout::new)
      .<TypeSupplierAdapter<ClassicPolygonCrystalLayout>>register("circle", ClassicCircleCrystalLayout.class, ClassicCircleCrystalLayout::new)
      .<TypeSupplierAdapter<ClassicSpiralCrystalLayout>>register("polygon", ClassicPolygonCrystalLayout.class, ClassicPolygonCrystalLayout::new)
      .<TypeSupplierAdapter<ArchitectCrystalLayout>>register("spiral", ClassicSpiralCrystalLayout.class, ClassicSpiralCrystalLayout::new)
      .register("architect", ArchitectCrystalLayout.class, ArchitectCrystalLayout::new);
   public static TypeSupplierAdapter<CrystalObjective> OBJECTIVE = new TypeSupplierAdapter<CrystalObjective>("type", false)
      .<TypeSupplierAdapter<EmptyCrystalObjective>>register("null", NullCrystalObjective.class, () -> NullCrystalObjective.INSTANCE)
      .<TypeSupplierAdapter<BossCrystalObjective>>register("empty", EmptyCrystalObjective.class, EmptyCrystalObjective::new)
      .<TypeSupplierAdapter<CakeCrystalObjective>>register("boss", BossCrystalObjective.class, BossCrystalObjective::new)
      .<TypeSupplierAdapter<ScavengerCrystalObjective>>register("cake", CakeCrystalObjective.class, CakeCrystalObjective::new)
      .<TypeSupplierAdapter<SpeedrunCrystalObjective>>register("scavenger", ScavengerCrystalObjective.class, ScavengerCrystalObjective::new)
      .<TypeSupplierAdapter<MonolithCrystalObjective>>register("speedrun", SpeedrunCrystalObjective.class, SpeedrunCrystalObjective::new)
      .<TypeSupplierAdapter<ElixirCrystalObjective>>register("monolith", MonolithCrystalObjective.class, MonolithCrystalObjective::new)
      .register("elixir", ElixirCrystalObjective.class, ElixirCrystalObjective::new);
   public static TypeSupplierAdapter<CrystalTime> TIME = new TypeSupplierAdapter<CrystalTime>("type", false)
      .<TypeSupplierAdapter<ValueCrystalTime>>register("null", NullCrystalTime.class, () -> NullCrystalTime.INSTANCE)
      .<TypeSupplierAdapter<PoolCrystalTime>>register("value", ValueCrystalTime.class, ValueCrystalTime::new)
      .register("pool", PoolCrystalTime.class, PoolCrystalTime::new);
   private CrystalVersion version = CrystalVersion.latest();
   private UUID vaultId = null;
   private int level = 0;
   private CrystalModel model = NullCrystalModel.INSTANCE;
   private CrystalTheme theme = NullCrystalTheme.INSTANCE;
   private CrystalLayout layout = NullCrystalLayout.INSTANCE;
   private CrystalObjective objective = NullCrystalObjective.INSTANCE;
   private CrystalTime time = NullCrystalTime.INSTANCE;
   private CrystalModifiers modifiers = new CrystalModifiers();
   private boolean unmodifiable = false;
   private float instability = 0.0F;

   protected CrystalData() {
   }

   protected CrystalData(@Nullable CompoundTag nbt) {
      if (nbt != null) {
         this.readNbt(nbt);
      }
   }

   public static CrystalData empty() {
      return new CrystalData();
   }

   public static CrystalData read(ItemStack stack) {
      return new CrystalData(stack.getTagElement("CrystalData"));
   }

   public CrystalData write(ItemStack stack) {
      this.writeNbt().ifPresent(nbt -> stack.getOrCreateTag().put("CrystalData", nbt));
      return this;
   }

   public UUID getVaultId() {
      return this.vaultId;
   }

   public int getLevel() {
      return this.level;
   }

   public CrystalModel getModel() {
      return this.model;
   }

   public CrystalTheme getTheme() {
      return this.theme;
   }

   public CrystalLayout getLayout() {
      return this.layout;
   }

   public CrystalObjective getObjective() {
      return this.objective;
   }

   public CrystalTime getTime() {
      return this.time;
   }

   public CrystalModifiers getModifiers() {
      return this.modifiers;
   }

   public boolean isUnmodifiable() {
      return this.unmodifiable;
   }

   public float getInstability() {
      return this.instability;
   }

   public void setVaultId(UUID vaultId) {
      this.vaultId = vaultId;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public void setModel(CrystalModel model) {
      this.model = model;
   }

   public void setTheme(CrystalTheme theme) {
      this.theme = theme;
   }

   public void setLayout(CrystalLayout layout) {
      this.layout = layout;
   }

   public void setObjective(CrystalObjective objective) {
      this.objective = objective;
   }

   public void setTime(CrystalTime time) {
      this.time = time;
   }

   public void setModifiers(CrystalModifiers modifiers) {
      this.modifiers = modifiers;
   }

   public void setUnmodifiable(boolean unmodifiable) {
      this.unmodifiable = unmodifiable;
   }

   public void setInstability(float instability) {
      this.instability = instability;
   }

   public boolean canGenerateCatalystFragments() {
      return this.modifiers.hasRandomModifiers();
   }

   public boolean canGenerateRunes() {
      return !(this.layout instanceof ArchitectCrystalLayout);
   }

   public boolean addModifierByCrafting(VaultModifierStack modifierStack, boolean preventsRandomModifiers, CrystalData.Simulate simulate) {
      return this.modifiers.addByCrafting(this, modifierStack, preventsRandomModifiers, simulate);
   }

   public void configure(Vault vault, RandomSource random) {
      this.time.configure(vault, random);
      this.layout.configure(vault, random);
      this.theme.configure(vault, random);
      this.objective.configure(vault, random);
      this.modifiers.configure(vault, random);
   }

   public void addText(List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Level: ").append(new TextComponent(this.getLevel() + "").setStyle(Style.EMPTY.withColor(11583738))));
      this.objective.addText(tooltip, flag);
      this.theme.addText(tooltip, flag);
      this.layout.addText(tooltip, flag);
      this.time.addText(tooltip, flag);
      if (this.instability > 0.0F) {
         TextComponent instabilityComponent = new TextComponent("%.1f%%".formatted(this.instability * 100.0F));
         instabilityComponent.setStyle(Style.EMPTY.withColor(this.getInstabilityTextColor(Math.round(this.instability * 100.0F))));
         tooltip.add(new TextComponent("Instability: ").append(instabilityComponent));
      }

      if (this.unmodifiable) {
         tooltip.add(new TextComponent("Unmodifiable").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(11027010))));
      }

      this.modifiers.addText(tooltip, flag);
   }

   private TextColor getInstabilityTextColor(float instability) {
      float threshold = 0.5F;
      float hueDarkGreen = 0.3334F;
      float hueGold = 0.1111F;
      float hue;
      float saturation;
      float value;
      if (instability <= 0.5F) {
         float p = instability / 0.5F;
         hue = (1.0F - p) * 0.3334F + p * 0.1111F;
         saturation = 1.0F;
         value = (1.0F - p) * 0.8F + p;
      } else {
         float p = (instability - 0.5F) / 0.5F;
         hue = (1.0F - p) * 0.1111F;
         saturation = 1.0F - p + p * 0.8F;
         value = 1.0F - p + p * 0.8F;
      }

      return TextColor.fromRgb(Color.HSBtoRGB(hue, saturation, value));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      VERSION.writeNbt(this.version).ifPresent(version -> nbt.put("Version", version));
      Adapters.UUID.writeNbt(this.vaultId).ifPresent(vaultId -> nbt.put("VaultId", vaultId));
      Adapters.INT.writeNbt(Integer.valueOf(this.level)).ifPresent(level -> nbt.put("Level", level));
      MODEL.writeNbt(this.model).ifPresent(model -> nbt.put("Model", model));
      THEME.writeNbt(this.theme).ifPresent(theme -> nbt.put("Theme", theme));
      LAYOUT.writeNbt(this.layout).ifPresent(layout -> nbt.put("Layout", layout));
      OBJECTIVE.writeNbt(this.objective).ifPresent(objective -> nbt.put("Objective", objective));
      TIME.writeNbt(this.time).ifPresent(time -> nbt.put("Time", time));
      CrystalModifiers.ADAPTER.writeNbt(this.modifiers).ifPresent(modifiers -> nbt.put("Modifiers", modifiers));
      Adapters.BOOLEAN.writeNbt(this.unmodifiable).ifPresent(exhausted -> nbt.put("Exhausted", exhausted));
      Adapters.FLOAT.writeNbt(Float.valueOf(this.instability)).ifPresent(instability -> nbt.put("Instability", instability));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      nbt = CrystalVersion.upgrade(nbt.copy());
      this.version = VERSION.readNbt(nbt.get("Version")).orElse(CrystalVersion.LEGACY);
      this.vaultId = Adapters.UUID.readNbt(nbt.get("VaultId")).orElse(null);
      this.level = Adapters.INT.readNbt(nbt.get("Level")).orElse(0);
      this.model = MODEL.readNbt(nbt.getCompound("Model")).orElse(NullCrystalModel.INSTANCE);
      this.theme = THEME.readNbt(nbt.getCompound("Theme")).orElse(NullCrystalTheme.INSTANCE);
      this.layout = LAYOUT.readNbt(nbt.getCompound("Layout")).orElse(NullCrystalLayout.INSTANCE);
      this.objective = OBJECTIVE.readNbt(nbt.getCompound("Objective")).orElse(NullCrystalObjective.INSTANCE);
      this.time = TIME.readNbt(nbt.getCompound("Time")).orElse(NullCrystalTime.INSTANCE);
      this.modifiers = CrystalModifiers.ADAPTER.readNbt(nbt.getCompound("Modifiers")).orElse(new CrystalModifiers());
      this.unmodifiable = Adapters.BOOLEAN.readNbt(nbt.get("Exhausted")).orElse(false);
      this.instability = Adapters.FLOAT.readNbt(nbt.get("Instability")).orElse(0.0F);
   }

   public CrystalData copy() {
      return new CrystalData(this.writeNbt().orElse(null));
   }

   public static enum Simulate {
      TRUE,
      FALSE;
   }
}

package iskallia.vault.item.crystal;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.item.crystal.layout.ArchitectCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicCircleCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicInfiniteCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicPolygonCrystalLayout;
import iskallia.vault.item.crystal.layout.ClassicSpiralCrystalLayout;
import iskallia.vault.item.crystal.layout.CompoundCrystalLayout;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.layout.HeraldCrystalLayout;
import iskallia.vault.item.crystal.layout.NullCrystalLayout;
import iskallia.vault.item.crystal.layout.ParadoxCrystalLayout;
import iskallia.vault.item.crystal.model.AugmentCrystalModel;
import iskallia.vault.item.crystal.model.ChaosCrystalModel;
import iskallia.vault.item.crystal.model.CompoundCrystalModel;
import iskallia.vault.item.crystal.model.CrystalModel;
import iskallia.vault.item.crystal.model.GrayscaleCrystalModel;
import iskallia.vault.item.crystal.model.NullCrystalModel;
import iskallia.vault.item.crystal.model.RainbowCrystalModel;
import iskallia.vault.item.crystal.model.RawCrystalModel;
import iskallia.vault.item.crystal.modifiers.CrystalModifiers;
import iskallia.vault.item.crystal.modifiers.DefaultCrystalModifiers;
import iskallia.vault.item.crystal.modifiers.ParadoxCrystalModifiers;
import iskallia.vault.item.crystal.objective.AscensionCrystalObjective;
import iskallia.vault.item.crystal.objective.BingoCrystalObjective;
import iskallia.vault.item.crystal.objective.BossCrystalObjective;
import iskallia.vault.item.crystal.objective.CakeCrystalObjective;
import iskallia.vault.item.crystal.objective.CompoundCrystalObjective;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.item.crystal.objective.ElixirCrystalObjective;
import iskallia.vault.item.crystal.objective.EmptyCrystalObjective;
import iskallia.vault.item.crystal.objective.HeraldCrystalObjective;
import iskallia.vault.item.crystal.objective.MonolithCrystalObjective;
import iskallia.vault.item.crystal.objective.NullCrystalObjective;
import iskallia.vault.item.crystal.objective.OfferingBossCrystalObjective;
import iskallia.vault.item.crystal.objective.ParadoxCrystalObjective;
import iskallia.vault.item.crystal.objective.PoolCrystalObjective;
import iskallia.vault.item.crystal.objective.ScavengerCrystalObjective;
import iskallia.vault.item.crystal.objective.SpeedrunCrystalObjective;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.crystal.properties.CrystalProperties;
import iskallia.vault.item.crystal.properties.InstabilityCrystalProperties;
import iskallia.vault.item.crystal.theme.CrystalTheme;
import iskallia.vault.item.crystal.theme.NullCrystalTheme;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.item.crystal.theme.ValueCrystalTheme;
import iskallia.vault.item.crystal.time.CrystalTime;
import iskallia.vault.item.crystal.time.NullCrystalTime;
import iskallia.vault.item.crystal.time.PoolCrystalTime;
import iskallia.vault.item.crystal.time.ValueCrystalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public class CrystalData extends CrystalEntry implements ISerializable<CompoundTag, JsonObject> {
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
      .<TypeSupplierAdapter<ParadoxCrystalLayout>>register("architect", ArchitectCrystalLayout.class, ArchitectCrystalLayout::new)
      .<TypeSupplierAdapter<HeraldCrystalLayout>>register("paradox", ParadoxCrystalLayout.class, ParadoxCrystalLayout::new)
      .<TypeSupplierAdapter<CompoundCrystalLayout>>register("herald", HeraldCrystalLayout.class, HeraldCrystalLayout::new)
      .register("compound", CompoundCrystalLayout.class, CompoundCrystalLayout::new);
   public static TypeSupplierAdapter<CrystalObjective> OBJECTIVE = new TypeSupplierAdapter<CrystalObjective>("type", false)
      .<TypeSupplierAdapter<PoolCrystalObjective>>register("null", NullCrystalObjective.class, () -> NullCrystalObjective.INSTANCE)
      .<TypeSupplierAdapter<EmptyCrystalObjective>>register("pool", PoolCrystalObjective.class, PoolCrystalObjective::new)
      .<TypeSupplierAdapter<BossCrystalObjective>>register("empty", EmptyCrystalObjective.class, EmptyCrystalObjective::new)
      .<TypeSupplierAdapter<CakeCrystalObjective>>register("boss", BossCrystalObjective.class, BossCrystalObjective::new)
      .<TypeSupplierAdapter<ScavengerCrystalObjective>>register("cake", CakeCrystalObjective.class, CakeCrystalObjective::new)
      .<TypeSupplierAdapter<SpeedrunCrystalObjective>>register("scavenger", ScavengerCrystalObjective.class, ScavengerCrystalObjective::new)
      .<TypeSupplierAdapter<MonolithCrystalObjective>>register("speedrun", SpeedrunCrystalObjective.class, SpeedrunCrystalObjective::new)
      .<TypeSupplierAdapter<ElixirCrystalObjective>>register("monolith", MonolithCrystalObjective.class, MonolithCrystalObjective::new)
      .<TypeSupplierAdapter<ParadoxCrystalObjective>>register("elixir", ElixirCrystalObjective.class, ElixirCrystalObjective::new)
      .<TypeSupplierAdapter<HeraldCrystalObjective>>register("paradox", ParadoxCrystalObjective.class, ParadoxCrystalObjective::new)
      .<TypeSupplierAdapter<CompoundCrystalObjective>>register("herald", HeraldCrystalObjective.class, HeraldCrystalObjective::new)
      .<TypeSupplierAdapter<AscensionCrystalObjective>>register("compound", CompoundCrystalObjective.class, CompoundCrystalObjective::new)
      .<TypeSupplierAdapter<BingoCrystalObjective>>register("ascension", AscensionCrystalObjective.class, AscensionCrystalObjective::new)
      .<TypeSupplierAdapter<OfferingBossCrystalObjective>>register("bingo", BingoCrystalObjective.class, BingoCrystalObjective::new)
      .register("offering_boss", OfferingBossCrystalObjective.class, OfferingBossCrystalObjective::new);
   public static TypeSupplierAdapter<CrystalTime> TIME = new TypeSupplierAdapter<CrystalTime>("type", false)
      .<TypeSupplierAdapter<ValueCrystalTime>>register("null", NullCrystalTime.class, () -> NullCrystalTime.INSTANCE)
      .<TypeSupplierAdapter<PoolCrystalTime>>register("value", ValueCrystalTime.class, ValueCrystalTime::new)
      .register("pool", PoolCrystalTime.class, PoolCrystalTime::new);
   public static TypeSupplierAdapter<CrystalModifiers> MODIFIERS = new TypeSupplierAdapter<DefaultCrystalModifiers>("type", false)
      .<TypeSupplierAdapter<ParadoxCrystalModifiers>>register("default", DefaultCrystalModifiers.class, DefaultCrystalModifiers::new)
      .register("paradox", ParadoxCrystalModifiers.class, ParadoxCrystalModifiers::new);
   public static TypeSupplierAdapter<CrystalProperties> PROPERTIES = new TypeSupplierAdapter<InstabilityCrystalProperties>("type", false)
      .<TypeSupplierAdapter<CapacityCrystalProperties>>register("instability", InstabilityCrystalProperties.class, InstabilityCrystalProperties::new)
      .register("capacity", CapacityCrystalProperties.class, CapacityCrystalProperties::new);
   private CrystalVersion version = CrystalVersion.latest();
   private CrystalModel model = NullCrystalModel.INSTANCE;
   private CrystalTheme theme = NullCrystalTheme.INSTANCE;
   private CrystalLayout layout = NullCrystalLayout.INSTANCE;
   private CrystalObjective objective = NullCrystalObjective.INSTANCE;
   private CrystalTime time = NullCrystalTime.INSTANCE;
   private CrystalModifiers modifiers = new DefaultCrystalModifiers();
   private CrystalProperties properties = new CapacityCrystalProperties();

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

   public static void run(ItemStack stack, Consumer<CrystalData> consumer) {
      CrystalData data = read(stack);
      consumer.accept(data);
      data.write(stack);
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

   public CrystalProperties getProperties() {
      return this.properties;
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

   public void setProperties(CrystalProperties properties) {
      this.properties = properties;
   }

   public boolean canGenerateCatalystFragments() {
      return this.modifiers.hasRandomModifiers();
   }

   public boolean canGenerateRunes() {
      return !(this.layout instanceof ArchitectCrystalLayout);
   }

   public boolean addModifierByCrafting(VaultModifierStack modifierStack, boolean preventsRandomModifiers, boolean simulate) {
      return this.modifiers.addByCrafting(this, modifierStack, preventsRandomModifiers, simulate);
   }

   @Override
   public Collection<CrystalEntry> getChildren() {
      return Arrays.asList(this.properties, this.objective, this.layout, this.theme, this.time, this.modifiers);
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      this.objective.addText(tooltip, minIndex, flag, time);
      this.theme.addText(tooltip, minIndex, flag, time);
      this.layout.addText(tooltip, minIndex, flag, time);
      this.time.addText(tooltip, minIndex, flag, time);
      this.properties.addText(tooltip, minIndex, flag, time);
      this.modifiers.addText(tooltip, minIndex, flag, time);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      VERSION.writeNbt(this.version).ifPresent(version -> nbt.put("Version", version));
      MODEL.writeNbt(this.model).ifPresent(model -> nbt.put("Model", model));
      THEME.writeNbt(this.theme).ifPresent(theme -> nbt.put("Theme", theme));
      LAYOUT.writeNbt(this.layout).ifPresent(layout -> nbt.put("Layout", layout));
      OBJECTIVE.writeNbt(this.objective).ifPresent(objective -> nbt.put("Objective", objective));
      TIME.writeNbt(this.time).ifPresent(time -> nbt.put("Time", time));
      MODIFIERS.writeNbt(this.modifiers).ifPresent(modifiers -> nbt.put("Modifiers", modifiers));
      PROPERTIES.writeNbt(this.properties).ifPresent(modifiers -> nbt.put("Properties", modifiers));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      nbt = CrystalVersion.upgrade(nbt.copy());
      this.version = VERSION.readNbt(nbt.get("Version")).orElse(CrystalVersion.LEGACY);
      this.model = MODEL.readNbt(nbt.getCompound("Model")).orElse(NullCrystalModel.INSTANCE);
      this.theme = THEME.readNbt(nbt.getCompound("Theme")).orElse(NullCrystalTheme.INSTANCE);
      this.layout = LAYOUT.readNbt(nbt.getCompound("Layout")).orElse(NullCrystalLayout.INSTANCE);
      this.objective = OBJECTIVE.readNbt(nbt.getCompound("Objective")).orElse(NullCrystalObjective.INSTANCE);
      this.time = TIME.readNbt(nbt.getCompound("Time")).orElse(NullCrystalTime.INSTANCE);
      this.modifiers = MODIFIERS.readNbt(nbt.getCompound("Modifiers")).orElse(new DefaultCrystalModifiers());
      this.properties = PROPERTIES.readNbt(nbt.getCompound("Properties")).orElse(new CapacityCrystalProperties());
   }

   public CrystalData copy() {
      return new CrystalData(this.writeNbt().orElse(null));
   }
}

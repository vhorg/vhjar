package iskallia.vault.item.crystal;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.item.crystal.theme.CrystalTheme;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.builder.ClassicVaultBuilder;
import iskallia.vault.world.vault.builder.CoopVaultBuilder;
import iskallia.vault.world.vault.builder.FinalLobbyBuilder;
import iskallia.vault.world.vault.builder.RaffleVaultBuilder;
import iskallia.vault.world.vault.builder.TroveVaultBuilder;
import iskallia.vault.world.vault.builder.VaultRaidBuilder;
import iskallia.vault.world.vault.gen.VaultRoomNames;
import iskallia.vault.world.vault.logic.VaultLogic;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

public class CrystalData implements INBTSerializable<CompoundTag> {
   public static final CrystalData EMPTY = new CrystalData.EmptyCrystalData();
   private CompoundTag delegate = new CompoundTag();
   protected UUID vaultId;
   protected int level;
   protected CrystalTheme theme;
   protected CrystalLayout layout;
   protected CrystalObjective objective;
   protected CrystalModifiers modifiers = new CrystalModifiers();
   protected CrystalData.Model model = CrystalData.Model.DEFAULT;
   protected CrystalData.Type type = CrystalData.Type.CLASSIC;
   protected boolean preventsRandomModifiers = false;
   protected boolean canBeModified = true;
   protected boolean canTriggerInfluences = true;
   protected boolean canGenerateTreasureRooms = true;
   protected List<String> guaranteedRoomFilters = new ArrayList<>();
   protected CrystalData.EchoData echoData;
   protected FrameData frameData;
   protected int instabilityCounter;
   protected boolean clarity;

   public CrystalData() {
   }

   public CrystalData(CompoundTag delegate) {
      this.delegate = delegate;
      this.deserializeNBT(this.delegate.getCompound("CrystalData"));
   }

   public CrystalData(ItemStack delegate) {
      if (delegate != null) {
         this.delegate = delegate.getOrCreateTag();
         this.deserializeNBT(this.delegate.getCompound("CrystalData"));
      }
   }

   public CompoundTag getDelegate() {
      return this.delegate;
   }

   public void updateDelegate() {
      if (this.delegate != null) {
         this.delegate.put("CrystalData", this.serializeNBT());
      }
   }

   public UUID getVaultId() {
      return this.vaultId;
   }

   public CrystalData setVaultId(UUID uuid) {
      if (!uuid.equals(this.vaultId)) {
         this.vaultId = uuid;
         this.updateDelegate();
      }

      return this;
   }

   public int getLevel() {
      return this.level;
   }

   public CrystalData setLevel(int level) {
      if (level != this.level) {
         this.level = level;
         this.updateDelegate();
      }

      return this;
   }

   public CrystalTheme getTheme() {
      return this.theme;
   }

   public CrystalData setTheme(CrystalTheme theme) {
      if (!theme.equals(this.theme)) {
         this.theme = theme;
         this.updateDelegate();
      }

      return this;
   }

   public CrystalLayout getLayout() {
      return this.layout;
   }

   public CrystalData setLayout(CrystalLayout layout) {
      this.layout = layout;
      this.updateDelegate();
      return this;
   }

   public CrystalObjective getObjective() {
      return this.objective;
   }

   public CrystalData setObjective(CrystalObjective objective) {
      if (!objective.equals(this.objective)) {
         this.objective = objective;
         this.updateDelegate();
      }

      return this;
   }

   public CrystalData.Model getModel() {
      return this.model;
   }

   public void setModel(CrystalData.Model model) {
      if (this.model != model) {
         this.model = model;
         this.updateDelegate();
      }
   }

   public CrystalData.Type getType() {
      return this.type;
   }

   public void setType(CrystalData.Type type) {
      if (this.type != type) {
         this.type = type;
         this.updateDelegate();
      }
   }

   public boolean canModifyWithCrafting() {
      if (!this.canBeModified()) {
         return false;
      } else {
         List<ResourceLocation> modifierNames = this.getModifiers().stream().map(VaultModifierStack::getModifierId).toList();
         return modifierNames.contains(VaultMod.id("afterlife")) ? false : this.getType().canCraft();
      }
   }

   protected boolean canAddModifier(VaultModifierStack modifierStack) {
      return this.canBeModified();
   }

   public boolean addModifiersByCrafting(List<VaultModifierStack> modifierStackList, CrystalData.Simulate simulate) {
      for (VaultModifierStack modifierStack : modifierStackList) {
         if (!this.addModifierByCrafting(modifierStack, true, CrystalData.Simulate.TRUE)) {
            return false;
         }
      }

      if (simulate == CrystalData.Simulate.FALSE) {
         for (VaultModifierStack modifierStackx : modifierStackList) {
            this.addModifierByCrafting(modifierStackx, true, simulate);
         }
      }

      return true;
   }

   public boolean addModifierByCrafting(VaultModifierStack modifierStack, boolean preventsRandomModifiers, CrystalData.Simulate simulate) {
      if (this.canModifyWithCrafting() && this.canAddModifier(modifierStack)) {
         if (simulate == CrystalData.Simulate.FALSE) {
            if (preventsRandomModifiers) {
               this.setPreventsRandomModifiers(true);
            }

            this.instabilityCounter++;
            this.addModifier(modifierStack);
         }

         return true;
      } else {
         return false;
      }
   }

   public void addModifier(VaultModifierStack modifierStack) {
      if (modifierStack.isEmpty()) {
         VaultMod.LOGGER.error("Attempted to add Empty modifier to crystal. If you see this stacktrace, please share it with the devs.", new Exception());
      } else {
         boolean found = false;
         ResourceLocation modifierId = modifierStack.getModifierId();

         for (VaultModifierStack modifier : this.modifiers) {
            if (modifier.getModifierId().equals(modifierId)) {
               modifier.grow(modifierStack.getSize());
               found = true;
               break;
            }
         }

         if (!found) {
            this.modifiers.add(modifierStack.copy());
         }

         this.sortModifiers();
         this.updateDelegate();
      }
   }

   private void sortModifiers() {
      this.modifiers.sort(Comparator.comparing(VaultModifierStack::getSize).reversed());
   }

   public void setInstabilityCounter(int value) {
      this.instabilityCounter = Math.max(0, value);
      this.updateDelegate();
   }

   public int getInstability() {
      if (ModConfigs.VAULT_CRYSTAL == null) {
         return 0;
      } else {
         int craftsBeforeInstability = ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.craftsBeforeInstability;
         float instabilityPerCraft = ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.instabilityPerCraft;
         float instabilityCap = ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.instabilityCap;
         int instability = this.instabilityCounter - craftsBeforeInstability;
         return instability < 1 ? 0 : (int)(Math.min(instability * instabilityPerCraft, instabilityCap) * 100.0F);
      }
   }

   private TextColor getInstabilityTextColor(int instability) {
      float threshold = ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.instabilityCap * 0.5F;
      float instabilityScaled = instability * 0.01F;
      float hueDarkGreen = 0.3334F;
      float hueGold = 0.1111F;
      float hue;
      float saturation;
      float value;
      if (instabilityScaled <= threshold) {
         float p = instabilityScaled / threshold;
         hue = (1.0F - p) * 0.3334F + p * 0.1111F;
         saturation = 1.0F;
         value = (1.0F - p) * 0.8F + p;
      } else {
         float p = (instabilityScaled - threshold) / threshold;
         hue = (1.0F - p) * 0.1111F;
         saturation = 1.0F - p + p * 0.8F;
         value = 1.0F - p + p * 0.8F;
      }

      return TextColor.fromRgb(Color.HSBtoRGB(hue, saturation, value));
   }

   public List<VaultModifierStack> getModifiers() {
      return Collections.unmodifiableList(this.modifiers);
   }

   public void setModifiers(CrystalModifiers modifiers) {
      this.modifiers.clear();
      this.modifiers.addAll(modifiers);
      this.sortModifiers();
      this.updateDelegate();
   }

   public void clearModifiers() {
      this.modifiers.clear();
      this.updateDelegate();
   }

   public boolean canAddRoom(String roomKey) {
      return true;
   }

   public void addGuaranteedRoom(String roomKey) {
      this.guaranteedRoomFilters.add(roomKey);
      this.updateDelegate();
   }

   public List<String> getGuaranteedRoomFilters() {
      return Collections.unmodifiableList(this.guaranteedRoomFilters);
   }

   public boolean preventsRandomModifiers() {
      return !this.canBeModified() ? true : this.preventsRandomModifiers || !this.getType().canGenerateRandomModifiers();
   }

   public void setPreventsRandomModifiers(boolean preventsRandomModifiers) {
      this.preventsRandomModifiers = preventsRandomModifiers;
      this.updateDelegate();
   }

   public boolean canTriggerInfluences() {
      return this.canTriggerInfluences;
   }

   public void setCanTriggerInfluences(boolean canTriggerInfluences) {
      this.canTriggerInfluences = canTriggerInfluences;
      this.updateDelegate();
   }

   public boolean canGenerateTreasureRooms() {
      return this.canGenerateTreasureRooms;
   }

   public void setCanGenerateTreasureRooms(boolean canGenerateTreasureRooms) {
      this.canGenerateTreasureRooms = canGenerateTreasureRooms;
      this.updateDelegate();
   }

   public boolean canBeModified() {
      return this.canBeModified;
   }

   public void setModifiable(boolean modifiable) {
      this.canBeModified = modifiable;
      this.updateDelegate();
   }

   public int getCurseCount() {
      return this.modifiers
         .stream()
         .filter(vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()))
         .map(VaultModifierStack::getSize)
         .reduce(0, Integer::sum);
   }

   public boolean isCursed() {
      return this.getCurseCount() > 0;
   }

   public void setClarity(boolean clarity) {
      this.clarity = clarity;
      this.updateDelegate();
   }

   public boolean hasClarity() {
      return this.clarity;
   }

   public void removeAllCurses() {
      this.modifiers.removeIf(vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()));
      this.updateDelegate();
   }

   public void removeRandomCurse(Random random) {
      List<VaultModifierStack> curseList = this.modifiers
         .stream()
         .filter(vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()))
         .toList();
      if (!curseList.isEmpty()) {
         VaultModifierStack modifierStack = curseList.get(random.nextInt(curseList.size()));
         if (modifierStack.shrink(1).isEmpty()) {
            this.modifiers.remove(modifierStack);
         }

         this.updateDelegate();
      }
   }

   public void apply(VaultRaid vault) {
      vault.getModifiers().addPermanentModifiers(this.modifiers);
   }

   public VaultRaid.Builder createVault(ServerLevel world, ServerPlayer player) {
      return this.getType().getVaultBuilder().initializeBuilder(world, player, this);
   }

   public static boolean shouldForceCowVault(CrystalData data) {
      List<ResourceLocation> requiredModifiers = Arrays.asList(VaultMod.id("hoard"), VaultMod.id("hunger"), VaultMod.id("raging"));
      List<VaultModifierStack> existingModifiers = data.getModifiers();
      return existingModifiers.size() == 3 && existingModifiers.stream().allMatch(modifier -> requiredModifiers.contains(modifier.getModifierId()));
   }

   public CrystalData.EchoData getEchoData() {
      if (this.echoData == null) {
         this.echoData = new CrystalData.EchoData(0);
      }

      return this.echoData;
   }

   public FrameData getFrameData() {
      return this.frameData;
   }

   public int addEchoGems(int amount) {
      int remainder = this.getEchoData().addEchoGems(amount);
      this.updateDelegate();
      return remainder;
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(Level world, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(new TextComponent("Level: ").append(new TextComponent(this.getLevel() + "").setStyle(Style.EMPTY.withColor(11583738))));
      CrystalData.Type crystalType = this.getType();
      if (crystalType.showTypePrefix()) {
      }

      if (this.objective == null) {
         tooltip.add(new TextComponent("Objective: ???").withStyle(ChatFormatting.GRAY));
      } else {
         this.objective.addText(tooltip, flag);
      }

      if (this.theme != null && !(this.theme instanceof PoolCrystalTheme)) {
         this.theme.addText(tooltip, flag);
      } else {
         tooltip.add(new TextComponent("Theme: ???").withStyle(ChatFormatting.GRAY));
      }

      if (this.layout == null) {
         tooltip.add(new TextComponent("Layout: ???").withStyle(ChatFormatting.GRAY));
      } else {
         this.layout.addText(tooltip, flag);
      }

      Map<String, Integer> collapsedFilters = new HashMap<>();

      for (String roomFilter : this.guaranteedRoomFilters) {
         int count = collapsedFilters.getOrDefault(roomFilter, 0);
         collapsedFilters.put(roomFilter, ++count);
      }

      collapsedFilters.forEach(
         (roomFilter, count) -> {
            Component roomName = VaultRoomNames.getName(roomFilter);
            if (roomName != null) {
               String roomStr = count > 1 ? "Rooms" : "Room";
               Component txt = new TextComponent("- Has ")
                  .withStyle(ChatFormatting.GRAY)
                  .append(new TextComponent(String.valueOf(count)).withStyle(ChatFormatting.GOLD))
                  .append(" ")
                  .append(roomName)
                  .append(new TextComponent(" " + roomStr).withStyle(ChatFormatting.GRAY));
               tooltip.add(txt);
            }
         }
      );
      int instability = this.getInstability();
      if (instability > 0) {
         TextComponent instabilityComponent = new TextComponent(instability + "%");
         instabilityComponent.setStyle(Style.EMPTY.withColor(this.getInstabilityTextColor(instability)));
         tooltip.add(new TextComponent("Instability: ").append(instabilityComponent));
      }

      if (this.getEchoData().getEchoCount() > 0) {
         int count = this.getEchoData().getEchoCount();
         tooltip.add(new TextComponent("Echoed").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(2491465))));
         if (Screen.hasShiftDown()) {
            Component description = new TextComponent("  " + count + "% Echo Rate").withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(description);
         }
      }

      if (!this.canBeModified()) {
         tooltip.add(new TextComponent("Exhausted").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(3084959))));
         if (Screen.hasShiftDown()) {
            Component description = new TextComponent("  Crystal can not be modified.").withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(description);
         }
      }

      if (this.delegate.getBoolean("Cloned")) {
         tooltip.add(new TextComponent("Cloned").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(2491465))));
         if (Screen.hasShiftDown()) {
            Component description = new TextComponent("  Crystal has been cloned with an Echoed Crystal.").withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(description);
         }
      }

      if (this.hasClarity()) {
         tooltip.add(new TextComponent("Clarity").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(4973509))));
         if (Screen.hasShiftDown()) {
            Component description = new TextComponent("  All curses on this crystal are revealed.").withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(description);
         }
      }

      int curseCount = this.getCurseCount();
      if (curseCount > 0) {
         Style style = Style.EMPTY.withColor(ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.curseColor);
         if (this.hasClarity()) {
            this.addCatalystModifierInformation(
               vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()),
               new TextComponent("Cursed").withStyle(style),
               tooltip
            );
         } else {
            MutableComponent component = new TextComponent("Cursed ").withStyle(style);
            tooltip.add(component.append("☠".repeat(curseCount)));
         }
      }

      this.addCatalystModifierInformation(
         vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isGood(vaultModifierStack.getModifierId()),
         new TextComponent("Positive Modifiers").withStyle(ChatFormatting.GREEN),
         tooltip
      );
      this.addCatalystModifierInformation(
         vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isBad(vaultModifierStack.getModifierId()),
         new TextComponent("Negative Modifiers").withStyle(ChatFormatting.RED),
         tooltip
      );
      this.addNonCatalystModifierInformation(
         vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isUnlisted(vaultModifierStack.getModifierId()),
         new TextComponent("Other Modifiers").withStyle(ChatFormatting.WHITE),
         tooltip
      );
   }

   private void addNonCatalystModifierInformation(Predicate<VaultModifierStack> filter, Component headerComponent, List<Component> tooltip) {
      List<VaultModifierStack> modifierList = this.modifiers.stream().filter(filter).toList();
      if (!modifierList.isEmpty()) {
         tooltip.add(headerComponent);

         for (VaultModifierStack modifierStack : modifierList) {
            VaultModifier<?> vaultModifier = modifierStack.getModifier();
            TextComponent modifierName = new TextComponent(vaultModifier.getDisplayNameFormatted(modifierStack.getSize()));
            modifierName.setStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor()));
            tooltip.add(new TextComponent("  ").append(modifierName));
            if (Screen.hasShiftDown()) {
               Component description = new TextComponent("  " + vaultModifier.getDisplayDescriptionFormatted(modifierStack.getSize()))
                  .withStyle(ChatFormatting.DARK_GRAY);
               tooltip.add(description);
            }
         }
      }
   }

   private void addCatalystModifierInformation(Predicate<VaultModifierStack> filter, Component headerComponent, List<Component> tooltip) {
      List<VaultModifierStack> modifierList = this.modifiers.stream().filter(filter).toList();
      if (!modifierList.isEmpty()) {
         tooltip.add(headerComponent);

         for (VaultModifierStack modifierStack : modifierList) {
            VaultModifier<?> vaultModifier = modifierStack.getModifier();
            TextComponent modifierName = new TextComponent(vaultModifier.getDisplayNameFormatted(modifierStack.getSize()));
            modifierName.setStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor()));
            Component stackSize = new TextComponent("%dx".formatted(modifierStack.getSize())).withStyle(ChatFormatting.GRAY);
            tooltip.add(new TextComponent("  ").withStyle(ChatFormatting.GRAY).append(stackSize).append(" ").append(modifierName));
            if (Screen.hasShiftDown()) {
               Component description = new TextComponent("  " + vaultModifier.getDisplayDescriptionFormatted(modifierStack.getSize()))
                  .withStyle(ChatFormatting.DARK_GRAY);
               tooltip.add(description);
            }
         }
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      if (this.vaultId != null) {
         nbt.putString("VaultId", this.vaultId.toString());
      }

      nbt.putInt("Level", this.level);
      if (this.theme != null) {
         nbt.put("Theme", this.theme.serializeNBT());
      }

      if (this.layout != null) {
         nbt.put("Layout", this.layout.serializeNBT());
      }

      if (this.objective != null) {
         nbt.put("Objective", this.objective.serializeNBT());
      }

      nbt.put("Modifiers", this.modifiers.serializeNBT());
      nbt.putByte("Model", this.model.serializedId);
      nbt.putString("Type", this.type.name());
      nbt.putBoolean("preventsRandomModifiers", this.preventsRandomModifiers);
      nbt.putBoolean("canBeModified", this.canBeModified);
      nbt.putBoolean("canTriggerInfluences", this.canTriggerInfluences);
      nbt.putBoolean("canGenerateTreasureRooms", this.canGenerateTreasureRooms);
      nbt.put("echoData", this.getEchoData().toNBT());
      ListTag roomList = new ListTag();
      this.guaranteedRoomFilters.forEach(roomKey -> roomList.add(StringTag.valueOf(roomKey)));
      nbt.put("rooms", roomList);
      if (this.frameData != null) {
         nbt.put("Frame", this.frameData.serializeNBT());
      }

      nbt.putInt("instabilityCounter", this.instabilityCounter);
      nbt.putBoolean("clarity", this.clarity);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.vaultId = nbt.contains("VaultId", 8) ? UUID.fromString(nbt.getString("VaultId")) : null;
      this.level = nbt.getInt("Level");
      this.theme = nbt.contains("Theme", 10) ? CrystalTheme.fromNBT(nbt.getCompound("Theme")) : null;
      this.layout = nbt.contains("Layout", 10) ? CrystalLayout.fromNBT(nbt.getCompound("Layout")) : null;
      this.objective = nbt.contains("Objective", 10) ? CrystalObjective.fromNBT(nbt.getCompound("Objective")) : null;
      this.modifiers.deserializeNBT(nbt.getList("Modifiers", 10));
      this.sortModifiers();
      this.model = nbt.contains("Model", 1) ? CrystalData.Model.fromSerializedId(nbt.getByte("Model")) : CrystalData.Model.DEFAULT;
      this.type = nbt.contains("Type", 8) ? Enum.valueOf(CrystalData.Type.class, nbt.getString("Type")) : CrystalData.Type.CLASSIC;
      if (this.type == CrystalData.Type.COOP) {
         this.type = CrystalData.Type.CLASSIC;
      }

      this.preventsRandomModifiers = nbt.contains("preventsRandomModifiers", 1) ? nbt.getBoolean("preventsRandomModifiers") : !this.modifiers.isEmpty();
      this.canBeModified = !nbt.contains("canBeModified", 1) || nbt.getBoolean("canBeModified");
      this.canTriggerInfluences = !nbt.contains("canTriggerInfluences", 1) || nbt.getBoolean("canTriggerInfluences");
      this.canGenerateTreasureRooms = !nbt.contains("canGenerateTreasureRooms", 1) || nbt.getBoolean("canGenerateTreasureRooms");
      if (nbt.contains("echoData")) {
         this.echoData = CrystalData.EchoData.fromNBT(nbt.getCompound("echoData"));
      }

      ListTag roomList = nbt.getList("rooms", 8);
      roomList.forEach(inbt -> this.guaranteedRoomFilters.add(this.migrateRoomName(inbt.getAsString())));
      this.frameData = FrameData.fromNBT(nbt.getCompound("Frame"));
      this.instabilityCounter = nbt.getInt("instabilityCounter");
      this.clarity = nbt.getBoolean("clarity");
   }

   private String migrateRoomName(String roomName) {
      if (roomName.equalsIgnoreCase("contest")) {
         roomName = "contest_tree";
      }

      return roomName;
   }

   public CrystalData copy() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("CrystalData", this.serializeNBT());
      return new CrystalData(nbt);
   }

   public static class EchoData {
      int echoCount;

      public EchoData(int echoCount) {
         this.echoCount = echoCount;
      }

      public int getEchoCount() {
         return this.echoCount;
      }

      public int addEchoGems(int amount) {
         if (this.echoCount >= 100) {
            return amount;
         } else {
            for (int i = amount; i > 0; i--) {
               if (this.echoCount >= 100) {
                  return i;
               }

               if (MathUtilities.randomFloat(0.0F, 1.0F) < this.getEchoSuccessRate()) {
                  this.echoCount++;
               }
            }

            return 0;
         }
      }

      public float getCloneSuccessRate() {
         return this.echoCount / 100.0F;
      }

      public float getEchoSuccessRate() {
         return (100 - this.echoCount) / 100.0F;
      }

      public CompoundTag toNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("echoCount", this.echoCount);
         return nbt;
      }

      public static CrystalData.EchoData fromNBT(CompoundTag nbt) {
         return new CrystalData.EchoData(nbt.getInt("echoCount"));
      }
   }

   private static class EmptyCrystalData extends CrystalData {
      @Override
      public boolean addModifierByCrafting(VaultModifierStack modifierStack, boolean preventsRandomModifiers, CrystalData.Simulate simulate) {
         return false;
      }

      @Override
      protected boolean canAddModifier(VaultModifierStack id) {
         return false;
      }

      @Override
      public boolean preventsRandomModifiers() {
         return false;
      }

      @Override
      public void setType(CrystalData.Type type) {
      }

      @Override
      public VaultRaid.Builder createVault(ServerLevel world, ServerPlayer player) {
         return null;
      }
   }

   public static enum Model {
      DEFAULT((byte)0),
      RAW((byte)1);

      private final byte serializedId;
      private static Map<Byte, CrystalData.Model> MAP = Arrays.stream(values())
         .collect(Collectors.toMap(CrystalData.Model::getSerializedId, m -> (CrystalData.Model)m));

      private Model(byte serializedId) {
         this.serializedId = serializedId;
      }

      public byte getSerializedId() {
         return this.serializedId;
      }

      public static CrystalData.Model fromSerializedId(byte serializedId) {
         return MAP.getOrDefault(serializedId, DEFAULT);
      }
   }

   public static enum Simulate {
      TRUE,
      FALSE;
   }

   public static enum Type {
      CLASSIC(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Normal"),
      RAFFLE(VaultLogic.RAFFLE, RaffleVaultBuilder.getInstance(), "Raffle"),
      COOP(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Cooperative"),
      TROVE(VaultLogic.CLASSIC, TroveVaultBuilder.getInstance(), "Vault Trove", ChatFormatting.GOLD),
      BOSS_BENEVOLENT_PREP("Velara's Sacrifice", PlayerFavourData.VaultGodType.BENEVOLENT.getChatColor()),
      BOSS_BENEVOLENT("Velara's Demand", PlayerFavourData.VaultGodType.BENEVOLENT.getChatColor()),
      BOSS_OMNISCIENT("Tenos' Oblivion", PlayerFavourData.VaultGodType.OMNISCIENT.getChatColor()),
      BOSS_TIMEKEEPER("Wendarr's Transience", PlayerFavourData.VaultGodType.TIMEKEEPER.getChatColor()),
      BOSS_MALEVOLENCE("Idona's Wrath", PlayerFavourData.VaultGodType.MALEVOLENT.getChatColor()),
      FINAL_LOBBY(VaultLogic.FINAL_LOBBY, FinalLobbyBuilder.getInstance(), "Final Vault", ChatFormatting.DARK_PURPLE),
      FINAL_VELARA(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Final Velara Challenge", ChatFormatting.GREEN),
      FINAL_TENOS(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Final Tenos Challenge", ChatFormatting.AQUA),
      FINAL_WENDARR(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Final Wendarr Challenge", ChatFormatting.GOLD),
      FINAL_IDONA(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Final Idona Challenge", ChatFormatting.RED);

      private final VaultLogic logic;
      private final VaultRaidBuilder vaultBuilder;
      private final String name;
      private final ChatFormatting color;

      private Type(String name, ChatFormatting color) {
         this(VaultLogic.CLASSIC, ClassicVaultBuilder.getInstance(), name, color);
      }

      private Type(VaultLogic logic, VaultRaidBuilder vaultBuilder, String name) {
         this(logic, vaultBuilder, name, ChatFormatting.GOLD);
      }

      private Type(VaultLogic logic, VaultRaidBuilder vaultBuilder, String name, ChatFormatting color) {
         this.logic = logic;
         this.vaultBuilder = vaultBuilder;
         this.name = name;
         this.color = color;
      }

      public boolean canCraft() {
         return this == CLASSIC || this == COOP;
      }

      public boolean showTypePrefix() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
      }

      public boolean showObjective() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
      }

      public boolean visibleInCreative() {
         return this == RAFFLE || this == COOP || this == TROVE || this == FINAL_LOBBY;
      }

      public boolean canBeCowVault() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
      }

      public boolean canGenerateRandomModifiers() {
         return this == CLASSIC
            || this == RAFFLE
            || this == COOP
            || this == FINAL_VELARA
            || this == FINAL_IDONA
            || this == FINAL_WENDARR
            || this == FINAL_TENOS;
      }

      public boolean canTriggerInfluences() {
         return this == CLASSIC || this == COOP;
      }

      public VaultLogic getLogic() {
         return this.logic;
      }

      public VaultRaidBuilder getVaultBuilder() {
         return this.vaultBuilder;
      }

      public Component getDisplayName() {
         return new TextComponent(this.name).withStyle(this.color);
      }
   }
}

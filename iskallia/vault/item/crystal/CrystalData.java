package iskallia.vault.item.crystal;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.builder.ClassicVaultBuilder;
import iskallia.vault.world.vault.builder.CoopVaultBuilder;
import iskallia.vault.world.vault.builder.RaffleVaultBuilder;
import iskallia.vault.world.vault.builder.TroveVaultBuilder;
import iskallia.vault.world.vault.builder.VaultRaidBuilder;
import iskallia.vault.world.vault.logic.VaultLogic;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.modifier.VaultModifier;
import iskallia.vault.world.vault.modifier.VaultModifiers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.StringUtils;

public class CrystalData implements INBTSerializable<CompoundNBT> {
   public static final CrystalData EMPTY = new CrystalData.EmptyCrystalData();
   private CompoundNBT delegate = new CompoundNBT();
   protected CrystalData.Type type = CrystalData.Type.CLASSIC;
   protected String playerBossName = "";
   protected List<CrystalData.Modifier> modifiers = new ArrayList<>();
   protected boolean preventsRandomModifiers = false;
   protected ResourceLocation selectedObjective = null;
   protected int targetObjectiveCount = -1;
   protected boolean canBeModified = true;

   public CrystalData() {
   }

   public CrystalData(ItemStack delegate) {
      if (delegate != null) {
         this.delegate = delegate.func_196082_o();
         this.deserializeNBT(this.delegate.func_74775_l("CrystalData"));
      }
   }

   public CompoundNBT getDelegate() {
      return this.delegate;
   }

   public void updateDelegate() {
      if (this.delegate != null) {
         this.delegate.func_218657_a("CrystalData", this.serializeNBT());
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

   public String getPlayerBossName() {
      return this.playerBossName;
   }

   public void setPlayerBossName(String playerBossName) {
      boolean nameChanged = !StringUtils.equalsIgnoreCase(this.playerBossName, playerBossName);
      this.playerBossName = playerBossName;
      if (nameChanged) {
         this.updateDelegate();
      }

      if (!playerBossName.isEmpty()) {
         this.setType(CrystalData.Type.RAFFLE);
         this.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      } else if (this.getType() == CrystalData.Type.RAFFLE) {
         this.setType(CrystalData.Type.CLASSIC);
      }
   }

   public boolean canCraftCatalysts() {
      if (!this.canBeModified()) {
         return false;
      } else {
         List<String> modifierNames = this.getModifiers().stream().map(CrystalData.Modifier::getModifierName).collect(Collectors.toList());
         return modifierNames.contains("Afterlife") ? false : this.getType().canCraftModifiers();
      }
   }

   public boolean canAddModifier(String name, CrystalData.Modifier.Operation operation) {
      return this.canBeModified() && this.getModifiers().stream().noneMatch(mod -> name.equals(mod.name));
   }

   public boolean addCatalystModifier(String name, boolean preventsRandomModifiers, CrystalData.Modifier.Operation operation) {
      if (!this.canAddModifier(name, operation)) {
         return false;
      } else {
         this.addModifier(name, operation);
         if (preventsRandomModifiers) {
            this.setPreventsRandomModifiers(true);
         }

         return true;
      }
   }

   public void addModifier(String name) {
      this.addModifier(name, CrystalData.Modifier.Operation.ADD);
   }

   private void addModifier(String name, CrystalData.Modifier.Operation operation) {
      this.modifiers.add(new CrystalData.Modifier(name, operation));
      this.updateDelegate();
   }

   public List<CrystalData.Modifier> getModifiers() {
      return Collections.unmodifiableList(this.modifiers);
   }

   public boolean preventsRandomModifiers() {
      return !this.canBeModified() ? true : this.preventsRandomModifiers || !this.getType().canGenerateRandomModifiers();
   }

   public void setPreventsRandomModifiers(boolean preventsRandomModifiers) {
      this.preventsRandomModifiers = preventsRandomModifiers;
      this.updateDelegate();
   }

   public boolean canBeModified() {
      return this.canBeModified;
   }

   public void setModifiable(boolean modifiable) {
      this.canBeModified = modifiable;
      this.updateDelegate();
   }

   public void setSelectedObjective(ResourceLocation selectedObjective) {
      if (!Objects.equals(this.selectedObjective, selectedObjective)) {
         this.selectedObjective = selectedObjective;
         this.updateDelegate();
      }
   }

   @Nullable
   public ResourceLocation getSelectedObjective() {
      return this.selectedObjective;
   }

   public void setTargetObjectiveCount(int targetObjectiveCount) {
      this.targetObjectiveCount = targetObjectiveCount;
      this.updateDelegate();
   }

   public int getTargetObjectiveCount() {
      return this.targetObjectiveCount;
   }

   public void apply(VaultRaid vault, Random random) {
      this.modifiers.forEach(modifier -> modifier.apply(vault.getModifiers(), random));
   }

   public VaultRaid.Builder createVault(ServerWorld world, ServerPlayerEntity player) {
      return this.getType().getVaultBuilder().initializeBuilder(world, player, this);
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      CrystalData.Type crystalType = this.getType();
      if (crystalType.showTypePrefix()) {
         tooltip.add(new StringTextComponent("Type: ").func_230529_a_(this.getType().getDisplayName()));
      } else {
         tooltip.add(this.getType().getDisplayName());
      }

      if (crystalType.showObjective()) {
         ResourceLocation objectiveKey = this.getSelectedObjective();
         ITextComponent objectiveCountDescription = null;
         ITextComponent objective;
         if (objectiveKey == null) {
            objective = new StringTextComponent("???").func_240699_a_(TextFormatting.GRAY);
         } else {
            VaultRaid.ARCHITECT_EVENT.get();
            VaultObjective vObjective = VaultObjective.getObjective(objectiveKey);
            if (vObjective == null) {
               objective = new StringTextComponent("???").func_240699_a_(TextFormatting.GRAY);
            } else {
               objective = vObjective.getObjectiveDisplayName();
               if (this.targetObjectiveCount >= 0) {
                  objectiveCountDescription = vObjective.getObjectiveTargetDescription(this.targetObjectiveCount);
               }
            }
         }

         tooltip.add(new StringTextComponent("Objective: ").func_230529_a_(objective));
         if (objectiveCountDescription != null) {
            tooltip.add(objectiveCountDescription);
         }
      }

      if (!this.getPlayerBossName().isEmpty()) {
         tooltip.add(
            new StringTextComponent("Player Boss: ").func_230529_a_(new StringTextComponent(this.getPlayerBossName()).func_240699_a_(TextFormatting.GREEN))
         );
      }

      for (CrystalData.Modifier modifier : this.modifiers) {
         StringTextComponent modifierName = new StringTextComponent(modifier.name);
         VaultModifier vModifier = ModConfigs.VAULT_MODIFIERS.getByName(modifier.name);
         if (vModifier != null) {
            modifierName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(vModifier.getColor())));
         }

         ITextComponent type = new StringTextComponent(modifier.operation.title).func_240699_a_(modifier.operation.color);
         tooltip.add(new StringTextComponent("- ").func_230529_a_(type).func_240702_b_(" ").func_230529_a_(modifierName));
         if (Screen.func_231173_s_() && vModifier != null) {
            ITextComponent description = new StringTextComponent("   " + vModifier.getDescription()).func_240699_a_(TextFormatting.DARK_GRAY);
            tooltip.add(description);
         }
      }
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("Type", this.type.name());
      nbt.func_74778_a("PlayerBossName", this.playerBossName);
      ListNBT modifiersList = new ListNBT();
      this.modifiers.forEach(modifier -> modifiersList.add(modifier.toNBT()));
      nbt.func_218657_a("Modifiers", modifiersList);
      nbt.func_74757_a("preventsRandomModifiers", this.preventsRandomModifiers);
      nbt.func_74757_a("canBeModified", this.canBeModified);
      if (this.selectedObjective != null) {
         nbt.func_74778_a("Objective", this.selectedObjective.toString());
      }

      nbt.func_74768_a("targetObjectiveCount", this.targetObjectiveCount);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.type = nbt.func_150297_b("Type", 8) ? Enum.valueOf(CrystalData.Type.class, nbt.func_74779_i("Type")) : CrystalData.Type.CLASSIC;
      this.playerBossName = nbt.func_74779_i("PlayerBossName");
      ListNBT modifiersList = nbt.func_150295_c("Modifiers", 10);
      modifiersList.forEach(inbt -> this.modifiers.add(CrystalData.Modifier.fromNBT((CompoundNBT)inbt)));
      this.preventsRandomModifiers = nbt.func_150297_b("preventsRandomModifiers", 1) ? nbt.func_74767_n("preventsRandomModifiers") : !this.modifiers.isEmpty();
      this.canBeModified = !nbt.func_150297_b("canBeModified", 1) || nbt.func_74767_n("canBeModified");
      this.selectedObjective = null;
      if (nbt.func_150297_b("Objective", 8)) {
         this.selectedObjective = new ResourceLocation(nbt.func_74779_i("Objective"));
      }

      this.targetObjectiveCount = nbt.func_150297_b("targetObjectiveCount", 3) ? nbt.func_74762_e("targetObjectiveCount") : -1;
   }

   private static class EmptyCrystalData extends CrystalData {
      private EmptyCrystalData() {
      }

      @Override
      public boolean addCatalystModifier(String name, boolean preventsRandomModifiers, CrystalData.Modifier.Operation operation) {
         return false;
      }

      @Override
      public boolean canAddModifier(String name, CrystalData.Modifier.Operation operation) {
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
      public void setPlayerBossName(String playerBossName) {
      }

      @Override
      public void setSelectedObjective(ResourceLocation selectedObjective) {
      }

      @Override
      public VaultRaid.Builder createVault(ServerWorld world, ServerPlayerEntity player) {
         return null;
      }
   }

   public static class Modifier {
      public final String name;
      public final CrystalData.Modifier.Operation operation;

      public Modifier(String name, CrystalData.Modifier.Operation operation) {
         this.name = name;
         this.operation = operation;
      }

      public void apply(VaultModifiers modifiers, Random random) {
         if (this.operation == CrystalData.Modifier.Operation.ADD) {
            modifiers.addPermanentModifier(this.name);
         }
      }

      public CompoundNBT toNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("Name", this.name);
         nbt.func_74768_a("Operation", this.operation.ordinal());
         return nbt;
      }

      public static CrystalData.Modifier fromNBT(CompoundNBT nbt) {
         return new CrystalData.Modifier(nbt.func_74779_i("Name"), CrystalData.Modifier.Operation.values()[nbt.func_74762_e("Operation")]);
      }

      public String getModifierName() {
         return this.name;
      }

      public CrystalData.Modifier.Operation getOperation() {
         return this.operation;
      }

      public static enum Operation {
         ADD("Has", TextFormatting.GREEN);

         private final String title;
         private final TextFormatting color;

         private Operation(String title, TextFormatting color) {
            this.title = title;
            this.color = color;
         }
      }
   }

   public static enum Type {
      CLASSIC("Normal"),
      RAFFLE(VaultLogic.RAFFLE, RaffleVaultBuilder.getInstance(), "Raffle"),
      COOP(VaultLogic.COOP, CoopVaultBuilder.getInstance(), "Cooperative"),
      TROVE(VaultLogic.CLASSIC, TroveVaultBuilder.getInstance(), "Vault Trove", TextFormatting.GOLD);

      private final VaultLogic logic;
      private final VaultRaidBuilder vaultBuilder;
      private final String name;
      private final TextFormatting color;

      private Type(String name) {
         this(VaultLogic.CLASSIC, ClassicVaultBuilder.getInstance(), name, TextFormatting.GOLD);
      }

      private Type(String name, TextFormatting color) {
         this(VaultLogic.CLASSIC, ClassicVaultBuilder.getInstance(), name, color);
      }

      private Type(VaultLogic logic, VaultRaidBuilder vaultBuilder, String name) {
         this(logic, vaultBuilder, name, TextFormatting.GOLD);
      }

      private Type(VaultLogic logic, VaultRaidBuilder vaultBuilder, String name, TextFormatting color) {
         this.logic = logic;
         this.vaultBuilder = vaultBuilder;
         this.name = name;
         this.color = color;
      }

      public boolean canCraftModifiers() {
         return this == CLASSIC || this == COOP;
      }

      public boolean showTypePrefix() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
      }

      public boolean showObjective() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
      }

      public boolean visibleInCreative() {
         return this == CLASSIC || this == RAFFLE || this == COOP || this == TROVE;
      }

      public boolean canBeCowVault() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
      }

      public boolean canGenerateRandomModifiers() {
         return this == CLASSIC || this == RAFFLE || this == COOP;
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

      public ITextComponent getDisplayName() {
         return new StringTextComponent(this.name).func_240699_a_(this.color);
      }
   }
}

package iskallia.vault.item.crystal.modifiers;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.CrystalEntry;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.item.crystal.properties.InstabilityCrystalProperties;
import iskallia.vault.world.VaultMode;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public abstract class CrystalModifiers extends CrystalEntry implements Iterable<VaultModifierStack>, ISerializable<CompoundTag, JsonObject> {
   public abstract List<VaultModifierStack> getList();

   @NotNull
   @Override
   public Iterator<VaultModifierStack> iterator() {
      return this.getList().iterator();
   }

   public abstract boolean hasRandomModifiers();

   public abstract boolean hasClarity();

   public abstract void setRandomModifiers(boolean var1);

   public abstract void setClarity(boolean var1);

   @Override
   public void configure(Vault vault, RandomSource random) {
      boolean casual = ((VaultMode.GameRuleValue)ServerLifecycleHooks.getCurrentServer().getGameRules().getRule(ModGameRules.MODE)).get() == VaultMode.CASUAL;

      for (VaultModifierStack stack : this) {
         vault.ifPresent(Vault.MODIFIERS, m -> m.addModifier(stack.getModifier(), stack.getSize(), true, random));
      }

      if (casual) {
         VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(VaultMod.id("casual"), null);
         if (modifier != null) {
            vault.ifPresent(Vault.MODIFIERS, m -> m.addModifier(modifier, 1, false, random));
         }
      }

      if (this.hasRandomModifiers()) {
         for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(VaultMod.id("default"), vault.get(Vault.LEVEL).get(), random)) {
            if (!casual || !(modifier instanceof PlayerInventoryRestoreModifier)) {
               vault.ifPresent(Vault.MODIFIERS, m -> m.addModifier(modifier, 1, true, random));
            }
         }
      }
   }

   protected boolean canAdd(CrystalData crystal, VaultModifierStack modifierStack) {
      return !crystal.getProperties().isUnmodifiable();
   }

   public boolean addByCrafting(CrystalData crystal, List<VaultModifierStack> modifierStackList, boolean simulate) {
      for (VaultModifierStack modifierStack : modifierStackList) {
         if (!this.addByCrafting(crystal, modifierStack, true, true)) {
            return false;
         }
      }

      if (!simulate) {
         for (VaultModifierStack modifierStackx : modifierStackList) {
            this.addByCrafting(crystal, modifierStackx, true, false);
         }
      }

      return true;
   }

   public boolean addByCrafting(CrystalData crystal, VaultModifierStack modifierStack, boolean preventsRandomModifiers, boolean simulate) {
      if (!this.canAdd(crystal, modifierStack)) {
         return false;
      } else {
         if (!simulate) {
            if (preventsRandomModifiers) {
               this.setRandomModifiers(false);
            }

            if (crystal.getProperties() instanceof InstabilityCrystalProperties properties) {
               properties.setInstability(properties.getInstability() + ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.instabilityPerCraft);
            }

            this.add(modifierStack);
         }

         return true;
      }
   }

   public void add(VaultModifierStack modifierStack) {
      if (modifierStack.isEmpty()) {
         VaultMod.LOGGER.error("Attempted to add Empty modifier to crystal. If you see this stacktrace, please share it with the devs.", new Exception());
      } else {
         boolean found = false;
         ResourceLocation modifierId = modifierStack.getModifierId();

         for (VaultModifierStack modifier : this.getList()) {
            if (modifier.getModifierId().equals(modifierId)) {
               modifier.grow(modifierStack.getSize());
               found = true;
               break;
            }
         }

         if (!found) {
            this.getList().add(modifierStack.copy());
         }

         this.sortModifiers();
      }
   }

   public boolean isEmpty() {
      return this.getList().isEmpty();
   }

   public int getCurseCount() {
      return this.getList()
         .stream()
         .filter(vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()))
         .map(VaultModifierStack::getSize)
         .reduce(0, Integer::sum);
   }

   public boolean hasCurse() {
      return this.getCurseCount() > 0;
   }

   public boolean removeAllCurses() {
      return this.getList().removeIf(stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(stack.getModifierId()));
   }

   public boolean removeRandomCurse() {
      List<VaultModifierStack> curseList = this.getList().stream().filter(stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(stack.getModifierId())).toList();
      if (curseList.isEmpty()) {
         return false;
      } else {
         RandomSource random = JavaRandom.ofNanoTime();
         VaultModifierStack modifierStack = curseList.get(random.nextInt(curseList.size()));
         if (modifierStack.shrink(1).isEmpty()) {
            this.getList().remove(modifierStack);
         }

         return true;
      }
   }

   public void sortModifiers() {
      this.getList().sort(Comparator.comparing(VaultModifierStack::getSize).reversed());
   }

   protected void addNonCatalystModifierInformation(Predicate<VaultModifierStack> filter, Component headerComponent, List<Component> tooltip) {
      List<VaultModifierStack> modifierList = this.getList().stream().filter(filter).toList();
      if (!modifierList.isEmpty()) {
         tooltip.add(headerComponent);

         for (VaultModifierStack modifierStack : modifierList) {
            VaultModifier<?> vaultModifier = modifierStack.getModifier();
            TextComponent modifierName = new TextComponent(vaultModifier.getDisplayNameFormatted(modifierStack.getSize()));
            modifierName.setStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor()));
            if (modifierStack.getSize() > 1) {
               Component stackSize = new TextComponent("%dx".formatted(modifierStack.getSize())).withStyle(ChatFormatting.GRAY);
               tooltip.add(new TextComponent("  ").withStyle(ChatFormatting.GRAY).append(stackSize).append(" ").append(modifierName));
            } else {
               tooltip.add(new TextComponent("  ").append(modifierName));
            }

            if (Screen.hasShiftDown()) {
               String descriptionTxt = vaultModifier.getDisplayDescriptionFormatted(modifierStack.getSize());
               if (!descriptionTxt.isEmpty()) {
                  Component description = new TextComponent("  " + descriptionTxt).withStyle(ChatFormatting.DARK_GRAY);
                  tooltip.add(description);
               }
            }
         }
      }
   }

   protected void addCatalystModifierInformation(Predicate<VaultModifierStack> filter, Component headerComponent, List<Component> tooltip) {
      List<VaultModifierStack> modifierList = this.getList().stream().filter(filter).toList();
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
}

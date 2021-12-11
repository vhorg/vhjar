package iskallia.vault.world.vault.modifier;

import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultModifiers implements INBTSerializable<CompoundNBT>, Iterable<VaultModifier> {
   private final List<VaultModifiers.ActiveModifier> modifiers = new ArrayList<>();
   protected boolean initialized;

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized() {
      this.initialized = true;
   }

   public void generateGlobal(VaultRaid vault, ServerWorld world, Random random) {
      int level = vault.getProperties().getValue(VaultRaid.LEVEL);
      VaultModifiersConfig.ModifierPoolType type = VaultModifiersConfig.ModifierPoolType.DEFAULT;
      if (vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false)) {
         type = VaultModifiersConfig.ModifierPoolType.RAFFLE;
      } else if (vault.getActiveObjective(RaidChallengeObjective.class).isPresent()) {
         type = VaultModifiersConfig.ModifierPoolType.RAID;
      }

      ResourceLocation objectiveKey = vault.getAllObjectives().stream().findFirst().map(VaultObjective::getId).orElse(null);
      ModConfigs.VAULT_MODIFIERS.getRandom(random, level, type, objectiveKey).forEach(this::addPermanentModifier);
   }

   @Deprecated
   public void generatePlayer(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      int level = player.getProperties().getValue(VaultRaid.LEVEL);
      VaultModifiersConfig.ModifierPoolType type = VaultModifiersConfig.ModifierPoolType.DEFAULT;
      if (vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false)) {
         type = VaultModifiersConfig.ModifierPoolType.RAFFLE;
      } else if (vault.getActiveObjective(RaidChallengeObjective.class).isPresent()) {
         type = VaultModifiersConfig.ModifierPoolType.RAID;
      }

      ResourceLocation objectiveKey = vault.getAllObjectives().stream().findFirst().map(VaultObjective::getId).orElse(null);
      ModConfigs.VAULT_MODIFIERS.getRandom(random, level, type, objectiveKey).forEach(this::addPermanentModifier);
      this.setInitialized();
   }

   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
      this.modifiers.forEach(modifier -> modifier.getModifier().apply(vault, player, world, random));
   }

   public void tick(VaultRaid vault, ServerWorld world, PlayerFilter applyFilter) {
      this.modifiers
         .removeIf(
            activeModifier -> {
               VaultModifier modifier = activeModifier.getModifier();
               vault.getPlayers().forEach(vPlayer -> {
                  if (applyFilter.test(vPlayer.getPlayerId())) {
                     modifier.tick(vault, null, world);
                  }
               });
               if (activeModifier.tick()) {
                  ITextComponent removalMsg = new StringTextComponent("Modifier ")
                     .func_240699_a_(TextFormatting.GRAY)
                     .func_230529_a_(modifier.getNameComponent())
                     .func_230529_a_(new StringTextComponent(" expired.").func_240699_a_(TextFormatting.GRAY));
                  vault.getPlayers().forEach(vPlayer -> {
                     if (applyFilter.test(vPlayer.getPlayerId())) {
                        modifier.remove(vault, vPlayer, world, world.func_201674_k());
                        vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(removalMsg, Util.field_240973_b_));
                     }
                  });
                  return true;
               } else {
                  return false;
               }
            }
         );
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT modifiersList = new ListNBT();
      this.modifiers.forEach(modifier -> modifiersList.add(modifier.serialize()));
      nbt.func_218657_a("modifiers", modifiersList);
      nbt.func_74757_a("Initialized", this.isInitialized());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.modifiers.clear();
      ListNBT modifierList = nbt.func_150295_c("modifiers", 10);

      for (int i = 0; i < modifierList.size(); i++) {
         CompoundNBT tag = modifierList.func_150305_b(i);
         VaultModifiers.ActiveModifier mod = VaultModifiers.ActiveModifier.deserialize(tag);
         if (mod != null) {
            this.modifiers.add(mod);
         }
      }

      ListNBT legacyModifierList = nbt.func_150295_c("List", 8);

      for (int ix = 0; ix < legacyModifierList.size(); ix++) {
         VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(legacyModifierList.func_150307_f(ix));
         if (modifier != null) {
            this.modifiers.add(new VaultModifiers.ActiveModifier(modifier, -1));
         }
      }

      this.initialized = nbt.func_74767_n("Initialized");
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.modifiers.size());
      this.modifiers.forEach(modifier -> modifier.encode(buffer));
   }

   public static VaultModifiers decode(PacketBuffer buffer) {
      VaultModifiers result = new VaultModifiers();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         VaultModifiers.ActiveModifier modifier = VaultModifiers.ActiveModifier.decode(buffer);
         if (modifier != null) {
            result.modifiers.add(modifier);
         }
      }

      return result;
   }

   public Stream<VaultModifier> stream() {
      return this.modifiers.stream().map(rec$ -> rec$.getModifier());
   }

   @Nonnull
   @Override
   public Iterator<VaultModifier> iterator() {
      List<VaultModifier> modifiers = this.stream().collect(Collectors.toList());
      return modifiers.iterator();
   }

   public void forEach(BiConsumer<Integer, VaultModifier> consumer) {
      int index = 0;

      for (VaultModifiers.ActiveModifier modifier : this.modifiers) {
         consumer.accept(index, modifier.getModifier());
         index++;
      }
   }

   public int size() {
      return this.modifiers.size();
   }

   public boolean isEmpty() {
      return this.size() <= 0;
   }

   public void addPermanentModifier(String name) {
      this.addPermanentModifier(ModConfigs.VAULT_MODIFIERS.getByName(name));
   }

   public void addPermanentModifier(VaultModifier modifier) {
      this.putModifier(modifier, -1);
   }

   public void addTemporaryModifier(VaultModifier modifier, int timeout) {
      this.putModifier(modifier, Math.max(0, timeout));
   }

   private void putModifier(VaultModifier modifier, int timeout) {
      this.modifiers.add(new VaultModifiers.ActiveModifier(modifier, timeout));
   }

   public boolean removePermanentModifier(String name) {
      for (VaultModifiers.ActiveModifier activeModifier : this.modifiers) {
         if (activeModifier.getModifier().getName().equals(name) && activeModifier.tick == -1) {
            this.modifiers.remove(activeModifier);
            return true;
         }
      }

      return false;
   }

   private static class ActiveModifier {
      private final VaultModifier modifier;
      private int tick;

      private ActiveModifier(VaultModifier modifier, int tick) {
         this.modifier = modifier;
         this.tick = tick;
      }

      @Nullable
      private static VaultModifiers.ActiveModifier deserialize(CompoundNBT tag) {
         VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(tag.func_74779_i("key"));
         int timeout = tag.func_74762_e("timeout");
         return modifier == null ? null : new VaultModifiers.ActiveModifier(modifier, timeout);
      }

      @Nullable
      private static VaultModifiers.ActiveModifier decode(PacketBuffer buffer) {
         String modifierName = buffer.func_150789_c(32767);
         int timeout = buffer.readInt();
         VaultModifier modifier = ModConfigs.VAULT_MODIFIERS.getByName(modifierName);
         return modifier == null ? null : new VaultModifiers.ActiveModifier(modifier, timeout);
      }

      private VaultModifier getModifier() {
         return this.modifier;
      }

      private boolean tick() {
         if (this.tick == -1) {
            return false;
         } else {
            this.tick--;
            return this.tick == 0;
         }
      }

      private void encode(PacketBuffer buffer) {
         buffer.func_180714_a(this.modifier.getName());
         buffer.writeInt(this.tick);
      }

      private CompoundNBT serialize() {
         CompoundNBT tag = new CompoundNBT();
         tag.func_74778_a("key", this.modifier.getName());
         tag.func_74768_a("timeout", this.tick);
         return tag;
      }
   }
}

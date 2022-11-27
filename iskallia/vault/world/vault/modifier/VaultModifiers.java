package iskallia.vault.world.vault.modifier;

import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.IVaultModifierBehaviorTick;
import iskallia.vault.world.vault.modifier.spi.IVaultModifierStack;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultModifiers implements INBTSerializable<CompoundTag> {
   private final VaultModifiers.ActiveModifiers activeModifiers = new VaultModifiers.ActiveModifiers();
   private final List<VaultModifiers.VaultModifierTimer> modifierTimers = new ArrayList<>();
   private boolean initialized;
   private static final String TAG_MODIFIERS = "modifiers";
   private static final String TAG_TIMERS = "timers";
   private static final String TAG_INITIALIZED = "initialized";

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized() {
      this.initialized = true;
   }

   public void apply(VaultRaid vault, VaultPlayer player, ServerLevel world, Random random) {
      this.activeModifiers.forEach(modifierStack -> {});
   }

   public void tick(VaultRaid vault, ServerLevel world, PlayerFilter applyFilter) {
      this.activeModifiers.removeIf(modifierStack -> {
         if (modifierStack.isEmpty()) {
            return true;
         } else {
            if (modifierStack.getModifier() instanceof IVaultModifierBehaviorTick tickBehavior) {
               vault.getPlayers().forEach(vaultPlayer -> {
                  if (applyFilter.test(vaultPlayer.getPlayerId())) {
                     tickBehavior.tick(vault, vaultPlayer, world, modifierStack.getSize());
                  }
               });
            }

            return false;
         }
      });
      this.modifierTimers
         .removeIf(
            modifierTimer -> {
               VaultModifierStack modifierStack = this.activeModifiers.get(modifierTimer.getId());
               if (modifierStack == null) {
                  return true;
               } else {
                  VaultModifier<?> modifier = modifierStack.getModifier();
                  if (modifierTimer.tick()) {
                     Component removalMsg = new TextComponent("Modifier ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(modifier.getNameComponent())
                        .append(new TextComponent(" expired.").withStyle(ChatFormatting.GRAY));
                     vault.getPlayers().forEach(vaultPlayer -> {
                        if (applyFilter.test(vaultPlayer.getPlayerId())) {
                           vaultPlayer.runIfPresent(world.getServer(), serverPlayer -> serverPlayer.sendMessage(removalMsg, Util.NIL_UUID));
                        }
                     });
                     if (modifierStack.shrink(1).isEmpty()) {
                        this.activeModifiers.remove(modifier);
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            }
         );
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("modifiers", this.activeModifiers.serializeNBT());
      ListTag timerList = new ListTag();
      this.modifierTimers.forEach(modifierTimer -> timerList.add(modifierTimer.serializeNBT()));
      nbt.put("timers", timerList);
      nbt.putBoolean("initialized", this.isInitialized());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.activeModifiers.deserializeNBT(nbt.getList("modifiers", 10));
      this.modifierTimers.clear();
      ListTag timerList = nbt.getList("timers", 10);

      for (int i = 0; i < timerList.size(); i++) {
         VaultModifiers.VaultModifierTimer.deserializeNBT(timerList.getCompound(i)).ifPresent(this.modifierTimers::add);
      }

      this.initialized = nbt.getBoolean("initialized");
   }

   public void encode(FriendlyByteBuf buffer) {
      this.activeModifiers.encode(buffer);
      buffer.writeInt(this.modifierTimers.size());
      this.modifierTimers.forEach(modifierTimer -> modifierTimer.encode(buffer));
   }

   public static VaultModifiers decode(FriendlyByteBuf buffer) {
      VaultModifiers result = new VaultModifiers();
      result.activeModifiers.decode(buffer);
      int timerCount = buffer.readInt();

      for (int i = 0; i < timerCount; i++) {
         VaultModifiers.VaultModifierTimer.decode(buffer).ifPresent(result.modifierTimers::add);
      }

      return result;
   }

   public <T extends VaultModifier<?>> Stream<VaultModifiers.ActiveModifierStack<T>> stream(Class<T> modifierClass) {
      return this.stream()
         .filter(activeModifierStack -> modifierClass.isAssignableFrom(activeModifierStack.getModifier().getClass()))
         .map(activeModifierStack -> (VaultModifiers.ActiveModifierStack<T>)activeModifierStack);
   }

   public Stream<IVaultModifierStack> stream() {
      return this.activeModifiers.stream();
   }

   public List<IVaultModifierStack> getModifiers() {
      return this.activeModifiers.getModifiers();
   }

   public void forEach(BiConsumer<Integer, IVaultModifierStack> consumer) {
      int index = 0;

      for (IVaultModifierStack modifierStack : this.getModifiers()) {
         consumer.accept(index, modifierStack);
         index++;
      }
   }

   public int size() {
      return this.activeModifiers.size();
   }

   public boolean isEmpty() {
      return this.size() <= 0;
   }

   public void addPermanentModifier(VaultModifierStack modifierStack) {
      this.addPermanentModifier(modifierStack.getModifier(), modifierStack.getSize());
   }

   public void addPermanentModifier(ResourceLocation id, int stackSize) {
      VaultModifierRegistry.getOpt(id).ifPresent(vaultModifier -> this.addPermanentModifier((VaultModifier<?>)vaultModifier, stackSize));
   }

   public void addPermanentModifier(VaultModifier<?> modifier, int stackSize) {
      this.activeModifiers.add(modifier, stackSize);
   }

   public void addPermanentModifiers(Collection<VaultModifierStack> modifierStacks) {
      this.activeModifiers.add(modifierStacks);
   }

   public void addTemporaryModifier(VaultModifier<?> modifier, int stackSize, int durationTicks) {
      this.addPermanentModifier(modifier, stackSize);
      if (durationTicks > 0) {
         for (int i = 0; i < stackSize; i++) {
            this.modifierTimers.add(new VaultModifiers.VaultModifierTimer(modifier.getId(), durationTicks));
         }
      }
   }

   public static class ActiveModifierStack<T extends VaultModifier<?>> implements IVaultModifierStack {
      private final IVaultModifierStack vaultModifierStack;

      public static <T extends VaultModifier<?>> VaultModifiers.ActiveModifierStack<T> of(IVaultModifierStack vaultModifierStack) {
         return new VaultModifiers.ActiveModifierStack<>(vaultModifierStack);
      }

      private ActiveModifierStack(IVaultModifierStack vaultModifierStack) {
         this.vaultModifierStack = vaultModifierStack;
      }

      @Override
      public T getModifier() {
         return (T)this.vaultModifierStack.getModifier();
      }

      @Override
      public ResourceLocation getModifierId() {
         return this.vaultModifierStack.getModifierId();
      }

      @Override
      public int getSize() {
         return this.vaultModifierStack.getSize();
      }

      @Override
      public boolean isEmpty() {
         return this.vaultModifierStack.isEmpty();
      }
   }

   private static class ActiveModifiers implements INBTSerializable<ListTag> {
      private final Map<ResourceLocation, VaultModifierStack> activeModifierMap = new HashMap<>();
      private final List<IVaultModifierStack> activeModifierList = new ArrayList<>();
      private final List<IVaultModifierStack> unmodifiableActiveModifierList = Collections.unmodifiableList(this.activeModifierList);

      public ActiveModifiers() {
      }

      public VaultModifierStack get(ResourceLocation id) {
         return this.activeModifierMap.get(id);
      }

      public boolean removeIf(Predicate<IVaultModifierStack> filter) {
         if (this.activeModifierList.removeIf(filter)) {
            this.activeModifierMap.entrySet().removeIf(entry -> filter.test(entry.getValue()));
            return true;
         } else {
            return false;
         }
      }

      public void forEach(Consumer<IVaultModifierStack> action) {
         this.activeModifierList.forEach(action);
      }

      public void remove(VaultModifier<?> modifier) {
         this.activeModifierMap.remove(modifier.getId());
         this.activeModifierList.removeIf(vaultModifierStack -> vaultModifierStack.getModifier() == modifier);
      }

      public void add(Collection<VaultModifierStack> vaultModifierStacks) {
         for (VaultModifierStack vaultModifierStack : vaultModifierStacks) {
            VaultModifier<?> modifier = vaultModifierStack.getModifier();
            this.activeModifierMap
               .computeIfAbsent(modifier.getId(), resourceLocation -> VaultModifierStack.of(modifier, 1))
               .grow(vaultModifierStack.getSize() - 1);
         }

         this.updateModifierList();
      }

      public void add(VaultModifier<?> modifier, int stackSize) {
         this.activeModifierMap.computeIfAbsent(modifier.getId(), resourceLocation -> VaultModifierStack.of(modifier, 1)).grow(stackSize - 1);
         this.updateModifierList();
      }

      public int size() {
         return this.activeModifierMap.size();
      }

      public Stream<IVaultModifierStack> stream() {
         return this.getModifiers().stream();
      }

      public List<IVaultModifierStack> getModifiers() {
         return this.unmodifiableActiveModifierList;
      }

      public ListTag serializeNBT() {
         ListTag modifiersList = new ListTag();
         this.activeModifierMap.values().forEach(modifierStack -> modifiersList.add(modifierStack.serializeNBT()));
         return modifiersList;
      }

      public void deserializeNBT(ListTag modifierList) {
         this.activeModifierMap.clear();
         this.activeModifierList.clear();

         for (int i = 0; i < modifierList.size(); i++) {
            VaultModifierStack modifierStack = VaultModifierStack.of(modifierList.getCompound(i));
            if (!modifierStack.isEmpty()) {
               this.activeModifierMap.put(modifierStack.getModifierId(), modifierStack);
               this.activeModifierList.add(VaultModifiers.ActiveModifierStack.of(modifierStack));
            }
         }

         this.updateModifierList();
      }

      public void encode(FriendlyByteBuf buffer) {
         buffer.writeInt(this.activeModifierMap.size());
         this.activeModifierMap.values().forEach(modifierStack -> modifierStack.encode(buffer));
      }

      public void decode(FriendlyByteBuf buffer) {
         this.activeModifierMap.clear();
         this.activeModifierList.clear();
         VaultModifiers.ActiveModifiers result = new VaultModifiers.ActiveModifiers();
         int modifierCount = buffer.readInt();

         for (int i = 0; i < modifierCount; i++) {
            VaultModifierStack modifierStack = VaultModifierStack.decode(buffer);
            if (!modifierStack.isEmpty()) {
               result.activeModifierMap.put(modifierStack.getModifierId(), modifierStack);
               result.activeModifierList.add(VaultModifiers.ActiveModifierStack.of(modifierStack));
            }
         }

         result.updateModifierList();
      }

      private void updateModifierList() {
         this.activeModifierList.clear();
         this.activeModifierList.addAll(this.activeModifierMap.values());
         this.sortModifierList();
      }

      private void sortModifierList() {
         this.activeModifierList.sort(Comparator.comparing(IVaultModifierStack::getSize).reversed());
      }
   }

   private static class VaultModifierTimer {
      private static final String TAG_ID = "id";
      private static final String TAG_DURATION_TICKS = "durationTicks";
      private final ResourceLocation id;
      private int durationTicks;

      private VaultModifierTimer(ResourceLocation id, int durationTicks) {
         this.id = id;
         this.durationTicks = durationTicks;
      }

      private ResourceLocation getId() {
         return this.id;
      }

      private boolean tick() {
         if (this.durationTicks < 0) {
            return false;
         } else {
            this.durationTicks--;
            return this.durationTicks == 0;
         }
      }

      private CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         tag.putString("id", this.id.toString());
         tag.putInt("durationTicks", this.durationTicks);
         return tag;
      }

      private static Optional<VaultModifiers.VaultModifierTimer> deserializeNBT(CompoundTag tag) {
         return VaultModifierRegistry.getOpt(new ResourceLocation(tag.getString("id")))
            .map(vaultModifier -> new VaultModifiers.VaultModifierTimer(vaultModifier.getId(), tag.getInt("durationTicks")));
      }

      private void encode(FriendlyByteBuf buffer) {
         buffer.writeResourceLocation(this.id);
         buffer.writeInt(this.durationTicks);
      }

      private static Optional<VaultModifiers.VaultModifierTimer> decode(FriendlyByteBuf buffer) {
         ResourceLocation resourceLocation = buffer.readResourceLocation();
         int durationTicks = buffer.readInt();
         return VaultModifierRegistry.getOpt(resourceLocation).map(vaultModifier -> new VaultModifiers.VaultModifierTimer(resourceLocation, durationTicks));
      }
   }
}

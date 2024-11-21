package iskallia.vault.item;

import iskallia.vault.antique.Antique;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.antique.condition.AntiqueCondition;
import iskallia.vault.config.AntiquesConfig;
import iskallia.vault.container.inventory.AntiqueCollectorBookContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.provider.AntiqueCollectorBookProvider;
import iskallia.vault.init.ModItems;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class AntiqueStampCollectorBook extends Item {
   public AntiqueStampCollectorBook(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack inHand = player.getItemInHand(hand);
      if (player instanceof ServerPlayer sPlayer) {
         int slot = hand == InteractionHand.MAIN_HAND ? sPlayer.getInventory().selected : 40;
         openBook(sPlayer, slot, inHand);
         return InteractionResultHolder.success(inHand);
      } else {
         return InteractionResultHolder.pass(inHand);
      }
   }

   public static void openBook(ServerPlayer player, int bookSlot, ItemStack bookStack) {
      AntiqueCollectorBookProvider provider = new AntiqueCollectorBookProvider(player, bookSlot, bookStack);
      NetworkHooks.openGui(player, provider, provider.extraDataWriter());
   }

   public static boolean interceptPlayerInventoryItemAddition(Inventory playerInventory, ItemStack toAdd) {
      if (!toAdd.is(ModItems.ANTIQUE)) {
         return false;
      } else {
         Antique antique = AntiqueItem.getAntique(toAdd);
         if (antique == null) {
            return false;
         } else {
            Player player = playerInventory.player;
            if (player.containerMenu instanceof AntiqueCollectorBookContainer) {
               return false;
            } else {
               ItemStack antiqueBook = ItemStack.EMPTY;

               for (int slot = 0; slot < playerInventory.getContainerSize(); slot++) {
                  ItemStack invStack = playerInventory.getItem(slot);
                  if (invStack.is(ModItems.ANTIQUE_COLLECTOR_BOOK)) {
                     antiqueBook = invStack;
                     break;
                  }
               }

               if (antiqueBook.isEmpty()) {
                  return false;
               } else {
                  AntiqueStampCollectorBook.StoredAntiques antiques = getStoredAntiques(antiqueBook);
                  AntiqueStampCollectorBook.StoredAntiqueInfo info = antiques.getInfo(antique);
                  int count = info.getCount();
                  int result = Math.min(count + toAdd.getCount(), 2147483582);
                  int added = result - count;
                  info.setCount(result);
                  toAdd.setCount(toAdd.getCount() - added);
                  setStoredAntiques(antiqueBook, antiques);
                  return true;
               }
            }
         }
      }
   }

   public static Container getAntiqueContainer(ItemStack bookStack, Predicate<Player> stillValidCheck) {
      return getAntiqueContainer(getStoredAntiques(bookStack), stillValidCheck);
   }

   public static Container getAntiqueContainer(AntiqueStampCollectorBook.StoredAntiques storedAntiques, Predicate<Player> stillValidCheck) {
      OverSizedInventory container = new OverSizedInventory(AntiqueRegistry.getRegistry().getValues().size(), stacks -> {}, stillValidCheck);
      List<Antique> sortedAntiques = AntiqueRegistry.sorted().toList();

      for (int slot = 0; slot < sortedAntiques.size(); slot++) {
         Antique antique = sortedAntiques.get(slot);
         int count = storedAntiques.getInfo(antique).getCount();
         if (count > 0) {
            container.setItem(slot, AntiqueItem.createStack(antique, count));
         }
      }

      return container;
   }

   public static void setStoredAntiques(ItemStack stack, AntiqueStampCollectorBook.StoredAntiques antiques) {
      CompoundTag tag = stack.getOrCreateTag();
      antiques.updateProgressCounts();
      tag.put("storedAntiques", antiques.serialize());
   }

   public static AntiqueStampCollectorBook.StoredAntiques getStoredAntiques(ItemStack stack) {
      CompoundTag tag = stack.getTag();
      if (tag == null) {
         return new AntiqueStampCollectorBook.StoredAntiques();
      } else {
         AntiqueStampCollectorBook.StoredAntiques antiques = new AntiqueStampCollectorBook.StoredAntiques();
         if (tag.contains("storedAntiques")) {
            antiques.load(tag.getCompound("storedAntiques"));
         }

         if (tag.contains("Antiques")) {
            antiques.loadLegacy(tag.getCompound("Antiques"));
            antiques.updateProgressCounts();
            tag.remove("Antiques");
            setStoredAntiques(stack, antiques);
         }

         return antiques;
      }
   }

   public static class StoredAntiqueInfo {
      private int count;
      private int progressRevealCount;
      private boolean hasDiscoveredAntique;

      private StoredAntiqueInfo() {
         this(0, 0, false);
      }

      private StoredAntiqueInfo(int count, int progressRevealCount, boolean hasDiscoveredAntique) {
         this.count = count;
         this.progressRevealCount = progressRevealCount;
         this.hasDiscoveredAntique = hasDiscoveredAntique;
         this.updateDiscovery();
      }

      public int getCount() {
         return this.count;
      }

      public void setCount(int count) {
         this.count = count;
         this.updateDiscovery();
      }

      public void addCount(int count) {
         this.count += count;
         this.updateDiscovery();
      }

      private void updateDiscovery() {
         this.hasDiscoveredAntique = this.hasDiscoveredAntique | this.count > 0;
      }

      public boolean hasDiscoveredAntique() {
         return this.hasDiscoveredAntique;
      }

      public int getProgressRevealCount() {
         return this.progressRevealCount;
      }
   }

   public static class StoredAntiques {
      private final Map<ResourceLocation, AntiqueStampCollectorBook.StoredAntiqueInfo> antiques = new HashMap<>();

      @Nonnull
      public AntiqueStampCollectorBook.StoredAntiqueInfo getInfo(Antique antique) {
         return this.getInfo(antique.getRegistryName());
      }

      @Nonnull
      private AntiqueStampCollectorBook.StoredAntiqueInfo getInfo(ResourceLocation key) {
         return this.antiques.computeIfAbsent(key, k -> new AntiqueStampCollectorBook.StoredAntiqueInfo());
      }

      public void load(CompoundTag tag) {
         tag.getAllKeys().forEach(key -> {
            ResourceLocation regKey = ResourceLocation.tryParse(key);
            if (regKey != null) {
               CompoundTag infoTag = tag.getCompound(key);
               int count = infoTag.getInt("count");
               int progressRevealCount = infoTag.getInt("progressRevealCount");
               boolean hasDiscoveredAntique = infoTag.getBoolean("hasDiscoveredAntique");
               this.antiques.put(regKey, new AntiqueStampCollectorBook.StoredAntiqueInfo(count, progressRevealCount, hasDiscoveredAntique));
            }
         });
      }

      public void load(FriendlyByteBuf buf) {
         int size = buf.readInt();

         for (int i = 0; i < size; i++) {
            ResourceLocation key = buf.readResourceLocation();
            int count = buf.readInt();
            int progressRevealCount = buf.readInt();
            boolean hasDiscoveredAntique = buf.readBoolean();
            this.antiques.put(key, new AntiqueStampCollectorBook.StoredAntiqueInfo(count, progressRevealCount, hasDiscoveredAntique));
         }
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         this.antiques.forEach((key, value) -> {
            CompoundTag infoTag = new CompoundTag();
            infoTag.putInt("count", value.count);
            infoTag.putInt("progressRevealCount", value.progressRevealCount);
            infoTag.putBoolean("hasDiscoveredAntique", value.hasDiscoveredAntique);
            tag.put(key.toString(), infoTag);
         });
         return tag;
      }

      public void write(FriendlyByteBuf buf) {
         buf.writeInt(this.antiques.size());
         this.antiques.forEach((key, value) -> {
            buf.writeResourceLocation(key);
            buf.writeInt(value.count);
            buf.writeInt(value.progressRevealCount);
            buf.writeBoolean(value.hasDiscoveredAntique);
         });
      }

      public void loadLegacy(CompoundTag tag) {
         tag.getAllKeys().forEach(key -> {
            ResourceLocation regKey = ResourceLocation.tryParse(key);
            if (regKey != null) {
               int count = tag.getInt(key);
               this.getInfo(regKey).addCount(count);
            }
         });
      }

      public void updateProgressCounts() {
         this.antiques.forEach((key, info) -> {
            Antique antique = (Antique)AntiqueRegistry.getRegistry().getValue(key);
            if (antique != null) {
               AntiquesConfig.Entry cfg = antique.getConfig();
               if (cfg != null) {
                  AntiqueCondition condition = cfg.getCondition();
                  int conditionRevealCount = condition.collectConditionDisplay().size();
                  int neededRevealCount = Math.max(cfg.getInfo().getRequiredCount(), conditionRevealCount);
                  float donePercent = (float)info.count / neededRevealCount;
                  int processCount = Mth.floor(donePercent * conditionRevealCount);
                  info.progressRevealCount = Math.max(info.progressRevealCount, processCount);
               }
            }
         });
      }
   }
}

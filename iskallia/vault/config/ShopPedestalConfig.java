package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.init.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ShopPedestalConfig extends Config {
   @Expose
   public LevelEntryList<ShopPedestalConfig.ShopTier> LEVELS;
   public static final ShopPedestalConfig.ShopOffer EMPTY = new ShopPedestalConfig.ShopOffer(ItemStack.EMPTY, OverSizedItemStack.EMPTY);

   @Override
   public String getName() {
      return "shop_pedestal";
   }

   public ShopPedestalConfig.ShopOffer getForLevel(int vaultLevel, RandomSource random) {
      return this.LEVELS
         .getForLevel(vaultLevel)
         .map(shopTier -> shopTier.TRADE_POOL)
         .filter(entries -> !entries.isEmpty())
         .flatMap(entries -> entries.getRandom(random))
         .map(shopEntry -> shopEntry.roll(random))
         .orElse(EMPTY);
   }

   @Override
   protected boolean isValid() {
      if (this.LEVELS != null && this.LEVELS.size() != 0) {
         for (ShopPedestalConfig.ShopTier v : this.LEVELS) {
            if (v.TRADE_POOL == null || v.TRADE_POOL.isEmpty()) {
               VaultMod.LOGGER.error("Pedestal loot pool for level {} cannot be empty", v.MIN_LEVEL);
               return false;
            }

            for (ShopPedestalConfig.ShopEntry value : v.TRADE_POOL.keySet()) {
               if (value == null || value.CURRENCY == null || value.OFFER == null) {
                  VaultMod.LOGGER.error("Pedestal loot entry cannot be empty");
                  return false;
               }

               if (value.MIN_COST > value.MAX_COST) {
                  VaultMod.LOGGER.error("Pedestal entry min cost cannot be larger than max cost");
                  return false;
               }
            }
         }

         return true;
      } else {
         VaultMod.LOGGER.error("Pedestal loot list cannot be empty");
         return false;
      }
   }

   @Override
   protected void reset() {
      this.LEVELS = LevelEntryList.of(
         new ShopPedestalConfig.ShopTier(
            0,
            new WeightedList<ShopPedestalConfig.ShopEntry>()
               .add(new ShopPedestalConfig.ShopEntry(Items.DIORITE.getDefaultInstance(), ModBlocks.VAULT_BRONZE, 1, 2), 1)
         ),
         new ShopPedestalConfig.ShopTier(
            10,
            new WeightedList<ShopPedestalConfig.ShopEntry>()
               .add(new ShopPedestalConfig.ShopEntry(Items.DIORITE.getDefaultInstance(), ModBlocks.VAULT_BRONZE, 128, 256), 1)
         )
      );
   }

   private static final class ShopEntry {
      @Expose
      private ItemStack OFFER;
      @Expose
      private Item CURRENCY;
      @Expose
      private int MIN_COST;
      @Expose
      private int MAX_COST;

      private ShopEntry(ItemStack OFFER, Item CURRENCY, int MIN_COST, int MAX_COST) {
         this.OFFER = OFFER;
         this.CURRENCY = CURRENCY;
         this.MIN_COST = MIN_COST;
         this.MAX_COST = MAX_COST;
      }

      public ShopPedestalConfig.ShopOffer roll(RandomSource random) {
         return ShopPedestalConfig.ShopOffer.of(
            this.OFFER, new OverSizedItemStack(new ItemStack(this.CURRENCY), random.nextInt(this.MIN_COST, this.MAX_COST + 1))
         );
      }
   }

   public record ShopOffer(ItemStack offer, OverSizedItemStack currency) {
      public static ShopPedestalConfig.ShopOffer load(CompoundTag tag) {
         ItemStack i = ItemStack.of(tag.getCompound("offer"));
         OverSizedItemStack c = OverSizedItemStack.deserialize(tag.getCompound("currency"));
         return of(i, c);
      }

      public static ShopPedestalConfig.ShopOffer of(ItemStack offer, OverSizedItemStack currency) {
         return !offer.isEmpty() && !currency.overSizedStack().isEmpty() ? new ShopPedestalConfig.ShopOffer(offer, currency) : ShopPedestalConfig.EMPTY;
      }

      public void save(CompoundTag tag) {
         tag.put("offer", this.offer.save(new CompoundTag()));
         tag.put("currency", this.currency.serialize());
      }

      public boolean isEmpty() {
         return this == ShopPedestalConfig.EMPTY;
      }
   }

   private static final class ShopTier implements LevelEntryList.ILevelEntry {
      @Expose
      private int MIN_LEVEL;
      @Expose
      private WeightedList<ShopPedestalConfig.ShopEntry> TRADE_POOL;

      private ShopTier(int MIN_LEVEL, WeightedList<ShopPedestalConfig.ShopEntry> TRADE_POOL) {
         this.MIN_LEVEL = MIN_LEVEL;
         this.TRADE_POOL = TRADE_POOL;
      }

      @Override
      public int getLevel() {
         return this.MIN_LEVEL;
      }
   }
}

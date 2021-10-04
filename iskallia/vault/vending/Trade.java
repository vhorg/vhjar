package iskallia.vault.vending;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.nbt.INBTSerializable;
import iskallia.vault.util.nbt.NBTSerialize;

public class Trade implements INBTSerializable {
   @Expose
   @NBTSerialize
   protected Product buy;
   @Expose
   @NBTSerialize
   protected Product extra;
   @Expose
   @NBTSerialize
   protected Product sell;
   @Expose
   @NBTSerialize
   protected int max_trades;
   @Expose
   @NBTSerialize
   protected int times_traded;

   public Trade() {
      this.max_trades = -1;
   }

   public Trade(Product buy, Product extra, Product sell) {
      this.buy = buy;
      this.extra = extra;
      this.sell = sell;
   }

   public Trade(Product buy, Product extra, Product sell, int max_trades, int times_traded) {
      this(buy, extra, sell);
      this.max_trades = max_trades;
      this.times_traded = times_traded;
   }

   public Product getBuy() {
      return this.buy;
   }

   public Product getExtra() {
      return this.extra;
   }

   public Product getSell() {
      return this.sell;
   }

   public int getMaxTrades() {
      return this.max_trades;
   }

   public int getTimesTraded() {
      return this.times_traded;
   }

   public void setTimesTraded(int amount) {
      this.times_traded = amount;
   }

   public int getTradesLeft() {
      return this.max_trades == -1 ? -1 : Math.max(0, this.max_trades - this.times_traded);
   }

   public void onTraded() {
      this.times_traded++;
   }

   public boolean wasTradeUsed() {
      return this.max_trades == -1 ? true : this.times_traded > 0;
   }

   public boolean isValid() {
      if (this.buy == null || !this.buy.isValid()) {
         return false;
      } else {
         return this.sell == null || !this.sell.isValid() ? false : this.extra == null || this.extra.isValid();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Trade trade = (Trade)obj;
         return trade.sell.equals(this.sell) && trade.buy.equals(this.buy);
      }
   }

   public Trade setMaxTrades(int amount) {
      this.max_trades = amount;
      return this;
   }

   public Trade copy() {
      return new Trade(this.getBuy(), this.getExtra(), this.getSell(), this.max_trades, this.times_traded);
   }
}

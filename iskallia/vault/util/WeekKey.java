package iskallia.vault.util;

import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.Objects;
import net.minecraft.nbt.CompoundNBT;

public class WeekKey {
   private final int year;
   private final int week;

   private WeekKey(int year, int week) {
      this.year = year;
      this.week = week;
   }

   public int getYear() {
      return this.year;
   }

   public int getWeek() {
      return this.week;
   }

   public static WeekKey of(int year, int week) {
      return new WeekKey(year, week);
   }

   public static WeekKey current() {
      LocalDateTime ldt = LocalDateTime.now();
      int year = ldt.get(IsoFields.WEEK_BASED_YEAR);
      int week = ldt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
      return new WeekKey(year, week);
   }

   public static WeekKey previous() {
      LocalDateTime ldt = LocalDateTime.now().minusWeeks(1L);
      int year = ldt.get(IsoFields.WEEK_BASED_YEAR);
      int week = ldt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
      return new WeekKey(year, week);
   }

   public CompoundNBT serialize() {
      CompoundNBT tag = new CompoundNBT();
      tag.func_74768_a("year", this.year);
      tag.func_74768_a("week", this.week);
      return tag;
   }

   public static WeekKey deserialize(CompoundNBT tag) {
      return new WeekKey(tag.func_74762_e("year"), tag.func_74762_e("week"));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         WeekKey weekKey = (WeekKey)o;
         return this.year == weekKey.year && this.week == weekKey.week;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.year, this.week);
   }
}

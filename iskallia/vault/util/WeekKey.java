package iskallia.vault.util;

import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;

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

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("year", this.year);
      tag.putInt("week", this.week);
      return tag;
   }

   public static WeekKey deserialize(CompoundTag tag) {
      return new WeekKey(tag.getInt("year"), tag.getInt("week"));
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

package iskallia.vault.entity.boss;

import net.minecraft.nbt.CompoundTag;

public class SparkStageAttributes {
   final int radius;
   final int minSparkCount;
   final int maxSparkCount;
   final int sparkLifespan;
   final int sparkSpawnInterval;
   final int health;
   final int stunTimePerSpark;
   public double damagePerSpark;

   public SparkStageAttributes(
      int radius, int minSparkCount, int maxSparkCount, int sparkLifespan, int sparkSpawnInterval, int health, int stunTimePerSpark, double damagePerSpark
   ) {
      this.radius = radius;
      this.minSparkCount = minSparkCount;
      this.maxSparkCount = maxSparkCount;
      this.sparkLifespan = sparkLifespan;
      this.sparkSpawnInterval = sparkSpawnInterval;
      this.health = health;
      this.stunTimePerSpark = stunTimePerSpark;
      this.damagePerSpark = damagePerSpark;
   }

   public static SparkStageAttributes from(CompoundTag tag) {
      return new SparkStageAttributes(
         tag.getInt("Radius"),
         tag.getInt("MinSparkCount"),
         tag.getInt("MaxSparkCount"),
         tag.getInt("SparkLifespan"),
         tag.getInt("SparkSpawnInterval"),
         tag.getInt("Health"),
         tag.getInt("StunTimePerSpark"),
         tag.getDouble("DamagePerSpark")
      );
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("Radius", this.radius);
      tag.putInt("MinSparkCount", this.minSparkCount);
      tag.putInt("MaxSparkCount", this.maxSparkCount);
      tag.putInt("SparkLifespan", this.sparkLifespan);
      tag.putInt("SparkSpawnInterval", this.sparkSpawnInterval);
      tag.putInt("Health", this.health);
      tag.putInt("StunTimePerSpark", this.stunTimePerSpark);
      tag.putDouble("DamagePerSpark", this.damagePerSpark);
      return tag;
   }
}

package iskallia.vault.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class Entropy {
   private static final Entropy INSTANCE = new Entropy();
   private static final Random rand = new Random();
   private final Map<UUID, Map<Entropy.Stat, Float>> playerStatMap = new HashMap<>();

   private Entropy() {
   }

   private synchronized Map<Entropy.Stat, Float> getEntropyStats(UUID uuid) {
      return this.playerStatMap.computeIfAbsent(uuid, id -> new HashMap<>());
   }

   public static boolean canExecute(Entity entity, Entropy.Stat stat, float chance) {
      if (chance >= 1.0F) {
         return true;
      } else if (chance <= 0.0F) {
         return false;
      } else {
         boolean randomHit = rand.nextFloat() < chance;
         if (!(entity instanceof Player)) {
            return randomHit;
         } else {
            UUID playerId = entity.getUUID();
            Map<Entropy.Stat, Float> entropyStats = INSTANCE.getEntropyStats(playerId);
            float entropy = entropyStats.computeIfAbsent(stat, s -> 0.0F);
            if (randomHit) {
               entropyStats.put(stat, --entropy);
               return true;
            } else {
               entropy += chance;
               if (entropy >= 1.0F) {
                  entropyStats.put(stat, Math.max(--entropy, -2.0F));
                  return true;
               } else {
                  entropyStats.put(stat, entropy);
                  return false;
               }
            }
         }
      }
   }

   public static class Stat {
      public static final Entropy.Stat BLOCK = new Entropy.Stat(0);
      public static final Entropy.Stat THORNS = new Entropy.Stat(1);
      public static final Entropy.Stat FATAL_STRIKE = new Entropy.Stat(2);
      public static final Entropy.Stat VANILLA_CRITICAL = new Entropy.Stat(3);
      public static final Entropy.Stat STUN_ATTACK_CHANCE = new Entropy.Stat(4);
      public static final Entropy.Stat KNOCKBACK_ATTACK_CHANCE = new Entropy.Stat(5);
      private final int hash;

      private Stat(int hash) {
         this.hash = hash;
      }

      public static Entropy.Stat effectCloud(MobEffect effect) {
         return new Entropy.Stat(effect.getRegistryName().hashCode());
      }

      public static Entropy.Stat effectCloudWhenHit(MobEffect effect) {
         return new Entropy.Stat(31 * effect.getRegistryName().hashCode());
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Entropy.Stat stat = (Entropy.Stat)o;
            return this.hash == stat.hash;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }
}

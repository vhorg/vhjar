package iskallia.vault.world.vault.logic.objective.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public abstract class RaidModifier {
   protected static final Random rand = new Random();
   @Expose
   private final boolean isPercentage;
   @Expose
   private final boolean isPositive;
   @Expose
   private final String name;

   protected RaidModifier(boolean isPercentage, boolean isPositive, String name) {
      this.isPercentage = isPercentage;
      this.isPositive = isPositive;
      this.name = name;
   }

   public boolean isPercentage() {
      return this.isPercentage;
   }

   public boolean isPositive() {
      return this.isPositive;
   }

   public String getName() {
      return this.name;
   }

   public abstract void affectRaidMob(Mob var1, float var2);

   public abstract void onVaultRaidFinish(VaultRaid var1, ServerLevel var2, BlockPos var3, ActiveRaid var4, float var5);

   public abstract Component getDisplay(float var1);

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RaidModifier modifier = (RaidModifier)o;
         return Objects.equals(this.name, modifier.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name);
   }
}

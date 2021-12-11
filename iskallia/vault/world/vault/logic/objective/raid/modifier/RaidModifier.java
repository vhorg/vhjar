package iskallia.vault.world.vault.logic.objective.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import java.util.Objects;
import java.util.Random;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

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

   public abstract void affectRaidMob(MobEntity var1, float var2);

   public abstract void onVaultRaidFinish(VaultRaid var1, ServerWorld var2, BlockPos var3, ActiveRaid var4, float var5);

   public abstract ITextComponent getDisplay(float var1);

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

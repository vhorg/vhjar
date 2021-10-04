package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.gen.layout.DiamondRoomLayout;
import iskallia.vault.world.vault.gen.layout.SquareRoomLayout;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class VaultSizeConfig extends Config {
   @Expose
   private VaultSizeConfig.SizeLayout defaultLayout;
   @Expose
   private VaultSizeConfig.SizeLayout raffleLayout;
   @Expose
   private final List<VaultSizeConfig.Level> levels = new ArrayList<>();

   @Override
   public String getName() {
      return "vault_size";
   }

   @Override
   protected void reset() {
      this.raffleLayout = new VaultSizeConfig.SizeLayout(11, 6.0F, DiamondRoomLayout.ID);
      this.defaultLayout = new VaultSizeConfig.SizeLayout(11, 6.0F, DiamondRoomLayout.ID);
      this.levels.clear();
      VaultSizeConfig.SizeLayout l1 = new VaultSizeConfig.SizeLayout(7, 3.0F, DiamondRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(0, new WeightedList<VaultSizeConfig.SizeLayout>().add(l1, 1)));
      VaultSizeConfig.SizeLayout l2 = new VaultSizeConfig.SizeLayout(9, 4.5F, DiamondRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(25, new WeightedList<VaultSizeConfig.SizeLayout>().add(l2, 1)));
      VaultSizeConfig.SizeLayout l3 = new VaultSizeConfig.SizeLayout(11, 6.0F, DiamondRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(50, new WeightedList<VaultSizeConfig.SizeLayout>().add(l3, 1)));
      VaultSizeConfig.SizeLayout l41 = new VaultSizeConfig.SizeLayout(13, 9.0F, DiamondRoomLayout.ID);
      VaultSizeConfig.SizeLayout l42 = new VaultSizeConfig.SizeLayout(13, 9.0F, SquareRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(75, new WeightedList<VaultSizeConfig.SizeLayout>().add(l41, 2).add(l42, 1)));
      VaultSizeConfig.SizeLayout l51 = new VaultSizeConfig.SizeLayout(15, 10.0F, DiamondRoomLayout.ID);
      VaultSizeConfig.SizeLayout l52 = new VaultSizeConfig.SizeLayout(15, 10.0F, SquareRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(100, new WeightedList<VaultSizeConfig.SizeLayout>().add(l51, 2).add(l52, 1)));
      VaultSizeConfig.SizeLayout l61 = new VaultSizeConfig.SizeLayout(17, 10.0F, DiamondRoomLayout.ID);
      VaultSizeConfig.SizeLayout l62 = new VaultSizeConfig.SizeLayout(15, 12.0F, SquareRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(125, new WeightedList<VaultSizeConfig.SizeLayout>().add(l61, 2).add(l62, 1)));
      VaultSizeConfig.SizeLayout l71 = new VaultSizeConfig.SizeLayout(19, 12.0F, DiamondRoomLayout.ID);
      VaultSizeConfig.SizeLayout l72 = new VaultSizeConfig.SizeLayout(17, 14.5F, SquareRoomLayout.ID);
      this.levels.add(new VaultSizeConfig.Level(150, new WeightedList<VaultSizeConfig.SizeLayout>().add(l71, 2).add(l72, 1)));
   }

   @Nonnull
   public VaultSizeConfig.SizeLayout getLayout(int vaultLevel, boolean isRaffle) {
      if (isRaffle) {
         return this.raffleLayout;
      } else {
         VaultSizeConfig.Level levelConfig = this.getForLevel(this.levels, vaultLevel);
         if (levelConfig == null) {
            return this.defaultLayout;
         } else {
            VaultSizeConfig.SizeLayout layout = levelConfig.outcomes.getRandom(rand);
            return layout == null ? this.defaultLayout : layout;
         }
      }
   }

   @Nullable
   public VaultSizeConfig.Level getForLevel(List<VaultSizeConfig.Level> levels, int level) {
      for (int i = 0; i < levels.size(); i++) {
         if (level < levels.get(i).level) {
            if (i != 0) {
               return levels.get(i - 1);
            }
            break;
         }

         if (i == levels.size() - 1) {
            return levels.get(i);
         }
      }

      return null;
   }

   public static class Level {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<VaultSizeConfig.SizeLayout> outcomes;

      public Level(int level, WeightedList<VaultSizeConfig.SizeLayout> outcomes) {
         this.level = level;
         this.outcomes = outcomes;
      }
   }

   public static class SizeLayout {
      @Expose
      private final int size;
      @Expose
      private final float objectiveRoomRatio;
      @Expose
      private final String layout;

      public SizeLayout(int size, float objectiveRoomRatio, ResourceLocation layout) {
         this(size, objectiveRoomRatio, layout.toString());
      }

      public SizeLayout(int size, float objectiveRoomRatio, String layout) {
         this.size = size;
         this.objectiveRoomRatio = objectiveRoomRatio;
         this.layout = layout;
      }

      public int getSize() {
         return this.size;
      }

      public float getObjectiveRoomRatio() {
         return this.objectiveRoomRatio;
      }

      public ResourceLocation getLayout() {
         return new ResourceLocation(this.layout);
      }
   }
}

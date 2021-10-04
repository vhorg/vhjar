package iskallia.vault.world.data;

import iskallia.vault.container.GlobalDifficultyContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.StringUtils;

public class GlobalDifficultyData extends WorldSavedData {
   protected static final String DATA_NAME = "the_vault_GlobalDifficulty";
   private GlobalDifficultyData.Difficulty crystalCost = null;
   private GlobalDifficultyData.Difficulty vaultDifficulty = null;

   public GlobalDifficultyData() {
      this("the_vault_GlobalDifficulty");
   }

   public GlobalDifficultyData(String name) {
      super(name);
   }

   public GlobalDifficultyData.Difficulty getCrystalCost() {
      return this.crystalCost;
   }

   public void setCrystalCost(GlobalDifficultyData.Difficulty crystalCost) {
      this.crystalCost = crystalCost;
      this.func_76185_a();
   }

   public GlobalDifficultyData.Difficulty getVaultDifficulty() {
      return this.vaultDifficulty;
   }

   public void setVaultDifficulty(GlobalDifficultyData.Difficulty vaultDifficulty) {
      this.vaultDifficulty = vaultDifficulty;
      this.func_76185_a();
   }

   public void openDifficultySelection(ServerPlayerEntity sPlayer) {
      if (ServerLifecycleHooks.getCurrentServer() != null
         && (!ServerLifecycleHooks.getCurrentServer().func_71262_S() || sPlayer.func_211513_k(sPlayer.func_184102_h().func_110455_j()))
         && (this.getVaultDifficulty() == null || this.getCrystalCost() == null)) {
         final CompoundNBT data = new CompoundNBT();
         data.func_74768_a("VaultDifficulty", GlobalDifficultyData.Difficulty.STANDARD.ordinal());
         data.func_74768_a("CrystalCost", GlobalDifficultyData.Difficulty.STANDARD.ordinal());
         NetworkHooks.openGui(sPlayer, new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new StringTextComponent("Welcome Vault Hunter!");
            }

            @Nullable
            public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
               return new GlobalDifficultyContainer(windowId, data);
            }
         }, buffer -> buffer.func_150786_a(data));
      }
   }

   public void func_76184_a(CompoundNBT nbt) {
      if (nbt.func_74764_b("CrystalCost")) {
         this.crystalCost = GlobalDifficultyData.Difficulty.values()[nbt.func_74762_e("CrystalCost")];
      }

      if (nbt.func_74764_b("VaultDifficulty")) {
         this.vaultDifficulty = GlobalDifficultyData.Difficulty.values()[nbt.func_74762_e("VaultDifficulty")];
      }
   }

   public CompoundNBT func_189551_b(CompoundNBT nbt) {
      if (this.crystalCost != null) {
         nbt.func_74768_a("CrystalCost", this.crystalCost.ordinal());
      }

      if (this.vaultDifficulty != null) {
         nbt.func_74768_a("VaultDifficulty", this.vaultDifficulty.ordinal());
      }

      return nbt;
   }

   public static GlobalDifficultyData get(ServerWorld world) {
      return (GlobalDifficultyData)world.func_73046_m().func_241755_D_().func_217481_x().func_215752_a(GlobalDifficultyData::new, "the_vault_GlobalDifficulty");
   }

   public static enum Difficulty {
      TRIVIAL(0.5),
      CASUAL(0.75),
      STANDARD(1.0),
      HARD(1.25),
      EXTREME(1.5);

      double multiplier;

      private Difficulty(double multiplier) {
         this.multiplier = multiplier;
      }

      public double getMultiplier() {
         return this.multiplier;
      }

      @Override
      public String toString() {
         return StringUtils.capitalize(this.name().toLowerCase());
      }

      public GlobalDifficultyData.Difficulty getNext() {
         int index = this.ordinal() + 1;
         if (index >= values().length) {
            index = 0;
         }

         return values()[index];
      }
   }
}

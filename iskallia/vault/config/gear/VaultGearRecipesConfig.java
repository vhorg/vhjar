package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.crafting.recipe.TrinketForgeRecipe;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.gear.crafting.recipe.VaultGearForgeRecipe;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultForgeRecipeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VaultGearRecipesConfig extends Config {
   @Expose
   private final List<VaultGearRecipesConfig.GearRecipe> gearRecipes = new ArrayList<>();
   @Expose
   private final List<VaultGearRecipesConfig.TrinketRecipe> trinketRecipes = new ArrayList<>();

   @Override
   public String getName() {
      return "gear%sgear_recipes".formatted(File.separator);
   }

   public List<VaultForgeRecipe> getAllRecipes() {
      List<VaultForgeRecipe> recipes = new ArrayList<>();
      this.gearRecipes.forEach(recipe -> recipes.add(recipe.makeRecipe()));
      this.trinketRecipes.forEach(recipe -> recipes.add(recipe.makeRecipe()));
      return recipes;
   }

   @Nullable
   public VaultForgeRecipe getRecipe(ResourceLocation id) {
      for (VaultGearRecipesConfig.GearRecipe recipe : this.gearRecipes) {
         if (id.equals(recipe.id)) {
            return recipe.makeRecipe();
         }
      }

      for (VaultGearRecipesConfig.TrinketRecipe recipex : this.trinketRecipes) {
         if (id.equals(recipex.id)) {
            return recipex.makeRecipe();
         }
      }

      return null;
   }

   @Override
   protected void reset() {
      this.gearRecipes.clear();
      this.trinketRecipes.clear();

      for (ProficiencyType type : ProficiencyType.getCraftableTypes()) {
         ItemStack out = new ItemStack(type.getDisplayStack().get().getItem());
         this.gearRecipes.add(new VaultGearRecipesConfig.GearRecipe(out, type).addInput(new ItemStack(Items.DIAMOND, 2)));
      }

      for (TrinketEffect<?> trinket : TrinketEffectRegistry.getOrderedEntries()) {
         this.trinketRecipes.add(new VaultGearRecipesConfig.TrinketRecipe(trinket).addInput(new ItemStack(Items.DIAMOND, 2)));
      }
   }

   @Override
   public <T extends Config> T readConfig() {
      T cfg = super.readConfig();
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      if (srv != null) {
         srv.getPlayerList().getPlayers().forEach(player -> this.syncTo((VaultGearRecipesConfig)cfg, player));
      }

      return cfg;
   }

   public void syncTo(VaultGearRecipesConfig cfg, ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(new VaultForgeRecipeMessage(cfg.getAllRecipes()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static class ForgeRecipe {
      @Expose
      protected final ResourceLocation id;
      @Expose
      protected final ItemEntry output;
      @Expose
      protected final List<ItemEntry> inputs = new ArrayList<>();

      public ForgeRecipe(ItemStack out) {
         this(out.getItem().getRegistryName(), out);
      }

      public ForgeRecipe(ResourceLocation id, ItemStack out) {
         this.id = id;
         this.output = new ItemEntry(out);
      }

      public <T extends VaultGearRecipesConfig.ForgeRecipe> T addInput(ItemStack in) {
         this.inputs.add(new ItemEntry(in));
         return (T)this;
      }

      public VaultForgeRecipe makeRecipe() {
         ItemStack out = this.output.createItemStack();
         List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
         return new VaultForgeRecipe(this.id, out, in);
      }
   }

   public static class GearRecipe extends VaultGearRecipesConfig.ForgeRecipe {
      @Expose
      private final ProficiencyType proficiencyType;

      public GearRecipe(ItemStack output, ProficiencyType proficiencyType) {
         super(output);
         this.proficiencyType = proficiencyType;
      }

      @Override
      public VaultForgeRecipe makeRecipe() {
         ItemStack out = this.output.createItemStack();
         List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
         return new VaultGearForgeRecipe(this.id, out, in, this.proficiencyType);
      }
   }

   public static class TrinketRecipe extends VaultGearRecipesConfig.ForgeRecipe {
      @Expose
      private final ResourceLocation trinket;

      public TrinketRecipe(TrinketEffect<?> trinket) {
         super(trinket.getRegistryName(), new ItemStack(ModItems.TRINKET));
         this.trinket = trinket.getRegistryName();
      }

      @Override
      public VaultForgeRecipe makeRecipe() {
         TrinketEffect<?> trinket = TrinketEffectRegistry.getEffect(this.trinket);
         if (trinket == null) {
            throw new IllegalArgumentException("Unknown trinket: " + this.trinket.toString());
         } else {
            ItemStack out = this.output.createItemStack();
            List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
            return new TrinketForgeRecipe(this.id, out, in, trinket);
         }
      }
   }
}

package iskallia.vault.config.tool;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.ToolStationRecipe;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.item.tool.ToolType;
import iskallia.vault.network.message.ToolStationRecipeMessage;
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

public class ToolRecipesConfig extends Config {
   @Expose
   private final List<ToolRecipesConfig.ToolRecipe> toolRecipes = new ArrayList<>();

   @Override
   public String getName() {
      return "tool%stool_recipes".formatted(File.separator);
   }

   public List<VaultForgeRecipe> getAllRecipes() {
      List<VaultForgeRecipe> recipes = new ArrayList<>();
      this.toolRecipes.forEach(recipe -> recipes.add(recipe.makeRecipe()));
      return recipes;
   }

   @Nullable
   public VaultForgeRecipe getRecipe(ResourceLocation id) {
      for (ToolRecipesConfig.ToolRecipe recipe : this.toolRecipes) {
         if (id.equals(recipe.id)) {
            return recipe.makeRecipe();
         }
      }

      return null;
   }

   @Override
   protected void reset() {
      this.toolRecipes.clear();
      ToolType[] basicTypes = new ToolType[]{ToolType.PICK, ToolType.AXE, ToolType.SHOVEL, ToolType.HAMMER, ToolType.SICKLE};

      for (ToolMaterial toolMaterial : ToolMaterial.values()) {
         for (ToolType toolType : basicTypes) {
            ItemStack out = ToolItem.create(toolMaterial, toolType);
            this.toolRecipes.add(new ToolRecipesConfig.ToolRecipe(out, toolType, toolMaterial).addInput(new ItemStack(Items.DIAMOND, 2)));
         }
      }
   }

   @Override
   public <T extends Config> T readConfig() {
      T cfg = super.readConfig();
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      if (srv != null) {
         srv.getPlayerList().getPlayers().forEach(player -> this.syncTo((ToolRecipesConfig)cfg, player));
      }

      return cfg;
   }

   public void syncTo(ToolRecipesConfig cfg, ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(new ToolStationRecipeMessage(cfg.getAllRecipes()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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

      public <T extends ToolRecipesConfig.ForgeRecipe> T addInput(ItemStack in) {
         this.inputs.add(new ItemEntry(in));
         return (T)this;
      }

      public VaultForgeRecipe makeRecipe() {
         ItemStack out = this.output.createItemStack();
         List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
         return new VaultForgeRecipe(this.id, out, in);
      }
   }

   public static class ToolRecipe extends ToolRecipesConfig.ForgeRecipe {
      @Expose
      private final ToolType toolType;
      @Expose
      private final ToolMaterial toolMaterial;

      public ToolRecipe(ItemStack output, ToolType toolType, ToolMaterial toolMaterial) {
         super(VaultMod.id("tool/" + toolType.getId() + "/" + toolMaterial.getId()), output);
         this.toolType = toolType;
         this.toolMaterial = toolMaterial;
      }

      @Override
      public VaultForgeRecipe makeRecipe() {
         ItemStack out = this.output.createItemStack();
         List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
         return new ToolStationRecipe(this.id, out, in, this.toolType, this.toolMaterial);
      }
   }
}

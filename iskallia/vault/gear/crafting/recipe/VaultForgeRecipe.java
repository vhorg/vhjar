package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class VaultForgeRecipe {
   private final ForgeRecipeType type;
   private final ResourceLocation id;
   protected final ItemStack output;
   private final List<ItemStack> inputs = new ArrayList<>();

   protected VaultForgeRecipe(ForgeRecipeType type, ResourceLocation id, ItemStack output) {
      this(type, id, output, new ArrayList<>());
   }

   public VaultForgeRecipe(ForgeRecipeType type, ResourceLocation id, ItemStack output, List<ItemStack> inputs) {
      this.type = type;
      this.id = id;
      this.output = output;
      this.inputs.addAll(inputs);
   }

   protected void readAdditional(FriendlyByteBuf buf) {
   }

   protected void writeAdditional(FriendlyByteBuf buf) {
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public List<ItemStack> getInputs() {
      return Collections.unmodifiableList(this.inputs);
   }

   protected ItemStack getRawOutput() {
      return this.output.copy();
   }

   public ItemStack getDisplayOutput() {
      return this.getRawOutput();
   }

   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter) {
      return this.getRawOutput();
   }

   public boolean canCraft(Player player) {
      return true;
   }

   public Component getDisabledText() {
      return new TextComponent("Undiscovered").withStyle(ChatFormatting.ITALIC);
   }

   public static VaultForgeRecipe read(FriendlyByteBuf buf) {
      ForgeRecipeType recipeType = (ForgeRecipeType)buf.readEnum(ForgeRecipeType.class);
      ResourceLocation id = buf.readResourceLocation();
      ItemStack out = buf.readItem();
      VaultForgeRecipe recipe = recipeType.makeRecipe(id, out);
      ((ArrayList)NetcodeUtils.readCollection(buf, ArrayList::new, buffer -> OverSizedItemStack.read(buf).overSizedStack())).forEach(recipe.inputs::add);
      recipe.readAdditional(buf);
      return recipe;
   }

   public final void write(FriendlyByteBuf buf) {
      buf.writeEnum(this.type);
      buf.writeResourceLocation(this.getId());
      buf.writeItemStack(this.output, false);
      NetcodeUtils.writeCollection(buf, this.inputs, (stack, buffer) -> new OverSizedItemStack(stack, stack.getCount()).write(buf));
      this.writeAdditional(buf);
   }
}

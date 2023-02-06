package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.util.NetcodeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultForgeRecipe {
   private final ResourceLocation id;
   protected final ItemStack output;
   private final List<ItemStack> inputs = new ArrayList<>();

   protected VaultForgeRecipe(ResourceLocation id, ItemStack output) {
      this.id = id;
      this.output = output;
   }

   public VaultForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs) {
      this.id = id;
      this.output = output;
      this.inputs.addAll(inputs);
   }

   protected int getClassId() {
      return 0;
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

   private static BiFunction<ResourceLocation, ItemStack, VaultForgeRecipe> ctor(int id) {
      switch (id) {
         case 0:
            return VaultForgeRecipe::new;
         case 1:
            return VaultGearForgeRecipe::new;
         case 2:
            return TrinketForgeRecipe::new;
         case 3:
            return ToolStationRecipe::new;
         case 4:
            return JewelRecipe::new;
         default:
            throw new IllegalArgumentException("Unknown forge recipe type: " + id);
      }
   }

   public static VaultForgeRecipe read(FriendlyByteBuf buf) {
      int classId = buf.readInt();
      ResourceLocation id = buf.readResourceLocation();
      ItemStack out = buf.readItem();
      VaultForgeRecipe recipe = ctor(classId).apply(id, out);
      ((ArrayList)NetcodeUtils.readCollection(buf, ArrayList::new, buffer -> OverSizedItemStack.read(buf).overSizedStack())).forEach(recipe.inputs::add);
      recipe.readAdditional(buf);
      return recipe;
   }

   public final void write(FriendlyByteBuf buf) {
      buf.writeInt(this.getClassId());
      buf.writeResourceLocation(this.getId());
      buf.writeItemStack(this.output, false);
      NetcodeUtils.writeCollection(buf, this.inputs, (stack, buffer) -> new OverSizedItemStack(stack, stack.getCount()).write(buf));
      this.writeAdditional(buf);
   }
}

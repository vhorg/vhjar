package iskallia.vault.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.config.adapter.ItemStackAdapter;
import iskallia.vault.config.adapter.RegistryCodecAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

public class HandCommand extends Command {
   private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Item.class, RegistryCodecAdapter.of(ForgeRegistries.ITEMS))
      .registerTypeAdapterFactory(ItemStackAdapter.FACTORY)
      .create();

   @Override
   public String getName() {
      return "hand";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.executes(this::hand);
   }

   private int hand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      if (((CommandSourceStack)ctx.getSource()).getEntityOrException() instanceof Player player) {
         ItemStack itemStack = player.getMainHandItem();
         String json = this.gson.toJson(itemStack);
         Minecraft minecraft = Minecraft.getInstance();
         GLFW.glfwSetClipboardString(minecraft.getWindow().getWindow(), json);
         minecraft.gui.getChat().addMessage(new TextComponent(json));
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}

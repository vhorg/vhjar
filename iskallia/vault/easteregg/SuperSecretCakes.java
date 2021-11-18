package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import iskallia.vault.util.AdvancementHelper;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class SuperSecretCakes {
   public static final String[] CAKE_QUOTES = new String[]{
      "The cake is a lie",
      "You can have cake and eat it too?",
      "Would like some tea with that?",
      "The cake equals Ï€ (Pi) ?",
      "This cake is made with love",
      "DONT GET GREEDY",
      "The cake is a pine?",
      "That'll go right to your thighs",
      "Have you got the coffee?",
      "When life gives you cake you eat it",
      "The cake says 'goodbye'",
      "The pie want to cry",
      "It's a piece of cake to bake a pretty cake",
      "The cherries are a lie",
      "1000 calories",
      "Icing on the cake!",
      "Happy Birthday! Is it your birthday?",
      "This is caketastic!",
      "An actual pie chart",
      "Arrr! I'm a Pie-rate",
      "Not every pies in the world is round, sometimes... pi * r ^ 2",
      "HALLO!",
      "#NeverLeaving cause cake sticks to you",
      "Tell me lies, tell me sweet little pies",
      "Diet...what diet!!!!",
      "I'll take the three story pie and a diet coke... don't want to get fat",
      "This is the end of all cake"
   };

   @SubscribeEvent
   public static void onCakePlaced(EntityPlaceEvent event) {
      if (!event.getWorld().func_201670_d()) {
         if (((ServerWorld)event.getWorld()).func_234923_W_() == Vault.VAULT_KEY) {
            if (event.getPlacedBlock().func_177230_c() == Blocks.field_150414_aQ) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCakeEat(RightClickBlock event) {
      if (event.getWorld().func_234923_W_() == Vault.VAULT_KEY) {
         if (event.getWorld().func_180495_p(event.getPos()).func_177230_c() == Blocks.field_150414_aQ) {
            if (event.getSide() == LogicalSide.CLIENT) {
               Random random = new Random();
               String cakeQuote = CAKE_QUOTES[random.nextInt(CAKE_QUOTES.length)];
               StringTextComponent text = new StringTextComponent("\"" + cakeQuote + "\"");
               text.func_230530_a_(Style.field_240709_b_.func_240722_b_(true).func_240718_a_(Color.func_240743_a_(-15343)));
               event.getPlayer().func_146105_b(text, true);
            } else {
               event.getPlayer().func_195064_c(new EffectInstance(Effects.field_76444_x, 1200, 0));
               event.getWorld().func_175655_b(event.getPos(), false);
               AdvancementHelper.grantCriterion((ServerPlayerEntity)event.getPlayer(), Vault.id("main/super_secret_cakes"), "cake_consumed");
               event.setCanceled(true);
            }
         }
      }
   }
}

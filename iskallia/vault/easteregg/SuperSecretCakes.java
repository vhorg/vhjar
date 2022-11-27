package iskallia.vault.easteregg;

import iskallia.vault.VaultMod;
import iskallia.vault.util.AdvancementHelper;
import iskallia.vault.world.data.ServerVaults;
import java.util.Random;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
      "The cake equals π (Pi) ?",
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
      LevelAccessor levelAccessor = event.getWorld();
      if (!levelAccessor.isClientSide()) {
         if (levelAccessor instanceof Level level && ServerVaults.isVaultWorld(level) && event.getPlacedBlock().getBlock() == Blocks.CAKE) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onCakeEat(RightClickBlock event) {
      Player player = event.getPlayer();
      Level world = event.getWorld();
      if (ServerVaults.isVaultWorld(world)) {
         if (!player.isSpectator()) {
            if (world.getBlockState(event.getPos()).getBlock() instanceof CakeBlock) {
               if (world.isClientSide()) {
                  Random random = new Random();
                  String cakeQuote = CAKE_QUOTES[random.nextInt(CAKE_QUOTES.length)];
                  TextComponent text = new TextComponent("\"" + cakeQuote + "\"");
                  text.setStyle(Style.EMPTY.withItalic(true).withColor(TextColor.fromRgb(-15343)));
                  player.displayClientMessage(text, true);
               } else if (world instanceof ServerLevel sWorld && player instanceof ServerPlayer sPlayer) {
                  world.destroyBlock(event.getPos(), false);
                  player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0));
                  AdvancementHelper.grantCriterion(sPlayer, VaultMod.id("main/super_secret_cakes"), "cake_consumed");
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}

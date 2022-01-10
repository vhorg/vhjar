package iskallia.vault.easteregg;

import iskallia.vault.Vault;
import iskallia.vault.util.AdvancementHelper;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.objective.CakeHuntObjective;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
         World world = event.getWorld();
         PlayerEntity player = event.getPlayer();
         if (world.func_180495_p(event.getPos()).func_177230_c() instanceof CakeBlock) {
            if (world.func_201670_d()) {
               Random random = new Random();
               String cakeQuote = CAKE_QUOTES[random.nextInt(CAKE_QUOTES.length)];
               StringTextComponent text = new StringTextComponent("\"" + cakeQuote + "\"");
               text.func_230530_a_(Style.field_240709_b_.func_240722_b_(true).func_240718_a_(Color.func_240743_a_(-15343)));
               player.func_146105_b(text, true);
            } else if (world instanceof ServerWorld && player instanceof ServerPlayerEntity) {
               ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
               ServerWorld sWorld = (ServerWorld)world;
               world.func_175655_b(event.getPos(), false);
               player.func_195064_c(new EffectInstance(Effects.field_76444_x, 1200, 0));
               AdvancementHelper.grantCriterion(sPlayer, Vault.id("main/super_secret_cakes"), "cake_consumed");
               VaultRaid raid = VaultRaidData.get(sWorld).getAt(sWorld, event.getPos());
               if (raid != null) {
                  raid.getGenerator().getPiecesAt(event.getPos(), VaultRoom.class).forEach(room -> room.setCakeEaten(true));
                  raid.getActiveObjective(CakeHuntObjective.class).ifPresent(cakeObjective -> cakeObjective.expandVault(sWorld, event.getPos(), raid));
               }

               event.setCanceled(true);
            }
         }
      }
   }
}

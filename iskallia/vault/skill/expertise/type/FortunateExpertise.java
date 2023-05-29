package iskallia.vault.skill.expertise.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.skill.ability.effect.spi.AbstractVeinMinerAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.util.BlockBreakHandler;
import iskallia.vault.util.OverlevelEnchantHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class FortunateExpertise extends LearnableSkill {
   private static final BlockBreakHandler blockBreakHandler = new BlockBreakHandler() {
      @Override
      protected int getBlockLimit(Player player) {
         return 1;
      }

      @Override
      protected ItemStack getMiningItemProxy(Player player) {
         ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND).copy();
         return OverlevelEnchantHelper.increaseFortuneBy(stack, this.getAdditionalFortuneLevels(player));
      }

      private int getAdditionalFortuneLevels(Player player) {
         return player instanceof ServerPlayer serverPlayer
            ? PlayerExpertisesData.get(serverPlayer.getLevel())
               .getExpertises(serverPlayer)
               .getAll(FortunateExpertise.class, Skill::isUnlocked)
               .stream()
               .mapToInt(FortunateExpertise::getAdditionalFortuneLevels)
               .sum()
            : 0;
      }

      @Override
      protected boolean shouldVoid(ServerLevel level, ServerPlayer player, BlockState blockState) {
         return PlayerAbilitiesData.get(player.getLevel())
            .getAbilities(player)
            .getAll(AbstractVeinMinerAbility.class, Ability::isActive)
            .stream()
            .findFirst()
            .map(ability -> ability.shouldVoid(level, player, blockState))
            .orElse(false);
      }
   };
   private int additionalFortuneLevels;

   @SubscribeEvent
   public static void onBlockMined(BreakEvent event) {
      if (!event.getWorld().isClientSide()
         && !(event.getPlayer() instanceof FakePlayer)
         && event.getPlayer() instanceof ServerPlayer player
         && event.getWorld() instanceof ServerLevel level
         && event.getPlayer().getUsedItemHand() == InteractionHand.MAIN_HAND
         && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, event.getPlayer().getMainHandItem()) > 0) {
         ActiveFlags.IS_FORTUNE_MINING.runIfNotSet(() -> {
            if (!PlayerExpertisesData.get(level).getExpertises(player).getAll(FortunateExpertise.class, Skill::isUnlocked).isEmpty()) {
               BlockPos pos = event.getPos();
               BlockState blockState = level.getBlockState(pos);
               if (blockBreakHandler.areaDig(level, player, pos, blockState.getBlock())) {
                  event.setCanceled(true);
               }
            }
         });
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.additionalFortuneLevels), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.additionalFortuneLevels = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.additionalFortuneLevels)).ifPresent(tag -> nbt.put("additionalFortuneLevels", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.additionalFortuneLevels = Adapters.INT.readNbt(nbt.get("additionalFortuneLevels")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.additionalFortuneLevels)).ifPresent(element -> json.add("additionalFortuneLevels", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.additionalFortuneLevels = Adapters.INT.readJson(json.get("additionalFortuneLevels")).orElseThrow();
   }

   public int getAdditionalFortuneLevels() {
      return this.additionalFortuneLevels;
   }
}

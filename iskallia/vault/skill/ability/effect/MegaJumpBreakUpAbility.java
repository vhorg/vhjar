package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.ServerVaults;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class MegaJumpBreakUpAbility extends MegaJumpAbility {
   private int ticks;

   public MegaJumpBreakUpAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int height) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, height);
      this.ticks = 0;
   }

   public MegaJumpBreakUpAbility() {
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> super.canDoAction(context) && ServerVaults.get(player.level).isPresent()).orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         super.doAction(context);
         this.ticks = 30;
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.START && !event.player.getCommandSenderWorld().isClientSide() && event.player.getCommandSenderWorld() instanceof ServerLevel) {
         Player player = event.player;
         ServerLevel world = (ServerLevel)player.getCommandSenderWorld();
         AbilityTree abilities = PlayerAbilitiesData.get(world).getAbilities(player);
         Ability focusedAbilityNode = abilities.getSelectedAbility(player).orElse(null);

         for (MegaJumpBreakUpAbility ability : abilities.getAll(MegaJumpBreakUpAbility.class, Skill::isUnlocked)) {
            if (ability.ticks > 0) {
               int ticks = ability.ticks;
               if (--ticks <= 0) {
                  ability.ticks = 0;
               } else {
                  ability.ticks = ticks;
                  if (focusedAbilityNode != null && focusedAbilityNode.getClass() == ability.getClass()) {
                     float radius = AreaOfEffectHelper.adjustAreaOfEffect(player, 4.0F);
                     float yRadius = AreaOfEffectHelper.adjustAreaOfEffect(player, 6.0F);
                     BlockHelper.withEllipsoidPositions(player.blockPosition().above(3), radius, yRadius, radius, offset -> {
                        BlockState state = world.getBlockState(offset);
                        if (ability.canBreakBlock(state)) {
                           float hardness = state.getDestroySpeed(world, offset);
                           if (hardness >= 0.0F && hardness <= 25.0F) {
                              ability.destroyBlock(world, offset, player);
                           }
                        }
                     });
                  }
               }
            }
         }
      }
   }

   private void destroyBlock(ServerLevel world, BlockPos pos, Player player) {
      ItemStack miningItem = new ItemStack(Items.DIAMOND_PICKAXE);
      Block.dropResources(world.getBlockState(pos), world, pos, world.getBlockEntity(pos), null, miningItem);
      world.destroyBlock(pos, false, player);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.ticks), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.ticks = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.ticks)).ifPresent(tag -> nbt.put("ticks", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.ticks = Adapters.INT.readNbt(nbt.get("ticks")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.ticks)).ifPresent(element -> json.add("ticks", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.ticks = Adapters.INT.readJson(json.get("ticks")).orElse(0);
   }
}

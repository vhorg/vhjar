package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.MegaJumpVelocityModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.BlockHelper;
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

public class MegaJumpBreakDownAbility extends MegaJumpAbility {
   private int radius;

   public MegaJumpBreakDownAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, int height, int radius) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, height);
      this.radius = radius;
   }

   public MegaJumpBreakDownAbility() {
   }

   public int getRadius() {
      return this.radius;
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> super.canDoAction(context) && ServerVaults.get(player.level).isPresent()).orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               int height = this.getHeight();

               for (ConfiguredModification<IntValueConfig, MegaJumpVelocityModification> mod : SpecialAbilityModification.getModifications(
                  player, MegaJumpVelocityModification.class
               )) {
                  height = mod.modification().adjustHeightConfig(mod.config(), height);
               }

               if (height == 0) {
                  this.breakBlocks(height, player);
                  return Ability.ActionResult.successCooldownImmediate();
               } else {
                  double magnitude = height * 0.15;
                  double addY = -Math.min(0.0, player.getDeltaMovement().y());
                  player.push(0.0, -(addY + magnitude), 0.0);
                  player.startFallFlying();
                  player.hurtMarked = true;
                  this.breakBlocks(height, player);
                  return Ability.ActionResult.successCooldownImmediate();
               }
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   private void breakBlocks(int height, ServerPlayer player) {
      ServerLevel sWorld = (ServerLevel)player.getCommandSenderWorld();
      AbilityTree abilityTree = PlayerAbilitiesData.get(sWorld).getAbilities(player);
      Ability focusedAbilityNode = abilityTree.getSelectedAbility().orElse(null);
      if (focusedAbilityNode != null && focusedAbilityNode.getClass() == this.getClass()) {
         int radius = Math.max(this.getRadius(), 1);
         int depth = Math.max(height, 1) + 1;
         BlockPos centerPos = player.blockPosition();
         if (depth > 2) {
            centerPos = centerPos.below(depth - 2);
         }

         BlockHelper.withEllipsoidPositions(centerPos, radius, depth, radius, offset -> {
            BlockState state = sWorld.getBlockState(offset);
            if (this.canBreakBlock(state)) {
               float hardness = state.getDestroySpeed(sWorld, offset);
               if (hardness >= 0.0F && hardness <= 25.0F) {
                  this.destroyBlock(sWorld, offset, player);
               }
            }
         });
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
      Adapters.INT.writeBits(Integer.valueOf(this.radius), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.radius = Adapters.INT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.radius)).ifPresent(tag -> nbt.put("radius", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.radius = Adapters.INT.readNbt(nbt.get("radius")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.radius)).ifPresent(element -> json.add("radius", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.radius = Adapters.INT.readJson(json.get("radius")).orElse(0);
   }
}

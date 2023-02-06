package iskallia.vault.mixin;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.HammerManager;
import iskallia.vault.item.tool.HammerTile;
import iskallia.vault.item.tool.IHammer;
import iskallia.vault.item.tool.ToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayerGameMode.class})
public abstract class MixinServerPlayerGameMode implements IHammer {
   @Shadow
   @Final
   protected ServerPlayer player;
   @Shadow
   protected ServerLevel level;
   @Shadow
   private GameType gameModeForPlayer;
   @Shadow
   private int gameTicks;
   @Shadow
   private BlockPos delayedDestroyPos;
   @Shadow
   private int destroyProgressStart;
   @Shadow
   private boolean hasDelayedDestroy;
   @Shadow
   private BlockPos destroyPos;
   @Shadow
   private int delayedTickStart;
   private HammerManager hammer = new HammerManager();

   @Shadow
   public abstract boolean isCreative();

   @Shadow
   public abstract void tick();

   @Shadow
   public abstract void destroyAndAck(BlockPos var1, Action var2, String var3);

   @Shadow
   public abstract boolean destroyBlock(BlockPos var1);

   @Override
   public HammerManager getHammer() {
      return this.hammer;
   }

   @Redirect(
      method = {"useItemOn"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;doesSneakBypassUse(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"
      )
   )
   public boolean doesSneakBypassUse(ItemStack instance, LevelReader levelReader, BlockPos pos, Player player) {
      BlockState state = levelReader.getBlockState(pos);
      return state.getBlock() instanceof VaultCrateBlock;
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   public void tick(CallbackInfo ci) {
      this.hammer.tiles.removeIf(tile -> !tile.isDestroyingBlock);
      if (this.hasDelayedDestroy) {
         BlockState blockstate = this.level.getBlockState(this.delayedDestroyPos);
         if (blockstate.isAir()) {
            return;
         }

         for (HammerTile tile : this.hammer.tiles) {
            BlockState state = this.level.getBlockState(tile.destroyPos);
            float f = this.doDestroyProgress(state, tile.destroyPos, this.delayedTickStart, tile);
            if (f >= 1.0F) {
               this.destroyBlock(tile.destroyPos);
            }
         }
      }

      for (HammerTile tilex : this.hammer.tiles) {
         if (tilex.isDestroyingBlock) {
            BlockState blockstate = this.level.getBlockState(this.destroyPos);
            if (blockstate.isAir()) {
               this.level.destroyBlockProgress(-1, tilex.destroyPos, -1);
               tilex.lastSentState = -1;
               tilex.isDestroyingBlock = false;
            } else {
               this.doDestroyProgress(this.level.getBlockState(tilex.destroyPos), tilex.destroyPos, tilex.destroyProgressStart, tilex);
            }
         }
      }
   }

   private float doDestroyProgress(BlockState state, BlockPos pos, int destroyProgressStart, HammerTile tile) {
      int i = this.gameTicks - destroyProgressStart;
      float f = state.getDestroyProgress(this.player, this.player.level, pos) * (i + 1);
      int j = Math.min(9, (int)(f * 10.0F));
      if (j != tile.lastSentState) {
         this.level.destroyBlockProgress(-1, pos, j);
         tile.lastSentState = j;
      }

      return f;
   }

   @Inject(
      method = {"handleBlockBreakAction"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayer;canInteractWith(Lnet/minecraft/core/BlockPos;D)Z",
         shift = Shift.BEFORE
      )}
   )
   public void handleBlockBreakAction(BlockPos pos, Action action, Direction facing, int buildHeight, CallbackInfo ci) {
      if (this.player.canInteractWith(pos, 1.0) && pos.getY() < buildHeight) {
         if (action == Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, pos)) {
               return;
            }

            if (this.isCreative()) {
               if (this.hasHammer(this.player)) {
                  this.computeHammerTiles(pos, facing, buildHeight);
               }

               this.hammer.tiles.removeIf(tilex -> {
                  this.destroyAndAck(tilex.destroyPos, action, "creative destroy");
                  return true;
               });
               return;
            }

            if (this.player.blockActionRestricted(this.level, pos, this.gameModeForPlayer)) {
               return;
            }

            if (this.hasHammer(this.player)) {
               this.computeHammerTiles(pos, facing, buildHeight);
            }

            for (HammerTile tile : this.hammer.tiles) {
               tile.destroyProgressStart = this.gameTicks;
               float f = 1.0F;
               BlockState blockstate = this.level.getBlockState(pos);
               if (!blockstate.isAir()) {
                  blockstate.attack(this.level, tile.destroyPos, this.player);
                  f = blockstate.getDestroyProgress(this.player, this.player.level, tile.destroyPos);
               }

               if (!blockstate.isAir() && f >= 1.0F) {
                  this.destroyAndAck(tile.destroyPos, action, "insta mine");
               } else {
                  if (tile.isDestroyingBlock) {
                     this.player
                        .connection
                        .send(
                           new ClientboundBlockBreakAckPacket(
                              tile.destroyPos,
                              this.level.getBlockState(tile.destroyPos),
                              Action.START_DESTROY_BLOCK,
                              false,
                              "abort destroying since another started (client insta mine, server disagreed)"
                           )
                        );
                  }

                  tile.isDestroyingBlock = true;
                  int i = (int)(f * 10.0F);
                  this.level.destroyBlockProgress(-1, tile.destroyPos, i);
                  this.player
                     .connection
                     .send(
                        new ClientboundBlockBreakAckPacket(
                           tile.destroyPos, this.level.getBlockState(tile.destroyPos), action, true, "actual start of destroying"
                        )
                     );
                  tile.lastSentState = i;
               }
            }
         } else if (action == Action.STOP_DESTROY_BLOCK) {
            if (pos.equals(this.destroyPos)) {
               int j = this.gameTicks - this.destroyProgressStart;
               BlockState state2 = this.level.getBlockState(pos);
               if (!state2.isAir()) {
                  float f1 = state2.getDestroyProgress(this.player, this.player.level, pos) * (j + 1);
                  if (f1 >= 0.7F) {
                     for (HammerTile tile : this.hammer.tiles) {
                        tile.isDestroyingBlock = false;
                        this.level.destroyBlockProgress(-1, tile.destroyPos, -1);
                        BlockState state3 = this.level.getBlockState(tile.destroyPos);
                        float f2 = state3.getDestroyProgress(this.player, this.player.level, tile.destroyPos)
                           * (this.gameTicks - this.destroyProgressStart + 1);
                        if (f2 >= 1.0F) {
                           this.destroyAndAck(tile.destroyPos, action, "destroyed");
                        }
                     }

                     return;
                  }

                  if (!this.hasDelayedDestroy) {
                     for (HammerTile tilex : this.hammer.tiles) {
                        tilex.isDestroyingBlock = false;
                     }
                  }
               }
            }

            for (HammerTile tilex : this.hammer.tiles) {
               this.player
                  .connection
                  .send(new ClientboundBlockBreakAckPacket(tilex.destroyPos, this.level.getBlockState(tilex.destroyPos), action, true, "stopped destroying"));
            }
         } else if (action == Action.ABORT_DESTROY_BLOCK) {
            this.hammer
               .tiles
               .removeIf(
                  tilex -> {
                     this.level.destroyBlockProgress(-1, tilex.destroyPos, -1);
                     this.player
                        .connection
                        .send(
                           new ClientboundBlockBreakAckPacket(tilex.destroyPos, this.level.getBlockState(tilex.destroyPos), action, true, "aborted destroying")
                        );
                     return true;
                  }
               );
         }
      }
   }

   private boolean hasHammer(ServerPlayer player) {
      ItemStack stack = player.getMainHandItem();
      if (stack.getItem() != ModItems.TOOL) {
         return false;
      } else {
         VaultGearData data = VaultGearData.read(stack);
         return data.get(ModGearAttributes.HAMMERING, VaultGearAttributeTypeMerger.anyTrue());
      }
   }

   private void computeHammerTiles(BlockPos pos, Direction facing, int buildHeight) {
      ToolItem.getHammerPositions(this.player.getMainHandItem(), pos, facing, this.player).forEachRemaining(p -> {
         if (!p.equals(pos)) {
            this.hammer.tiles.add(new HammerTile(true, this.gameTicks, p, -1));
         }
      });
   }
}

package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.config.VaultModifierPoolsConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.SingularVaultRoomLayout;
import iskallia.vault.world.vault.gen.layout.SpiralHelper;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.modifier.ChanceArtifactModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerNoExitModifier;
import iskallia.vault.world.vault.modifier.modifier.VaultTimeModifier;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

public class CakeHuntObjective extends VaultObjective {
   private int maxCakeCount = 22 + rand.nextInt(9);
   private int cakeCount = 0;
   private float modifierChance = 0.75F;
   private VaultModifierPoolsConfig.ModifierPoolType poolType = VaultModifierPoolsConfig.ModifierPoolType.DEFAULT;

   public CakeHuntObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   public void setModifierChance(float modifierChance) {
      this.modifierChance = modifierChance;
   }

   public void setPoolType(VaultModifierPoolsConfig.ModifierPoolType poolType) {
      this.poolType = poolType;
   }

   public void expandVault(ServerLevel world, BlockPos cakePos, VaultRaid vault) {
      vault.getGenerator()
         .getPiecesAt(cakePos, VaultRoom.class)
         .stream()
         .findAny()
         .ifPresent(
            room -> vault.getProperties()
               .getBase(VaultRaid.START_FACING)
               .ifPresent(
                  vaultDir -> {
                     this.cakeCount++;
                     if (this.cakeCount < this.maxCakeCount) {
                        this.addRandomModifier(vault, world);
                        Vec3i curr = SpiralHelper.getSpiralPosition(this.cakeCount - 1, vaultDir.getClockWise(), Rotation.COUNTERCLOCKWISE_90);
                        Vec3i next = SpiralHelper.getSpiralPosition(this.cakeCount, vaultDir.getClockWise(), Rotation.COUNTERCLOCKWISE_90);
                        Direction direction = Direction.getNearest(next.getX() - curr.getX(), 0.0F, next.getZ() - curr.getZ());
                        List<VaultPiece> generatedPieces = VaultJigsawHelper.expandVault(vault, world, room, direction);
                        generatedPieces.stream()
                           .filter(piece -> piece instanceof VaultRoom)
                           .findFirst()
                           .ifPresent(newRoomPiece -> this.ensureCakeIsPresent(world, (VaultRoom)newRoomPiece));
                        BreadcrumbFeature.generateVaultBreadcrumb(vault, world, generatedPieces);
                     }
                  }
               )
         );
   }

   private void addRandomModifier(VaultRaid vault, ServerLevel sWorld) {
      if (!(sWorld.getRandom().nextFloat() >= this.modifierChance)) {
         int level = vault.getProperties().getValue(VaultRaid.LEVEL);
         Set<VaultModifier<?>> modifiers = ModConfigs.VAULT_MODIFIER_POOLS.getRandom(rand, level, this.poolType, null);
         modifiers.removeIf(mod -> mod instanceof PlayerNoExitModifier);
         modifiers.removeIf(mod -> mod instanceof VaultTimeModifier);
         modifiers.removeIf(mod -> mod instanceof PlayerInventoryRestoreModifier);
         if (sWorld.getRandom().nextFloat() < 0.65F) {
            modifiers.removeIf(mod -> mod instanceof ChanceArtifactModifier);
         }

         List<VaultModifier<?>> modifierList = new ArrayList<>(modifiers);
         Collections.shuffle(modifierList);
         VaultModifier<?> modifier = MiscUtils.getRandomEntry(modifierList, rand);
         if (modifier != null) {
            Component ct = new TextComponent("Added ").withStyle(ChatFormatting.GRAY).append(modifier.getNameComponent());
            vault.getModifiers().addPermanentModifier(modifier, 1);
            vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(sWorld.getServer(), sPlayer -> sPlayer.sendMessage(ct, Util.NIL_UUID)));
         }
      }
   }

   private void spawnRewards(ServerLevel world, VaultRaid vault) {
      VaultPlayer rewardPlayer = vault.getProperties()
         .getBase(VaultRaid.HOST)
         .flatMap(vault::getPlayer)
         .filter(vPlayer -> vPlayer instanceof VaultRunner)
         .orElseGet(() -> vault.getPlayers().stream().filter(vPlayer -> vPlayer instanceof VaultRunner).findAny().orElse(null));
      if (rewardPlayer != null) {
         rewardPlayer.runIfPresent(
            world.getServer(),
            sPlayer -> {
               BlockPos pos = sPlayer.blockPosition();
               Builder builder = new Builder(world)
                  .withRandom(world.random)
                  .withParameter(LootContextParams.THIS_ENTITY, sPlayer)
                  .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                  .withLuck(sPlayer.getLuck());
               LootContext ctx = builder.create(LootContextParamSets.CHEST);
               this.dropRewardCrate(world, vault, pos, ctx);

               for (int i = 1; i < vault.getPlayers().size(); i++) {
                  if (rand.nextFloat() < 0.5F) {
                     this.dropRewardCrate(world, vault, pos, ctx);
                  }
               }

               MutableComponent msgContainer = new TextComponent("").withStyle(ChatFormatting.WHITE);
               MutableComponent playerName = sPlayer.getDisplayName().copy();
               playerName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168)));
               MiscUtils.broadcast(msgContainer.append(playerName).append(" finished a Cake Hunt!"));
            }
         );
      }
   }

   private void dropRewardCrate(ServerLevel world, VaultRaid vault, BlockPos pos, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.CAKE, stacks);
      ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), crate);
      item.setDefaultPickUpDelay();
      world.addFreshEntity(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   @Override
   protected void addSpecialLoot(ServerLevel world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      super.addSpecialLoot(world, vault, context, stacks);
      int amt = Math.max(rand.nextInt(this.maxCakeCount), rand.nextInt(this.maxCakeCount));

      for (int i = 0; i < amt; i++) {
         stacks.add(new ItemStack(Items.CAKE));
      }
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.getServer();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.cakeHunt(this.maxCakeCount, this.cakeCount);
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }));
      vault.getGenerator().getPieces(VaultRoom.class).forEach(room -> this.ensureCakeIsPresent(world, room));
      if (this.cakeCount >= this.maxCakeCount) {
         this.setCompleted();
         this.spawnRewards(world, vault);
         DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(world);
         vault.getPlayers()
            .forEach(
               vPlayer -> vPlayer.runIfPresent(
                  srv, serverPlayer -> discoveredModelsData.discoverRandomArmorPieceAndBroadcast(serverPlayer, ModDynamicModels.Armor.CAKE, world.getRandom())
               )
            );
      } else if (world.getGameTime() % 300L == 0L) {
         vault.getPlayers()
            .stream()
            .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
            .forEach(
               vPlayer -> vPlayer.runIfPresent(
                  srv, playerEntity -> vault.getGenerator().getPiecesAt(playerEntity.blockPosition(), VaultRoom.class).stream().findFirst().ifPresent(room -> {
                     if (!room.isCakeEaten()) {
                        BlockPos cakePos = room.getCakePos();
                        if (cakePos != null) {
                           int bDst = (int)Mth.sqrt((float)playerEntity.blockPosition().distSqr(cakePos));
                           Component dist = new TextComponent("Distance to cake: " + bDst + "m").withStyle(ChatFormatting.GRAY);
                           playerEntity.displayClientMessage(dist, true);
                        }
                     }
                  })
               )
            );
      }
   }

   private void ensureCakeIsPresent(ServerLevel world, VaultRoom room) {
      if (!room.isCakeEaten()) {
         BoundingBox roomBox = room.getBoundingBox();
         if (room.getCakePos() == null) {
            for (int xx = roomBox.minX(); xx <= roomBox.maxX(); xx++) {
               for (int yy = roomBox.minY(); yy <= roomBox.maxY(); yy++) {
                  for (int zz = roomBox.minZ(); zz <= roomBox.maxZ(); zz++) {
                     BlockPos pos = new BlockPos(xx, yy, zz);
                     BlockState state = world.getBlockState(pos);
                     if (state.getBlock() instanceof CakeBlock) {
                        world.removeBlock(pos, false);
                     }
                  }
               }
            }

            BlockPos at;
            do {
               at = MiscUtils.getRandomPos(roomBox, rand);
            } while (!world.isEmptyBlock(at) || !world.getBlockState(at.below()).isFaceSturdy(world, at, Direction.UP) || !world.isEmptyBlock(at.above()));

            world.setBlock(at, Blocks.CAKE.defaultBlockState(), 2);
            room.setCakePos(at);
         } else {
            for (int xx = roomBox.minX(); xx <= roomBox.maxX(); xx++) {
               for (int yy = roomBox.minY(); yy <= roomBox.maxY(); yy++) {
                  for (int zzx = roomBox.minZ(); zzx <= roomBox.maxZ(); zzx++) {
                     BlockPos pos = new BlockPos(xx, yy, zzx);
                     BlockState state = world.getBlockState(pos);
                     if (state.getBlock() instanceof CakeBlock) {
                        return;
                     }
                  }
               }
            }

            BlockPos at;
            do {
               at = MiscUtils.getRandomPos(roomBox, rand);
            } while (!world.isEmptyBlock(at) || !world.getBlockState(at.below()).isFaceSturdy(world, at, Direction.UP) || !world.isEmptyBlock(at.above()));

            world.setBlock(at, Blocks.CAKE.defaultBlockState(), 2);
            room.setCakePos(at);
         }
      }
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return null;
   }

   @Override
   public Component getObjectiveDisplayName() {
      return new TextComponent("Cake Hunt").withStyle(ChatFormatting.DARK_PURPLE);
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.maxCakeCount = amount;
   }

   @Nullable
   @Override
   public Component getObjectiveTargetDescription(int amount) {
      return new TextComponent("Cakes needed to be found: ").append(new TextComponent(String.valueOf(amount)).withStyle(ChatFormatting.DARK_PURPLE));
   }

   @Nullable
   @Override
   public VaultRoomLayoutGenerator getCustomLayout() {
      return new SingularVaultRoomLayout();
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putInt("maxCakeCount", this.maxCakeCount);
      tag.putInt("cakeCount", this.cakeCount);
      tag.putFloat("modifierChance", this.modifierChance);
      tag.putInt("poolType", this.poolType.ordinal());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.maxCakeCount = nbt.getInt("maxCakeCount");
      this.cakeCount = nbt.getInt("cakeCount");
      this.modifierChance = nbt.getFloat("modifierChance");
      if (nbt.contains("poolType", 3)) {
         this.poolType = VaultModifierPoolsConfig.ModifierPoolType.values()[nbt.getInt("poolType")];
      }
   }
}

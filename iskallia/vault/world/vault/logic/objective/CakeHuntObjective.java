package iskallia.vault.world.vault.logic.objective;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.SingularVaultRoomLayout;
import iskallia.vault.world.vault.gen.layout.SpiralHelper;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.modifier.ArtifactChanceModifier;
import iskallia.vault.world.vault.modifier.InventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.NoExitModifier;
import iskallia.vault.world.vault.modifier.TimerModifier;
import iskallia.vault.world.vault.modifier.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;

public class CakeHuntObjective extends VaultObjective {
   public static UUID PENALTY = UUID.fromString("5AD3F258-FEE1-4E67-B885-69FD380BB150");
   private int maxCakeCount = 22 + rand.nextInt(9);
   private int cakeCount = 0;
   private float modifierChance = 0.75F;
   private VaultModifiersConfig.ModifierPoolType poolType = VaultModifiersConfig.ModifierPoolType.DEFAULT;
   private float healthPenalty;
   private ResourceLocation roomPool = Vault.id("vault/rooms");
   private ResourceLocation tunnelPool = Vault.id("vault/tunnels");

   public CakeHuntObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   public void setModifierChance(float modifierChance) {
      this.modifierChance = modifierChance;
   }

   public void setPoolType(VaultModifiersConfig.ModifierPoolType poolType) {
      this.poolType = poolType;
   }

   public void setHealthPenalty(float healthPenalty) {
      this.healthPenalty = healthPenalty;
   }

   public void setRoomPool(ResourceLocation roomPool) {
      this.roomPool = roomPool;
   }

   public void setTunnelPool(ResourceLocation tunnelPool) {
      this.tunnelPool = tunnelPool;
   }

   public void expandVault(ServerWorld world, ServerPlayerEntity player, BlockPos cakePos, VaultRaid vault) {
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
                        this.addRandomModifier(vault, world, player);
                        Vector3i curr = SpiralHelper.getSpiralPosition(this.cakeCount - 1, vaultDir.func_176746_e(), vaultDir);
                        Vector3i next = SpiralHelper.getSpiralPosition(this.cakeCount, vaultDir.func_176746_e(), vaultDir);
                        Direction direction = Direction.func_176737_a(
                           next.func_177958_n() - curr.func_177958_n(), 0.0F, next.func_177952_p() - curr.func_177952_p()
                        );
                        List<VaultPiece> generatedPieces = VaultJigsawHelper.expandVault(
                           vault, world, room, direction, VaultJigsawHelper.getRandomPiece(this.roomPool), VaultJigsawHelper.getRandomPiece(this.tunnelPool)
                        );
                        generatedPieces.stream()
                           .filter(piece -> piece instanceof VaultRoom)
                           .findFirst()
                           .ifPresent(newRoomPiece -> this.ensureCakeIsPresent(world, (VaultRoom)newRoomPiece));
                        if (!vault.getProperties().exists(VaultRaid.PARENT)) {
                           BreadcrumbFeature.generateVaultBreadcrumb(vault, world, generatedPieces);
                        }
                     }
                  }
               )
         );
   }

   private void addRandomModifier(VaultRaid vault, ServerWorld sWorld, ServerPlayerEntity player) {
      if (this.healthPenalty != 0.0F) {
         vault.getPlayers().stream().map(p -> p.getServerPlayer(sWorld.func_73046_m())).filter(Optional::isPresent).map(Optional::get).forEach(other -> {
            ModifiableAttributeInstance attribute = other.func_110148_a(Attributes.field_233818_a_);
            if (attribute != null) {
               attribute.func_188479_b(PENALTY);
               double amount = this.cakeCount * this.healthPenalty;
               amount = Math.min(amount, attribute.func_111126_e() - 2.0);
               AttributeModifier modifierx = new AttributeModifier(PENALTY, "Cake Health Penalty", -amount, Operation.ADDITION);
               attribute.func_233767_b_(modifierx);
            }
         });
      }

      if (!(sWorld.func_201674_k().nextFloat() >= this.modifierChance)) {
         int level = vault.getProperties().getValue(VaultRaid.LEVEL);
         Set<VaultModifier> modifiers = ModConfigs.VAULT_MODIFIERS.getRandom(rand, level, this.poolType, null);
         modifiers.removeIf(mod -> mod instanceof NoExitModifier);
         modifiers.removeIf(mod -> mod instanceof TimerModifier);
         modifiers.removeIf(mod -> mod instanceof InventoryRestoreModifier);
         if (sWorld.func_201674_k().nextFloat() < 0.65F) {
            modifiers.removeIf(mod -> mod instanceof ArtifactChanceModifier);
         }

         List<VaultModifier> modifierList = new ArrayList<>(modifiers);
         Collections.shuffle(modifierList);
         VaultModifier modifier = MiscUtils.getRandomEntry(modifierList, rand);
         if (modifier != null) {
            ITextComponent c0 = player.func_145748_c_().func_230532_e_().func_240699_a_(TextFormatting.LIGHT_PURPLE);
            ITextComponent c1 = new StringTextComponent(" found a ").func_240699_a_(TextFormatting.GRAY);
            ITextComponent c2 = new StringTextComponent("cake").func_240699_a_(TextFormatting.GREEN);
            ITextComponent c3 = new StringTextComponent(" and added ").func_240699_a_(TextFormatting.GRAY);
            ITextComponent c4 = modifier.getNameComponent();
            ITextComponent c5 = new StringTextComponent(".").func_240699_a_(TextFormatting.GRAY);
            ITextComponent ct = new StringTextComponent("")
               .func_230529_a_(c0)
               .func_230529_a_(c1)
               .func_230529_a_(c2)
               .func_230529_a_(c3)
               .func_230529_a_(c4)
               .func_230529_a_(c5);
            vault.getModifiers().addPermanentModifier(modifier);
            vault.getPlayers().forEach(vPlayer -> {
               modifier.apply(vault, vPlayer, sWorld, sWorld.func_201674_k());
               vPlayer.runIfPresent(sWorld.func_73046_m(), sPlayer -> sPlayer.func_145747_a(ct, Util.field_240973_b_));
            });
         }
      }
   }

   private void spawnRewards(ServerWorld world, VaultRaid vault) {
      VaultPlayer rewardPlayer = vault.getProperties()
         .getBase(VaultRaid.HOST)
         .flatMap(vault::getPlayer)
         .filter(vPlayer -> vPlayer instanceof VaultRunner)
         .orElseGet(() -> vault.getPlayers().stream().filter(vPlayer -> vPlayer instanceof VaultRunner).findAny().orElse(null));
      if (rewardPlayer != null) {
         rewardPlayer.runIfPresent(
            world.func_73046_m(),
            sPlayer -> {
               BlockPos pos = sPlayer.func_233580_cy_();
               Builder builder = new Builder(world)
                  .func_216023_a(world.field_73012_v)
                  .func_216015_a(LootParameters.field_216281_a, sPlayer)
                  .func_216015_a(LootParameters.field_237457_g_, Vector3d.func_237489_a_(pos))
                  .func_186469_a(sPlayer.func_184817_da());
               LootContext ctx = builder.func_216022_a(LootParameterSets.field_216261_b);
               this.dropRewardCrate(world, vault, pos, ctx);

               for (int i = 1; i < vault.getPlayers().size(); i++) {
                  if (rand.nextFloat() < 0.5F) {
                     this.dropRewardCrate(world, vault, pos, ctx);
                  }
               }

               IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
               IFormattableTextComponent playerName = sPlayer.func_145748_c_().func_230532_e_();
               playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
               MiscUtils.broadcast(msgContainer.func_230529_a_(playerName).func_240702_b_(" finished a Cake Hunt!"));
            }
         );
      }
   }

   private void dropRewardCrate(ServerWorld world, VaultRaid vault, BlockPos pos, LootContext context) {
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, context);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE_CAKE, stacks);
      ItemEntity item = new ItemEntity(world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), crate);
      item.func_174869_p();
      world.func_217376_c(item);
      this.crates.add(new VaultObjective.Crate(stacks));
   }

   @Override
   protected void addSpecialLoot(ServerWorld world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      super.addSpecialLoot(world, vault, context, stacks);
      int amt = Math.max(rand.nextInt(this.maxCakeCount), rand.nextInt(this.maxCakeCount));

      for (int i = 0; i < amt; i++) {
         stacks.add(new ItemStack(Items.field_222070_lD));
      }

      if (world.func_201674_k().nextFloat() < 0.25F) {
         stacks.add(new ItemStack(ModItems.ARMOR_CRATE_CAKE));
      }
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.func_73046_m();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.cakeHunt(this.maxCakeCount, this.cakeCount);
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
      }));
      vault.getGenerator().getPieces(VaultRoom.class).forEach(room -> this.ensureCakeIsPresent(world, room));
      if (this.cakeCount >= this.maxCakeCount) {
         this.setCompleted();
         this.spawnRewards(world, vault);
      } else if (world.func_82737_E() % 300L == 0L) {
         vault.getPlayers()
            .stream()
            .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
            .forEach(
               vPlayer -> vPlayer.runIfPresent(
                  srv,
                  playerEntity -> vault.getGenerator().getPiecesAt(playerEntity.func_233580_cy_(), VaultRoom.class).stream().findFirst().ifPresent(room -> {
                     if (!room.isCakeEaten()) {
                        BlockPos cakePos = room.getCakePos();
                        if (cakePos != null) {
                           int bDst = (int)MathHelper.func_76133_a(playerEntity.func_233580_cy_().func_177951_i(cakePos));
                           ITextComponent dist = new StringTextComponent("Distance to cake: " + bDst + "m").func_240699_a_(TextFormatting.GRAY);
                           playerEntity.func_146105_b(dist, true);
                        }
                     }
                  })
               )
            );
      }
   }

   private void ensureCakeIsPresent(ServerWorld world, VaultRoom room) {
      if (!room.isCakeEaten()) {
         MutableBoundingBox roomBox = room.getBoundingBox();
         if (room.getCakePos() == null) {
            for (int xx = roomBox.field_78897_a; xx <= roomBox.field_78893_d; xx++) {
               for (int yy = roomBox.field_78895_b; yy <= roomBox.field_78894_e; yy++) {
                  for (int zz = roomBox.field_78896_c; zz <= roomBox.field_78892_f; zz++) {
                     BlockPos pos = new BlockPos(xx, yy, zz);
                     BlockState state = world.func_180495_p(pos);
                     if (state.func_177230_c() instanceof CakeBlock) {
                        world.func_217377_a(pos, false);
                     }
                  }
               }
            }

            BlockPos at;
            do {
               at = MiscUtils.getRandomPos(roomBox, rand);
            } while (
               !world.func_175623_d(at)
                  || !world.func_180495_p(at.func_177977_b()).func_224755_d(world, at, Direction.UP)
                  || !world.func_175623_d(at.func_177984_a())
            );

            world.func_180501_a(at, Blocks.field_150414_aQ.func_176223_P(), 2);
            room.setCakePos(at);
         } else {
            for (int xx = roomBox.field_78897_a; xx <= roomBox.field_78893_d; xx++) {
               for (int yy = roomBox.field_78895_b; yy <= roomBox.field_78894_e; yy++) {
                  for (int zzx = roomBox.field_78896_c; zzx <= roomBox.field_78892_f; zzx++) {
                     BlockPos pos = new BlockPos(xx, yy, zzx);
                     BlockState state = world.func_180495_p(pos);
                     if (state.func_177230_c() instanceof CakeBlock) {
                        return;
                     }
                  }
               }
            }

            BlockPos at;
            do {
               at = MiscUtils.getRandomPos(roomBox, rand);
            } while (
               !world.func_175623_d(at)
                  || !world.func_180495_p(at.func_177977_b()).func_224755_d(world, at, Direction.UP)
                  || !world.func_175623_d(at.func_177984_a())
            );

            world.func_180501_a(at, Blocks.field_150414_aQ.func_176223_P(), 2);
            room.setCakePos(at);
         }
      }
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerWorld world, BlockPos pos) {
      return Blocks.field_150350_a.func_176223_P();
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return config != null ? tblResolver.apply(config.getBossCrate()) : LootTable.field_186464_a;
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Cake Hunt").func_240699_a_(TextFormatting.DARK_PURPLE);
   }

   @Override
   public void setObjectiveTargetCount(int amount) {
      this.maxCakeCount = amount;
   }

   @Nullable
   @Override
   public ITextComponent getObjectiveTargetDescription(int amount) {
      return new StringTextComponent("Cakes needed to be found: ")
         .func_230529_a_(new StringTextComponent(String.valueOf(amount)).func_240699_a_(TextFormatting.DARK_PURPLE));
   }

   @Nullable
   @Override
   public VaultRoomLayoutGenerator getCustomLayout() {
      SingularVaultRoomLayout layout = new SingularVaultRoomLayout();
      layout.setRoomId(this.roomPool);
      layout.setTunnelId(this.tunnelPool);
      return layout;
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74768_a("maxCakeCount", this.maxCakeCount);
      tag.func_74768_a("cakeCount", this.cakeCount);
      tag.func_74776_a("modifierChance", this.modifierChance);
      tag.func_74768_a("poolType", this.poolType.ordinal());
      tag.func_74776_a("healthPenalty", this.healthPenalty);
      tag.func_74778_a("roomPool", this.roomPool.toString());
      tag.func_74778_a("tunnelPool", this.tunnelPool.toString());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.maxCakeCount = nbt.func_74762_e("maxCakeCount");
      this.cakeCount = nbt.func_74762_e("cakeCount");
      this.modifierChance = nbt.func_74760_g("modifierChance");
      if (nbt.func_150297_b("poolType", 3)) {
         this.poolType = VaultModifiersConfig.ModifierPoolType.values()[nbt.func_74762_e("poolType")];
      }

      this.healthPenalty = nbt.func_74760_g("healthPenalty");
      if (nbt.func_150297_b("roomPool", 8)) {
         this.roomPool = new ResourceLocation(nbt.func_74779_i("roomPool"));
      }

      if (nbt.func_150297_b("tunnelPool", 8)) {
         this.tunnelPool = new ResourceLocation(nbt.func_74779_i("tunnelPool"));
      }
   }
}

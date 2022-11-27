package iskallia.vault.world.vault.logic.objective.ancient;

import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.ScheduledItemDropData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.layout.DenseSquareRoomLayout;
import iskallia.vault.world.vault.gen.layout.VaultRoomLayoutGenerator;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.task.VaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

public class AncientObjective extends VaultObjective {
   public static final int MAX_ANCIENTS = 4;
   private int generatedIdentifier = 0;
   private final List<AncientEternalReference> identifiers = new ArrayList<>();
   private final List<String> foundEternals = new ArrayList<>();
   private final Set<BlockPos> placedEternals = new HashSet<>();

   public AncientObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   @Override
   public void initialize(MinecraftServer srv, VaultRaid vault) {
      vault.getProperties().getBase(VaultRaid.HOST).ifPresent(id -> {
         List<AncientEternalReference> ancients = AncientEternalArchive.getAncients(srv, id);

         for (int i = 0; i < Math.min(ancients.size(), 4); i++) {
            this.identifiers.add(ancients.get(i));
         }
      });
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerLevel world, BlockPos pos) {
      return (BlockState)ModBlocks.CRYO_CHAMBER.defaultBlockState().setValue(CryoChamberBlock.CHAMBER_STATE, CryoChamberBlock.ChamberState.RUSTY);
   }

   @Override
   public void postProcessObjectiveRelevantBlock(ServerLevel world, BlockPos pos) {
      if (this.generatedIdentifier < this.identifiers.size()) {
         BlockEntity te = world.getBlockEntity(pos);
         if (te instanceof AncientCryoChamberTileEntity) {
            world.setBlock(
               pos.above(),
               (BlockState)((BlockState)ModBlocks.CRYO_CHAMBER.defaultBlockState().setValue(CryoChamberBlock.HALF, DoubleBlockHalf.UPPER))
                  .setValue(CryoChamberBlock.CHAMBER_STATE, CryoChamberBlock.ChamberState.RUSTY),
               3
            );
            AncientEternalReference identifier = this.identifiers.get(this.generatedIdentifier);
            AncientCryoChamberTileEntity cryoChamber = (AncientCryoChamberTileEntity)te;
            cryoChamber.setEternalName(identifier.getName());
            this.placedEternals.add(pos);
            this.generatedIdentifier++;
         }
      }
   }

   @Override
   public int modifyObjectiveCount(int objectives) {
      return this.identifiers.size();
   }

   @Override
   public void notifyBail(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      if (!this.foundEternals.isEmpty()) {
         this.setCompleted();
         player.runIfPresent(world.getServer(), sPlayer -> {
            ScheduledItemDropData.get(world).addDrop(sPlayer, this.getRewardCrate(sPlayer, vault));
            MutableComponent ct = new TextComponent("");
            ct.append(sPlayer.getDisplayName().copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(9974168))));
            ct.append(" rescued ");

            for (int i = 0; i < this.foundEternals.size(); i++) {
               if (i != 0) {
                  ct.append(", ");
               }

               ct.append(new TextComponent(this.foundEternals.get(i)).withStyle(ChatFormatting.GOLD));
            }

            ct.append("!");
            MiscUtils.broadcast(ct);
         });
      }
   }

   private ItemStack getRewardCrate(ServerPlayer sPlayer, VaultRaid vault) {
      ServerLevel world = sPlayer.getLevel();
      BlockPos pos = sPlayer.blockPosition();
      Builder builder = new Builder(world)
         .withRandom(world.random)
         .withParameter(LootContextParams.THIS_ENTITY, sPlayer)
         .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
         .withLuck(sPlayer.getLuck());
      LootContext ctx = builder.create(LootContextParamSets.CHEST);
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, ctx);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.BOSS, stacks);
      this.crates.add(new VaultObjective.Crate(stacks));
      return crate;
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return null;
   }

   @Override
   protected void addSpecialLoot(ServerLevel world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      LootTable ancientTable = world.getServer().getLootTables().get(config.getAncientEternalBonusBox());

      for (String eternalName : this.foundEternals) {
         NonNullList<ItemStack> ancientLoot = NonNullList.create();
         ancientLoot.addAll(ancientTable.getRandomItems(context));
         Collections.shuffle(ancientLoot);
         ItemStack box = new ItemStack(Items.BLACK_SHULKER_BOX);
         ContainerHelper.saveAllItems(box.getOrCreateTagElement("BlockEntityTag"), ancientLoot);
         box.setHoverName(new TextComponent(eternalName).withStyle(ChatFormatting.GOLD));
         stacks.add(box);
      }
   }

   @Override
   public Component getVaultName() {
      return new TextComponent("Ancient Vault").withStyle(ChatFormatting.DARK_AQUA);
   }

   @Override
   public Component getObjectiveDisplayName() {
      return new TextComponent("Ancient Vault").withStyle(ChatFormatting.DARK_AQUA);
   }

   @Nullable
   @Override
   public VaultRoomLayoutGenerator getCustomLayout() {
      return new DenseSquareRoomLayout(19);
   }

   @Override
   public int getVaultTimerStart(int vaultTime) {
      return 18000;
   }

   @Override
   public boolean preventsEatingExtensionFruit(MinecraftServer srv, VaultRaid vault) {
      return true;
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerLevel world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.getServer();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.ancientsHunt(this.identifiers.size(), this.foundEternals.size());
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }));
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      ListTag ancients = new ListTag();
      this.identifiers.forEach(ancient -> ancients.add(ancient.serialize()));
      tag.put("ancients", ancients);
      ListTag placed = new ListTag();
      this.placedEternals.forEach(pos -> {
         CompoundTag posTag = new CompoundTag();
         posTag.putInt("x", pos.getX());
         posTag.putInt("y", pos.getY());
         posTag.putInt("z", pos.getZ());
         placed.add(posTag);
      });
      tag.put("placed", placed);
      ListTag found = new ListTag();
      this.foundEternals.forEach(name -> found.add(StringTag.valueOf(name)));
      tag.put("found", found);
      tag.putInt("generatedIdentifier", this.generatedIdentifier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.identifiers.clear();
      ListTag ancients = tag.getList("ancients", 10);

      for (int i = 0; i < ancients.size(); i++) {
         CompoundTag ancientTag = ancients.getCompound(i);
         this.identifiers.add(AncientEternalReference.deserialize(ancientTag));
      }

      this.placedEternals.clear();
      ListTag placed = tag.getList("placed", 10);

      for (int i = 0; i < placed.size(); i++) {
         CompoundTag posTag = placed.getCompound(i);
         this.placedEternals.add(new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z")));
      }

      this.foundEternals.clear();
      ListTag found = tag.getList("found", 8);

      for (int i = 0; i < found.size(); i++) {
         this.foundEternals.add(found.getString(i));
      }

      this.generatedIdentifier = tag.getInt("generatedIdentifier");
   }
}

package iskallia.vault.world.vault.logic.objective.ancient;

import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.ScheduledItemDropData;
import iskallia.vault.world.data.VaultRaidData;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class AncientObjective extends VaultObjective {
   public static final int MAX_ANCIENTS = 4;
   private int generatedIdentifier = 0;
   private final List<String> generatedEternals = new ArrayList<>();
   private final List<String> foundEternals = new ArrayList<>();
   private final Set<BlockPos> placedEternals = new HashSet<>();

   public AncientObjective(ResourceLocation id) {
      super(id, VaultTask.EMPTY, VaultTask.EMPTY);
   }

   @Override
   public void initialize(MinecraftServer srv, VaultRaid vault) {
      vault.getProperties().getBase(VaultRaid.HOST).ifPresent(id -> {
         List<String> ancients = AncientEternalArchive.getAncients(srv, id);

         for (int i = 0; i < Math.min(ancients.size(), 4); i++) {
            this.generatedEternals.add(ancients.get(i));
         }
      });
   }

   @Nonnull
   @Override
   public BlockState getObjectiveRelevantBlock(VaultRaid vault, ServerWorld world, BlockPos pos) {
      return (BlockState)ModBlocks.CRYO_CHAMBER.func_176223_P().func_206870_a(CryoChamberBlock.CHAMBER_STATE, CryoChamberBlock.ChamberState.RUSTY);
   }

   @Override
   public void postProcessObjectiveRelevantBlock(ServerWorld world, BlockPos pos) {
      if (this.generatedIdentifier < this.generatedEternals.size()) {
         TileEntity te = world.func_175625_s(pos);
         if (te instanceof AncientCryoChamberTileEntity) {
            world.func_180501_a(
               pos.func_177984_a(),
               (BlockState)((BlockState)ModBlocks.CRYO_CHAMBER.func_176223_P().func_206870_a(CryoChamberBlock.HALF, DoubleBlockHalf.UPPER))
                  .func_206870_a(CryoChamberBlock.CHAMBER_STATE, CryoChamberBlock.ChamberState.RUSTY),
               3
            );
            String name = this.generatedEternals.get(this.generatedIdentifier);
            AncientCryoChamberTileEntity cryoChamber = (AncientCryoChamberTileEntity)te;
            cryoChamber.setEternalName(name);
            this.placedEternals.add(pos);
            this.generatedIdentifier++;
         }
      }
   }

   @Override
   public int modifyObjectiveCount(int objectives) {
      return this.generatedEternals.size();
   }

   @Override
   public void notifyBail(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      if (!this.foundEternals.isEmpty()) {
         this.setCompleted();
         player.runIfPresent(world.func_73046_m(), sPlayer -> {
            ScheduledItemDropData.get(world).addDrop(sPlayer, this.getRewardCrate(sPlayer, vault));
            IFormattableTextComponent ct = new StringTextComponent("");
            ct.func_230529_a_(sPlayer.func_145748_c_().func_230532_e_().func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168))));
            ct.func_240702_b_(" rescued ");

            for (int i = 0; i < this.foundEternals.size(); i++) {
               if (i != 0) {
                  ct.func_240702_b_(", ");
               }

               ct.func_230529_a_(new StringTextComponent(this.foundEternals.get(i)).func_240699_a_(TextFormatting.GOLD));
            }

            ct.func_240702_b_("!");
            MiscUtils.broadcast(ct);
         });
      }
   }

   private ItemStack getRewardCrate(ServerPlayerEntity sPlayer, VaultRaid vault) {
      ServerWorld world = sPlayer.func_71121_q();
      BlockPos pos = sPlayer.func_233580_cy_();
      Builder builder = new Builder(world)
         .func_216023_a(world.field_73012_v)
         .func_216015_a(LootParameters.field_216281_a, sPlayer)
         .func_216015_a(LootParameters.field_237457_g_, Vector3d.func_237489_a_(pos))
         .func_186469_a(sPlayer.func_184817_da());
      LootContext ctx = builder.func_216022_a(LootParameterSets.field_216261_b);
      NonNullList<ItemStack> stacks = this.createLoot(world, vault, ctx);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);
      this.crates.add(new VaultObjective.Crate(stacks));
      return crate;
   }

   @Nullable
   @Override
   public LootTable getRewardLootTable(VaultRaid vault, Function<ResourceLocation, LootTable> tblResolver) {
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      return config != null ? tblResolver.apply(config.getBossCrate()) : LootTable.field_186464_a;
   }

   @Override
   protected void addSpecialLoot(ServerWorld world, VaultRaid vault, LootContext context, NonNullList<ItemStack> stacks) {
      super.addSpecialLoot(world, vault, context, stacks);
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      LootTable ancientTable = world.func_73046_m().func_200249_aQ().func_186521_a(config.getAncientEternalBonusBox());

      for (String eternalName : this.foundEternals) {
         NonNullList<ItemStack> ancientLoot = NonNullList.func_191196_a();
         ancientLoot.addAll(ancientTable.func_216113_a(context));
         Collections.shuffle(ancientLoot);
         ItemStack box = new ItemStack(Items.field_221897_gG);
         ItemStackHelper.func_191282_a(box.func_190925_c("BlockEntityTag"), ancientLoot);
         box.func_200302_a(new StringTextComponent(eternalName).func_240699_a_(TextFormatting.GOLD));
         stacks.add(box);
      }
   }

   @Override
   public ITextComponent getVaultName() {
      return new StringTextComponent("Ancient Vault").func_240699_a_(TextFormatting.DARK_AQUA);
   }

   @Override
   public ITextComponent getObjectiveDisplayName() {
      return new StringTextComponent("Ancient Vault").func_240699_a_(TextFormatting.DARK_AQUA);
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

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onBreak(BreakEvent event) {
      if (!event.getWorld().func_201670_d() && event.getState().func_203425_a(ModBlocks.CRYO_CHAMBER)) {
         if (event.getWorld() instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld)event.getWorld();
            TileEntity te = CryoChamberBlock.getCryoChamberTileEntity(sWorld, event.getPos(), event.getState());
            if (te instanceof AncientCryoChamberTileEntity) {
               AncientCryoChamberTileEntity ancientCryoChamber = (AncientCryoChamberTileEntity)te;
               VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, event.getPos());
               if (vault != null) {
                  vault.getActiveObjective(AncientObjective.class).ifPresent(objective -> {
                     if (objective.placedEternals.contains(te.func_174877_v())) {
                        objective.foundEternals.add(ancientCryoChamber.getEternalName());
                     }
                  });
               }
            }
         }
      }
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      super.tick(vault, filter, world);
      MinecraftServer srv = world.func_73046_m();
      vault.getPlayers().stream().filter(vPlayer -> filter.test(vPlayer.getPlayerId())).forEach(vPlayer -> vPlayer.runIfPresent(srv, playerEntity -> {
         VaultGoalMessage pkt = VaultGoalMessage.ancientsHunt(this.generatedEternals.size(), this.foundEternals.size());
         ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
      }));
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      ListNBT ancients = new ListNBT();
      this.generatedEternals.forEach(ancient -> ancients.add(StringNBT.func_229705_a_(ancient)));
      tag.func_218657_a("ancients", ancients);
      ListNBT placed = new ListNBT();
      this.placedEternals.forEach(pos -> {
         CompoundNBT posTag = new CompoundNBT();
         posTag.func_74768_a("x", pos.func_177958_n());
         posTag.func_74768_a("y", pos.func_177956_o());
         posTag.func_74768_a("z", pos.func_177952_p());
         placed.add(posTag);
      });
      tag.func_218657_a("placed", placed);
      ListNBT found = new ListNBT();
      this.foundEternals.forEach(name -> found.add(StringNBT.func_229705_a_(name)));
      tag.func_218657_a("found", found);
      tag.func_74768_a("generatedIdentifier", this.generatedIdentifier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.generatedEternals.clear();
      ListNBT ancients = tag.func_150295_c("ancients", 10);

      for (int i = 0; i < ancients.size(); i++) {
         CompoundNBT ancientTag = ancients.func_150305_b(i);
         this.generatedEternals.add(ancientTag.func_74779_i("name"));
      }

      ancients = tag.func_150295_c("ancients", 8);

      for (int i = 0; i < ancients.size(); i++) {
         this.generatedEternals.add(ancients.func_150307_f(i));
      }

      this.placedEternals.clear();
      ListNBT placed = tag.func_150295_c("placed", 10);

      for (int i = 0; i < placed.size(); i++) {
         CompoundNBT posTag = placed.func_150305_b(i);
         this.placedEternals.add(new BlockPos(posTag.func_74762_e("x"), posTag.func_74762_e("y"), posTag.func_74762_e("z")));
      }

      this.foundEternals.clear();
      ListNBT found = tag.func_150295_c("found", 8);

      for (int i = 0; i < found.size(); i++) {
         this.foundEternals.add(found.func_150307_f(i));
      }

      this.generatedIdentifier = tag.func_74762_e("generatedIdentifier");
   }
}

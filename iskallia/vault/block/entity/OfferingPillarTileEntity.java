package iskallia.vault.block.entity;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.offering.OfferingBossFight;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.WorldZone;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.item.crystal.data.serializable.INbtSerializable;
import iskallia.vault.network.message.ScavengerAltarConsumeMessage;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldZonesData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class OfferingPillarTileEntity extends BlockEntity {
   protected static final BlockPos[] PARTICLE_POSITIONS = new BlockPos[]{
      BlockPos.ZERO.east(),
      BlockPos.ZERO.west(),
      BlockPos.ZERO.south(),
      BlockPos.ZERO.north(),
      BlockPos.ZERO.north().north(),
      BlockPos.ZERO.north().east(),
      BlockPos.ZERO.north().west(),
      BlockPos.ZERO.south().south(),
      BlockPos.ZERO.south().east(),
      BlockPos.ZERO.south().west(),
      BlockPos.ZERO.west().west(),
      BlockPos.ZERO.east().east()
   };
   public static final int MAX_CONSUME_TICKS = 20;
   private final OfferingPillarTileEntity.Config config = new OfferingPillarTileEntity.Config();
   private boolean populated;
   private int zoneId;
   private int offeringCount;
   private int offeringTarget;
   private PartialEntity boss;
   private final Map<String, Integer> modifiers;
   private final List<ItemStack> loot;
   private ItemStack heldItem;
   private UUID itemPlacedBy;
   public int ticksToConsume = 20;
   public int ticksToConsumeOld;
   public boolean consuming;

   public OfferingPillarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.OFFERING_PILLAR_TILE_ENTITY, pos, state);
      this.modifiers = new LinkedHashMap<>();
      this.loot = new ArrayList<>();
      this.heldItem = ItemStack.EMPTY;
      this.consuming = false;
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public int getOfferingCount() {
      return this.offeringCount;
   }

   public void setOfferingCount(int offeringCount) {
      this.offeringCount = offeringCount;
   }

   public int getOfferingTarget() {
      return this.offeringTarget;
   }

   public PartialEntity getBoss() {
      return this.boss;
   }

   public Map<String, Integer> getModifiers() {
      return this.modifiers;
   }

   public List<ItemStack> getLoot() {
      return this.loot;
   }

   public ItemStack getHeldItem() {
      return this.heldItem;
   }

   public void setHeldItem(ItemStack heldItem) {
      this.heldItem = heldItem;
   }

   public UUID getItemPlacedBy() {
      return this.itemPlacedBy;
   }

   public void setItemPlacedBy(UUID itemPlacedBy) {
      this.itemPlacedBy = itemPlacedBy;
   }

   public OfferingBossFight createFight() {
      return new OfferingBossFight(this.getBlockPos(), this.config.zone, this.zoneId, this.config.roomStyle, this.boss, this.modifiers, this.loot);
   }

   public void onPopulate() {
      ChunkRandom random = ChunkRandom.any();
      random.setDecoratorSeed(
         ServerVaults.get(this.level).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.getBlockPos().getX(), this.getBlockPos().getZ(), 329045113
      );
      this.offeringTarget = this.config.count.get(random);
      this.boss = this.config.boss.getRandom(random).orElseGet(() -> PartialEntity.parse("minecraft:pig", true).orElseThrow());
      this.populated = true;
   }

   public void onLoad() {
      super.onLoad();
      if (this.level instanceof ServerLevel world) {
         if (!this.isPopulated()) {
            this.onPopulate();
         }

         if (this.zoneId < 0 && this.config.zone != null) {
            this.zoneId = WorldZonesData.get(world.getServer())
               .getOrCreate(world.dimension())
               .add(new WorldZone().add(this.config.zone.offset(this.getBlockPos())).setModify(false));
         }
      }
   }

   public static void tick(Level world, BlockPos pos, BlockState state, OfferingPillarTileEntity entity) {
      if (world instanceof ClientLevel clientWorld) {
         entity.tickClient(clientWorld, pos, state);
      } else if (world instanceof ServerLevel serverWorld) {
         entity.tickServer(serverWorld, pos, state);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void tickClient(ClientLevel world, BlockPos pos, BlockState state) {
      this.playEffects(world);
      if (!this.heldItem.isEmpty()) {
         this.ticksToConsumeOld = this.ticksToConsume;
         if (this.ticksToConsume > 0) {
            this.ticksToConsume--;
         }
      }
   }

   public void tickServer(ServerLevel world, BlockPos pos, BlockState state) {
      if (!this.isPopulated()) {
         this.onPopulate();
      }

      if (!this.heldItem.isEmpty()) {
         if (this.ticksToConsume > 0) {
            if (!this.consuming) {
               this.consuming = true;
               world.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            this.ticksToConsume--;
         } else {
            this.ticksToConsume = 20;
            this.consuming = false;
            ItemStack original = this.heldItem.copy();
            if (this.config.offering.test(this.heldItem)) {
               CommonEvents.SCAVENGER_ALTAR_CONSUME.invoke(world, this);
            }

            if (this.heldItem.getCount() != original.getCount()) {
               world.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
               world.playSound(null, pos, SoundEvents.PLAYER_BURP, SoundSource.BLOCKS, 1.0F, 1.0F);
               ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ScavengerAltarConsumeMessage(this.getBlockPos()));
            } else {
               world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
            }

            Block.popResource(world, pos.above(), this.heldItem);
            this.setHeldItem(ItemStack.EMPTY);
            this.setItemPlacedBy(null);
         }

         this.sendUpdates();
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected void playEffects(Level world) {
      RandomSource random = JavaRandom.ofNanoTime();
      BlockPos above = this.getBlockPos().above();
      Vec3 center = new Vec3(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);
      int particleSpeed = this.consuming && this.ticksToConsume > 16.0F ? 5 : 40;

      for (BlockPos pos : PARTICLE_POSITIONS) {
         for (int offset = 0; offset < 3; offset++) {
            if (random.nextInt(particleSpeed) == 0) {
               world.addParticle(
                  (ParticleOptions)ModParticles.SCAVENGER_CORE.get(),
                  center.x,
                  center.y,
                  center.z,
                  -0.5F + random.nextFloat() + pos.getX(),
                  -2.0F + random.nextFloat() + pos.getY() + offset,
                  -0.5F + random.nextFloat() + pos.getZ()
               );
            }
         }
      }

      if (random.nextInt(10) == 0) {
         Vec3 pos = new Vec3(
            above.getX() + 0.5F + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F / 10.0F,
            above.getY() - 0.25F + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F / 10.0F,
            above.getZ() + 0.5F + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F / 10.0F
         );
         world.addParticle((ParticleOptions)ModParticles.SCAVENGER_CORE.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
      }
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      this.config.writeNbt().ifPresent(tag -> nbt.put("config", tag));
      Adapters.BOOLEAN.writeNbt(this.populated).ifPresent(tag -> nbt.put("populated", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.zoneId)).ifPresent(tag -> nbt.put("zoneId", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.offeringCount)).ifPresent(tag -> nbt.put("offeringCount", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.offeringTarget)).ifPresent(tag -> nbt.put("offeringTarget", tag));
      Adapters.PARTIAL_ENTITY.writeNbt(this.boss).ifPresent(tag -> nbt.put("boss", tag));
      CompoundTag modifiers = new CompoundTag();
      this.modifiers.forEach((name, count) -> Adapters.INT.writeNbt(count).ifPresent(tag -> modifiers.put(name, tag)));
      nbt.put("modifiers", modifiers);
      ListTag loot = new ListTag();
      this.loot.forEach(stack -> Adapters.ITEM_STACK.writeNbt(stack).ifPresent(loot::add));
      nbt.put("loot", loot);
      Adapters.ITEM_STACK.writeNbt(this.heldItem).ifPresent(tag -> nbt.put("heldItem", tag));
      Adapters.UUID.writeNbt(this.itemPlacedBy).ifPresent(tag -> nbt.put("itemPlacedBy", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.ticksToConsume)).ifPresent(tag -> nbt.put("ticksToConsume", tag));
      Adapters.INT.writeNbt(Integer.valueOf(this.ticksToConsumeOld)).ifPresent(tag -> nbt.put("ticksToConsumeOld", tag));
      Adapters.BOOLEAN.writeNbt(this.consuming).ifPresent(tag -> nbt.put("consuming", tag));
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (!nbt.contains("config")) {
         this.config.readNbt(nbt);
      } else {
         this.populated = Adapters.BOOLEAN.readNbt(nbt.get("populated")).orElse(false);
         this.zoneId = Adapters.INT.readNbt(nbt.get("zoneId")).orElse(-1);
         this.offeringCount = Adapters.INT.readNbt(nbt.get("offeringCount")).orElse(0);
         this.offeringTarget = Adapters.INT.readNbt(nbt.get("offeringTarget")).orElse(0);
         this.boss = Adapters.PARTIAL_ENTITY.readNbt(nbt.get("boss")).orElse(null);
         CompoundTag modifiers = nbt.getCompound("modifiers");
         this.modifiers.clear();

         for (String name : modifiers.getAllKeys()) {
            Adapters.INT.readNbt(modifiers.get(name)).ifPresent(tag -> this.modifiers.put(name, tag));
         }

         ListTag loot = nbt.getList("loot", 10);
         this.loot.clear();

         for (int i = 0; i < loot.size(); i++) {
            Adapters.ITEM_STACK.readNbt(loot.getCompound(i)).ifPresent(this.loot::add);
         }

         this.heldItem = Adapters.ITEM_STACK.readNbt(nbt.get("heldItem")).orElse(ItemStack.EMPTY);
         this.itemPlacedBy = Adapters.UUID.readNbt(nbt.get("itemPlacedBy")).orElse(null);
         this.ticksToConsume = Adapters.INT.readNbt(nbt.get("ticksToConsume")).orElse(20);
         this.ticksToConsumeOld = Adapters.INT.readNbt(nbt.get("ticksToConsumeOld")).orElse(0);
         this.consuming = Adapters.BOOLEAN.readNbt(nbt.get("consuming")).orElse(false);
      }
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public static class Config implements INbtSerializable<CompoundTag> {
      private ItemPredicate offering;
      private IntRoll count;
      private final WeightedList<PartialEntity> boss = new WeightedList<>();
      private BlockCuboid zone;
      private OfferingBossFight.RoomStyle roomStyle;

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.ITEM_PREDICATE.writeNbt(this.offering).ifPresent(tag -> nbt.put("offering", tag));
            Adapters.INT_ROLL.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
            ListTag boss = new ListTag();
            this.boss.forEach((entity, weight) -> Adapters.PARTIAL_ENTITY.writeNbt(entity).ifPresent(tag -> {
               if (tag instanceof CompoundTag compound) {
                  Adapters.DOUBLE.writeNbt(weight).ifPresent(tag1 -> compound.put("weight", tag1));
                  boss.add(tag);
               }
            }));
            nbt.put("boss", boss);
            Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
            Adapters.ofEnum(OfferingBossFight.RoomStyle.class, EnumAdapter.Mode.NAME).writeNbt(this.roomStyle).ifPresent(tag -> nbt.put("roomStyle", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.offering = Adapters.ITEM_PREDICATE.readNbt(nbt.get("offering")).orElse(ItemPredicate.FALSE);
         this.count = Adapters.INT_ROLL.readNbt(nbt.get("count")).orElse(IntRoll.ofConstant(3));
         ListTag boss = nbt.getList("boss", 10);
         this.boss.clear();

         for (int i = 0; i < boss.size(); i++) {
            double weight = Adapters.DOUBLE.readNbt(boss.getCompound(i).get("weight")).orElse(1.0);
            Adapters.PARTIAL_ENTITY.readNbt(boss.getCompound(i)).ifPresent(entity -> this.boss.put(entity, weight));
         }

         this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
         this.roomStyle = Adapters.ofEnum(OfferingBossFight.RoomStyle.class, EnumAdapter.Mode.NAME)
            .readNbt(nbt.get("roomStyle"))
            .orElse(OfferingBossFight.RoomStyle.BOSS_1);
      }
   }
}

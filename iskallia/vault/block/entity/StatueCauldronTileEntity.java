package iskallia.vault.block.entity;

import iskallia.vault.block.StatueCauldronBlock;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.StatueType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class StatueCauldronTileEntity extends TileEntity implements ITickableTileEntity {
   private final ItemStackHandler itemHandler = this.createHandler();
   private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);
   private final Predicate<ItemEntity> itemPredicate = itemEntity -> itemEntity.func_92059_d().func_77973_b() instanceof LootStatueBlockItem;
   private UUID owner;
   private int statueCount;
   private int requiredAmount;
   private List<String> names = new ArrayList<>();

   public List<String> getNames() {
      return this.names;
   }

   public StatueCauldronTileEntity() {
      super(ModBlocks.STATUE_CAULDRON_TILE_ENTITY);
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public UUID getOwner() {
      return this.owner;
   }

   public void setStatueCount(int statueCount) {
      this.statueCount = statueCount;
   }

   public int getStatueCount() {
      return this.statueCount;
   }

   public void setRequiredAmount(int requiredAmount) {
      this.requiredAmount = requiredAmount;
   }

   public int getRequiredAmount() {
      return this.requiredAmount;
   }

   public void addName(String name) {
      this.names.add(name);
   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         for (ItemEntity e : this.field_145850_b
            .func_225316_b(ItemEntity.class, new AxisAlignedBB(this.func_174877_v()).func_72314_b(1.0, 1.0, 1.0), this.itemPredicate)) {
            this.handler.ifPresent(h -> {
               if (h.insertItem(0, e.func_92059_d(), true).func_190926_b()) {
                  e.func_70106_y();
               }

               this.func_70296_d();
               this.sendUpdates();
            });
         }

         if (this.statueCount >= this.requiredAmount) {
            List<String> nameList = new ArrayList<>(this.names);
            Collections.shuffle(nameList);
            String name = nameList.size() == 0 ? "iGoodie" : nameList.get(0);
            if (name == null || name.isEmpty()) {
               name = "iGoodie";
            }

            ItemStack statue = LootStatueBlockItem.getStatueBlockItem(name, StatueType.OMEGA);
            ItemEntity itemEntity = new ItemEntity(
               this.field_145850_b,
               this.func_174877_v().func_177958_n() + 0.5,
               this.func_174877_v().func_177956_o() + 1.2,
               this.func_174877_v().func_177952_p() + 0.5,
               statue
            );
            this.field_145850_b.func_217376_c(itemEntity);
            this.field_145850_b
               .func_175656_a(this.func_174877_v(), (BlockState)ModBlocks.STATUE_CAULDRON.func_176223_P().func_206870_a(StatueCauldronBlock.field_176591_a, 0));
            this.statueCount = 0;
            this.names.clear();
            this.sendUpdates();
         }
      }
   }

   private void bubbleCauldron(ServerWorld world) {
      int particleCount = 100;
      world.func_184148_a(
         null,
         this.field_174879_c.func_177958_n(),
         this.field_174879_c.func_177956_o(),
         this.field_174879_c.func_177952_p(),
         ModSounds.CAULDRON_BUBBLES_SFX,
         SoundCategory.MASTER,
         1.0F,
         (float)Math.random()
      );
      world.func_195598_a(
         ParticleTypes.field_197607_R,
         this.field_174879_c.func_177958_n() + 0.5,
         this.field_174879_c.func_177956_o() + 0.5,
         this.field_174879_c.func_177952_p() + 0.5,
         particleCount,
         0.0,
         0.0,
         0.0,
         Math.PI
      );
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(64) {
         protected void onContentsChanged(int slot) {
            StatueCauldronTileEntity.this.sendUpdates();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if ((Integer)StatueCauldronTileEntity.this.func_195044_w().func_177229_b(CauldronBlock.field_176591_a) != 3) {
               return false;
            } else if (StatueCauldronTileEntity.this.getStatueCount() >= StatueCauldronTileEntity.this.getRequiredAmount()) {
               return false;
            } else if (stack.func_77973_b() instanceof LootStatueBlockItem) {
               StatueType type = MiscUtils.getEnumEntry(StatueType.class, stack.func_196082_o().func_74775_l("BlockEntityTag").func_74762_e("StatueType"));
               return type.doesStatueCauldronAccept();
            } else {
               return false;
            }
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (simulate && this.isItemValid(slot, stack)) {
               int amount = ModConfigs.STATUE_RECYCLING.getItemValue(stack.func_77973_b().getRegistryName().toString());
               StatueCauldronTileEntity.this.statueCount = Math.min(
                  StatueCauldronTileEntity.this.statueCount + amount, StatueCauldronTileEntity.this.requiredAmount
               );
               CompoundNBT tag = stack.func_196082_o();
               CompoundNBT blockData = tag.func_74775_l("BlockEntityTag");
               String name = blockData.func_74779_i("PlayerNickname");
               if (!name.isEmpty()) {
                  for (int i = 0; i < amount; i++) {
                     StatueCauldronTileEntity.this.addName(name);
                  }
               }

               if (StatueCauldronTileEntity.this.field_145850_b != null && !StatueCauldronTileEntity.this.field_145850_b.field_72995_K) {
                  StatueCauldronTileEntity.this.bubbleCauldron((ServerWorld)StatueCauldronTileEntity.this.field_145850_b);
               }

               StatueCauldronTileEntity.this.sendUpdates();
               return ItemStack.field_190927_a;
            } else {
               return stack;
            }
         }
      };
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      if (this.owner != null) {
         compound.func_186854_a("Owner", this.owner);
      }

      ListNBT nameList = new ListNBT();
      if (this.names != null && !this.names.isEmpty()) {
         int i = 0;

         for (String name : this.names) {
            CompoundNBT nameNbt = new CompoundNBT();
            nameNbt.func_74778_a("name" + i++, name);
            nameList.add(nameNbt);
         }
      }

      compound.func_218657_a("NameList", nameList);
      compound.func_74768_a("StatueCount", this.statueCount);
      compound.func_74768_a("RequiredAmount", this.requiredAmount);
      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      if (nbt.func_150297_b("Owner", 11)) {
         this.owner = nbt.func_186857_a("Owner");
      }

      ListNBT nameList = nbt.func_150295_c("NameList", 10);
      int i = 0;
      this.names.clear();

      for (INBT nameNbt : nameList) {
         this.names.add(((CompoundNBT)nameNbt).func_74779_i("name" + i++));
      }

      this.statueCount = nbt.func_74762_e("StatueCount");
      this.requiredAmount = nbt.func_74762_e("RequiredAmount");
      super.func_230337_a_(state, nbt);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT compound = super.func_189517_E_();
      if (this.owner != null) {
         compound.func_186854_a("Owner", this.owner);
      }

      ListNBT nameList = new ListNBT();
      if (this.names != null && !this.names.isEmpty()) {
         int i = 0;

         for (String name : this.names) {
            CompoundNBT nameNbt = new CompoundNBT();
            nameNbt.func_74778_a("name" + i++, name);
            nameList.add(nameNbt);
         }
      }

      compound.func_218657_a("NameList", nameList);
      compound.func_74768_a("StatueCount", this.statueCount);
      compound.func_74768_a("RequiredAmount", this.requiredAmount);
      return compound;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT tag = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), tag);
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 11);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public void setNames(ListNBT nameList) {
      this.names.clear();
      int i = 0;

      for (INBT nameNbt : nameList) {
         this.names.add(((CompoundNBT)nameNbt).func_74779_i("name" + i++));
      }
   }
}

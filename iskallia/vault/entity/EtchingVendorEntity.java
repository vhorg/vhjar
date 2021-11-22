package iskallia.vault.entity;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EtchingVendorEntity extends MobEntity {
   private static final DataParameter<BlockPos> VENDOR_POS = EntityDataManager.func_187226_a(EtchingVendorEntity.class, DataSerializers.field_187200_j);

   public EtchingVendorEntity(EntityType<? extends MobEntity> type, World world) {
      super(type, world);
      this.func_184224_h(true);
      this.func_189654_d(true);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(VENDOR_POS, BlockPos.field_177992_a);
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg = new GoalSelector(this.field_70170_p.func_234924_Y_());
      this.field_70715_bh = new GoalSelector(this.field_70170_p.func_234924_Y_());
      this.field_70714_bg.func_75776_a(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.field_70714_bg.func_75776_a(10, new LookRandomlyGoal(this));
   }

   public void setVendorPos(BlockPos pos) {
      this.field_70180_af.func_187227_b(VENDOR_POS, pos);
   }

   public BlockPos getVendorPos() {
      return (BlockPos)this.field_70180_af.func_187225_a(VENDOR_POS);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      this.func_110160_i(true, false);
      if (!this.field_70170_p.func_201670_d()) {
         if (!this.isValid()) {
            this.func_70106_y();
         }
      }
   }

   public boolean isValid() {
      if (this.field_70170_p.func_234923_W_() != Vault.VAULT_KEY) {
         return false;
      } else if (!this.field_70170_p.isAreaLoaded(this.getVendorPos(), 1)) {
         return false;
      } else if (this.func_195048_a(Vector3d.func_237489_a_(this.getVendorPos())) > 4.0) {
         return false;
      } else {
         TileEntity te = this.field_70170_p.func_175625_s(this.getVendorPos());
         return !(te instanceof EtchingVendorControllerTileEntity)
            ? false
            : ((EtchingVendorControllerTileEntity)te).getMonitoredEntityId() == this.func_145782_y();
      }
   }

   @Nullable
   public EtchingVendorControllerTileEntity getControllerTile() {
      return (EtchingVendorControllerTileEntity)this.field_70170_p.func_175625_s(this.getVendorPos());
   }

   protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
      if (player instanceof ServerPlayerEntity) {
         NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
            public ITextComponent func_145748_c_() {
               return new StringTextComponent("Etching Trader");
            }

            @Nullable
            public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerx) {
               return new EtchingTradeContainer(windowId, playerInventory, EtchingVendorEntity.this.func_145782_y());
            }
         }, buf -> buf.writeInt(this.func_145782_y()));
      }

      return ActionResultType.func_233537_a_(this.field_70170_p.field_72995_K);
   }

   public boolean func_213397_c(double distanceToClosestPlayer) {
      return false;
   }

   @Nullable
   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187910_gj;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return SoundEvents.field_187912_gl;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187911_gk;
   }
}

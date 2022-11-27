package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.init.ModConfigs;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class KillBossObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("kill_boss", Objective.class).with(Version.v1_0, KillBossObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUID> BOSS_ID = FieldKey.of("boss_id", UUID.class)
      .with(Version.v1_0, Adapter.ofUUID().asNullable(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> BOSS_TYPE = FieldKey.of("boss_type", ResourceLocation.class)
      .with(Version.v1_0, Adapter.ofIdentifier(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<BlockPos> BOSS_POS = FieldKey.of("boss_pos", BlockPos.class)
      .with(Version.v1_0, Adapter.ofBlockPos().asNullable(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<String> BOSS_NAME = FieldKey.of("boss_name", String.class)
      .with(Version.v1_0, Adapter.ofString().asNullable(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> BOSS_DEAD = FieldKey.of("boss_dead", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<LootTableKey> LOOT_TABLE = FieldKey.of("loot_table", LootTableKey.class)
      .with(Version.v1_0, Adapter.<LootTableKey, LootTable>ofRegistryKey(() -> VaultRegistry.LOOT_TABLE).asNullable(), DISK.all())
      .register(FIELDS);

   protected KillBossObjective() {
   }

   public static Objective ofStandardConfig(int level, RandomSource random) {
      EntityType<?> type = ModConfigs.VAULT_MOBS.getForLevel(level).BOSS_POOL.getRandom(random).orElseThrow().getType();
      return new KillBossObjective().set(BOSS_TYPE, type.getRegistryName()).set(BOSS_NAME, "Boss");
   }

   public static Objective ofRaffleConfig(String name, int level, RandomSource random) {
      EntityType<?> type = ModConfigs.VAULT_MOBS.getForLevel(level).RAFFLE_BOSS_POOL.getRandom(random).orElseThrow().getType();
      return new KillBossObjective().set(BOSS_TYPE, type.getRegistryName()).set(BOSS_NAME, name);
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.ENTITY_DEATH.register(this, event -> {
         if (event.getEntity().getUUID().equals(this.get(BOSS_ID))) {
            this.set(BOSS_DEAD);
         }
      });
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.get(BOSS_ID) == null) {
         if (this.get(BOSS_POS) == null) {
            return;
         }

         this.spawnBoss(world, vault, this.get(BOSS_POS));
      }

      if (this.has(BOSS_DEAD)) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (this.has(BOSS_DEAD)) {
         super.tickListener(world, vault, listener);
      }
   }

   private Entity spawnBoss(VirtualWorld world, Vault vault, BlockPos pos) {
      EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(this.get(BOSS_TYPE));
      if (type == null) {
         return null;
      } else {
         LivingEntity boss = (LivingEntity)type.create(world);
         if (boss == null) {
            return null;
         } else {
            boss.setCustomName(new TextComponent(this.get(BOSS_NAME)));
            boss.moveTo(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, world.random.nextFloat() * 360.0F, 0.0F);
            world.addWithUUID(boss);
            this.set(BOSS_ID, boss.getUUID());
            if (boss instanceof Mob mob) {
               mob.setPersistenceRequired();
            }

            if (boss instanceof FighterEntity fighter) {
               fighter.changeSize(2.0F);
               fighter.bossInfo.setVisible(true);
            }

            if (boss instanceof VaultBoss vaultBoss) {
               vaultBoss.getServerBossInfo().setVisible(true);
            }

            EntityScaler.scale(vault, boss);

            for (int i = 0; i < 5; i++) {
               LightningBolt bolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(world);
               if (bolt != null) {
                  bolt.moveTo(Vec3.atBottomCenterOf(pos.offset(world.random.nextInt(100) - 50, 0, world.random.nextInt(100) - 50)));
                  bolt.setVisualOnly(true);
                  world.addFreshEntity(bolt);
               }
            }

            return boss;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (this.has(BOSS_DEAD)) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      int width = window.getGuiScaledWidth();
      int height = window.getGuiScaledHeight();
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Component txt = new TextComponent("Kill the Boss!").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD);
      Minecraft.getInstance()
         .font
         .drawInBatch(
            txt.getVisualOrderText(), 8.0F, height - 54, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
         );
      buffer.endBatch();
      return true;
   }

   @Override
   public boolean isActive(Objective objective) {
      if (objective == this && !this.has(BOSS_DEAD)) {
         return true;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(objective)) {
               return true;
            }
         }

         return false;
      }
   }
}

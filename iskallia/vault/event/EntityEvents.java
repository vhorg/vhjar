package iskallia.vault.event;

import iskallia.vault.Vault;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultDoorBlock;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.VaultFighterEntity;
import iskallia.vault.entity.VaultGuardianEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.StatueType;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.raid.VaultRaid;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class EntityEvents {
   @SubscribeEvent
   public static void onEntityTick(LivingUpdateEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K
         && event.getEntity() instanceof MonsterEntity
         && !(event.getEntity() instanceof EternalEntity)
         && event.getEntity().field_70170_p.func_234923_W_() == Vault.VAULT_KEY
         && !event.getEntity().func_184216_O().contains("VaultScaled")) {
         MonsterEntity entity = (MonsterEntity)event.getEntity();
         VaultRaid raid = VaultRaidData.get((ServerWorld)entity.field_70170_p).getAt(entity.func_233580_cy_());
         if (raid != null) {
            EntityScaler.scaleVault(entity, raid.level, new Random(), EntityScaler.Type.MOB);
            entity.func_184216_O().add("VaultScaled");
            entity.func_110163_bv();
         }
      }
   }

   @SubscribeEvent
   public static void onEntityTick2(LivingUpdateEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K && event.getEntity() instanceof FighterEntity) {
         ((FighterEntity)event.getEntity()).func_110163_bv();
      }
   }

   @SubscribeEvent
   public static void onEntityTick3(EntityConstructing event) {
      if (!event.getEntity().field_70170_p.field_72995_K
         && event.getEntity() instanceof AreaEffectCloudEntity
         && event.getEntity().field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
         event.getEntity()
            .func_184102_h()
            .func_212871_a_(
               new TickDelayedTask(
                  event.getEntity().func_184102_h().func_71259_af() + 2,
                  () -> {
                     if (event.getEntity().func_184216_O().contains("vault_door")) {
                        for (int ox = -1; ox <= 1; ox++) {
                           for (int oz = -1; oz <= 1; oz++) {
                              BlockPos pos = event.getEntity().func_233580_cy_().func_177982_a(ox, 0, oz);
                              BlockState state = event.getEntity().field_70170_p.func_180495_p(pos);
                              if (state.func_177230_c() == Blocks.field_150454_av) {
                                 BlockState newState = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)VaultDoorBlock.VAULT_DOORS
                                                .get(event.getEntity().field_70170_p.field_73012_v.nextInt(VaultDoorBlock.VAULT_DOORS.size()))
                                                .func_176223_P()
                                                .func_206870_a(DoorBlock.field_176520_a, state.func_177229_b(DoorBlock.field_176520_a)))
                                             .func_206870_a(DoorBlock.field_176519_b, state.func_177229_b(DoorBlock.field_176519_b)))
                                          .func_206870_a(DoorBlock.field_176521_M, state.func_177229_b(DoorBlock.field_176521_M)))
                                       .func_206870_a(DoorBlock.field_176522_N, state.func_177229_b(DoorBlock.field_176522_N)))
                                    .func_206870_a(DoorBlock.field_176523_O, state.func_177229_b(DoorBlock.field_176523_O));
                                 PortalPlacer placer = new PortalPlacer(
                                    (pos1, random, facing) -> null, (pos1, random, facing) -> Blocks.field_150357_h.func_176223_P()
                                 );
                                 placer.place(
                                    event.getEntity().field_70170_p, pos, ((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176735_f(), 1, 2
                                 );
                                 placer.place(
                                    event.getEntity().field_70170_p,
                                    pos.func_177972_a(((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176734_d()),
                                    ((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176735_f(),
                                    1,
                                    2
                                 );
                                 placer.place(
                                    event.getEntity().field_70170_p,
                                    pos.func_177967_a(((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176734_d(), 2),
                                    ((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176735_f(),
                                    1,
                                    2
                                 );
                                 placer.place(
                                    event.getEntity().field_70170_p,
                                    pos.func_177972_a((Direction)state.func_177229_b(DoorBlock.field_176520_a)),
                                    ((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176735_f(),
                                    1,
                                    2
                                 );
                                 placer.place(
                                    event.getEntity().field_70170_p,
                                    pos.func_177967_a((Direction)state.func_177229_b(DoorBlock.field_176520_a), 2),
                                    ((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176735_f(),
                                    1,
                                    2
                                 );
                                 event.getEntity().field_70170_p.func_180501_a(pos.func_177984_a(), Blocks.field_150350_a.func_176223_P(), 27);
                                 event.getEntity().field_70170_p.func_180501_a(pos, newState, 11);
                                 event.getEntity()
                                    .field_70170_p
                                    .func_180501_a(pos.func_177984_a(), (BlockState)newState.func_206870_a(DoorBlock.field_176523_O, DoubleBlockHalf.UPPER), 11);

                                 for (int x = -30; x <= 30; x++) {
                                    for (int z = -30; z <= 30; z++) {
                                       for (int y = -15; y <= 15; y++) {
                                          BlockPos c = pos.func_177982_a(x, y, z);
                                          BlockState s = event.getEntity().field_70170_p.func_180495_p(c);
                                          if (s.func_177230_c() == Blocks.field_196562_aR) {
                                             event.getEntity()
                                                .field_70170_p
                                                .func_180501_a(
                                                   c,
                                                   (BlockState)Blocks.field_150486_ae
                                                      .func_176223_P()
                                                      .func_206870_a(
                                                         ChestBlock.field_176459_a,
                                                         Direction.func_176731_b(event.getEntity().field_70170_p.field_73012_v.nextInt(4))
                                                      ),
                                                   2
                                                );
                                             TileEntity te = event.getEntity().field_70170_p.func_175625_s(c);
                                             if (te instanceof ChestTileEntity) {
                                                ((ChestTileEntity)te).func_189404_a(Vault.id("chest/treasure"), 0L);
                                             }
                                          } else if (s.func_177230_c() == Blocks.field_196566_aV) {
                                             event.getEntity()
                                                .field_70170_p
                                                .func_180501_a(
                                                   c,
                                                   (BlockState)Blocks.field_150486_ae
                                                      .func_176223_P()
                                                      .func_206870_a(
                                                         ChestBlock.field_176459_a,
                                                         Direction.func_176731_b(event.getEntity().field_70170_p.field_73012_v.nextInt(4))
                                                      ),
                                                   2
                                                );
                                             TileEntity te = event.getEntity().field_70170_p.func_175625_s(c);
                                             if (te instanceof ChestTileEntity) {
                                                ((ChestTileEntity)te).func_189404_a(Vault.id("chest/treasure_extra"), 0L);
                                             }
                                          } else if (s.func_177230_c() == Blocks.field_150357_h) {
                                             event.getEntity().field_70170_p.func_175656_a(c, ModBlocks.VAULT_BEDROCK.func_176223_P());
                                          }
                                       }
                                    }
                                 }

                                 event.getEntity().func_70106_y();
                              }
                           }
                        }
                     }
                  }
               )
            );
      }
   }

   @SubscribeEvent
   public static void onEntityTick5(LivingUpdateEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K
         && event.getEntity().field_70170_p.func_234923_W_() == Vault.VAULT_KEY
         && event.getEntity() instanceof ArmorStandEntity) {
         event.getEntityLiving().field_70170_p.func_175656_a(event.getEntityLiving().func_233580_cy_(), ModBlocks.OBELISK.func_176223_P());
         event.getEntityLiving().func_70106_y();
      }
   }

   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K
         && event.getEntity().field_70170_p.func_234923_W_() == Vault.VAULT_KEY
         && event.getEntity().func_184216_O().contains("VaultBoss")) {
         ServerWorld world = (ServerWorld)event.getEntityLiving().field_70170_p;
         VaultRaid raid = VaultRaidData.get(world).getAt(event.getEntity().func_233580_cy_());
         if (raid != null) {
            raid.bosses.remove(event.getEntity().func_110124_au());
            if (raid.isFinalVault) {
               return;
            }

            raid.runForPlayers(
               world.func_73046_m(),
               player -> {
                  for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                     ItemStack stack = player.func_184582_a(slot);
                     float chance = ModAttributes.GEAR_LEVEL_CHANCE.getOrDefault(stack, 1.0F).getValue(stack);
                     if (world.func_201674_k().nextFloat() < chance) {
                        VaultGear.addLevel(stack, 1.0F);
                     }
                  }

                  Builder builder = new Builder(world)
                     .func_216023_a(world.field_73012_v)
                     .func_216015_a(LootParameters.field_216281_a, player)
                     .func_216015_a(LootParameters.field_237457_g_, event.getEntity().func_213303_ch())
                     .func_216015_a(LootParameters.field_216283_c, event.getSource())
                     .func_216021_b(LootParameters.field_216284_d, event.getSource().func_76346_g())
                     .func_216021_b(LootParameters.field_216285_e, event.getSource().func_76364_f())
                     .func_216015_a(LootParameters.field_216282_b, player)
                     .func_186469_a(player.func_184817_da());
                  LootContext ctx = builder.func_216022_a(LootParameterSets.field_216263_d);
                  NonNullList<ItemStack> stacks = NonNullList.func_191196_a();
                  stacks.addAll(world.func_73046_m().func_200249_aQ().func_186521_a(Vault.id("chest/boss")).func_216113_a(ctx));
                  if (raid.playerBossName != null && !raid.playerBossName.isEmpty()) {
                     stacks.add(LootStatueBlockItem.forVaultBoss(event.getEntity().func_200201_e().getString(), StatueType.VAULT_BOSS.ordinal(), false));
                     if (world.field_73012_v.nextInt(4) != 0) {
                        stacks.add(ItemTraderCore.generate(event.getEntity().func_200201_e().getString(), 100, true, ItemTraderCore.CoreType.RAFFLE));
                     }
                  }

                  int count = EternalsData.get(world).getTotalEternals();
                  if (count != 0) {
                     stacks.add(new ItemStack(ModItems.ETERNAL_SOUL, world.field_73012_v.nextInt(count) + 1));
                  }

                  ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);
                  event.getEntity().func_199701_a_(crate);
                  FireworkRocketEntity fireworks = new FireworkRocketEntity(
                     world,
                     event.getEntity().func_226277_ct_(),
                     event.getEntity().func_226278_cu_(),
                     event.getEntity().func_226281_cx_(),
                     new ItemStack(Items.field_196152_dE)
                  );
                  world.func_217376_c(fireworks);
                  raid.won = true;
                  raid.ticksLeft = 400;
                  world.func_184148_a(
                     null,
                     player.func_226277_ct_(),
                     player.func_226278_cu_(),
                     player.func_226281_cx_(),
                     SoundEvents.field_194228_if,
                     SoundCategory.MASTER,
                     1.0F,
                     1.0F
                  );
                  StringTextComponent title = new StringTextComponent("Vault Cleared!");
                  title.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  Entity entity = event.getEntity();
                  IFormattableTextComponent entityName = entity instanceof FighterEntity
                     ? entity.func_200200_C_().func_230532_e_()
                     : entity.func_200600_R().func_212546_e().func_230532_e_();
                  entityName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14512414)));
                  IFormattableTextComponent subtitle = new StringTextComponent(" is defeated.");
                  subtitle.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  StringTextComponent actionBar = new StringTextComponent("You'll be teleported back soon...");
                  actionBar.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(14536734)));
                  STitlePacket titlePacket = new STitlePacket(Type.TITLE, title);
                  STitlePacket subtitlePacket = new STitlePacket(Type.SUBTITLE, entityName.func_230532_e_().func_230529_a_(subtitle));
                  player.field_71135_a.func_147359_a(titlePacket);
                  player.field_71135_a.func_147359_a(subtitlePacket);
                  player.func_146105_b(actionBar, true);
                  IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
                  playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
                  StringTextComponent text = new StringTextComponent(" cleared a Vault by defeating ");
                  text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
                  StringTextComponent punctuation = new StringTextComponent("!");
                  punctuation.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16777215)));
                  world.func_73046_m()
                     .func_184103_al()
                     .func_232641_a_(
                        playerName.func_230529_a_(text).func_230529_a_(entityName).func_230529_a_(punctuation), ChatType.CHAT, player.func_110124_au()
                     );
               }
            );
         }
      }
   }

   @SubscribeEvent
   public static void onEntityDrops(LivingDropsEvent event) {
      if (!event.getEntity().field_70170_p.field_72995_K) {
         if (event.getEntity().field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
            if (!(event.getEntity() instanceof VaultGuardianEntity)) {
               if (!(event.getEntity() instanceof VaultFighterEntity)) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntitySpawn(CheckSpawn event) {
      if (event.getEntity().func_130014_f_().func_234923_W_() == Vault.VAULT_KEY && !event.isSpawner()) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onPlayerDeathInVaults(LivingDeathEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (!entityLiving.field_70170_p.field_72995_K) {
         if (entityLiving instanceof ServerPlayerEntity) {
            if (entityLiving.field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
               ServerPlayerEntity player = (ServerPlayerEntity)entityLiving;
               Vector3d position = player.func_213303_ch();
               player.func_71121_q()
                  .func_184148_a(
                     null, position.field_72450_a, position.field_72448_b, position.field_72449_c, ModSounds.TIMER_KILL_SFX, SoundCategory.MASTER, 0.75F, 1.0F
                  );
               VaultRaid raid = VaultRaidData.get((ServerWorld)event.getEntity().field_70170_p).getAt(player.func_233580_cy_());
               if (raid != null) {
                  raid.finished = true;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerHurt(LivingDamageEvent event) {
      if (event.getEntity() instanceof PlayerEntity && !event.getEntity().field_70170_p.field_72995_K) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
         VaultRaid raid = VaultRaidData.get((ServerWorld)event.getEntity().field_70170_p).getAt(player.func_233580_cy_());
         if (raid != null) {
            if (raid.won) {
               event.setCanceled(true);
            }

            if (raid.isFinalVault && player.func_110143_aJ() - event.getAmount() <= 0.0F) {
               player.func_71121_q()
                  .func_184148_a(
                     null,
                     player.func_233580_cy_().func_177958_n(),
                     player.func_233580_cy_().func_177956_o(),
                     player.func_233580_cy_().func_177952_p(),
                     ModSounds.TIMER_KILL_SFX,
                     SoundCategory.MASTER,
                     0.75F,
                     1.0F
                  );
               event.setCanceled(true);
               IFormattableTextComponent text = new StringTextComponent("");
               text.func_230529_a_(new StringTextComponent(player.func_200200_C_().getString()).func_240699_a_(TextFormatting.GREEN));
               text.func_230529_a_(new StringTextComponent(" has fallen, F."));
               player.func_184102_h().func_184103_al().func_232641_a_(text, ChatType.CHAT, player.func_110124_au());
               raid.addSpectator(player);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onVaultGuardianDamage(LivingDamageEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (!entityLiving.field_70170_p.field_72995_K) {
         if (entityLiving instanceof VaultGuardianEntity) {
            Entity trueSource = event.getSource().func_76346_g();
            if (trueSource instanceof LivingEntity) {
               LivingEntity attacker = (LivingEntity)trueSource;
               attacker.func_70097_a(DamageSource.func_92087_a(entityLiving), 20.0F);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurtCrit(LivingHurtEvent event) {
      if (event.getSource().func_76346_g() instanceof LivingEntity) {
         LivingEntity source = (LivingEntity)event.getSource().func_76346_g();
         if (!source.field_70170_p.field_72995_K) {
            if (source.func_233645_dx_().func_233790_b_(ModAttributes.CRIT_CHANCE)) {
               double chance = source.func_233637_b_(ModAttributes.CRIT_CHANCE);
               if (source.func_233645_dx_().func_233790_b_(ModAttributes.CRIT_MULTIPLIER)) {
                  double multiplier = source.func_233637_b_(ModAttributes.CRIT_MULTIPLIER);
                  if (source.field_70170_p.field_73012_v.nextDouble() < chance) {
                     source.field_70170_p
                        .func_184148_a(
                           null,
                           source.func_226277_ct_(),
                           source.func_226278_cu_(),
                           source.func_226281_cx_(),
                           SoundEvents.field_187718_dS,
                           source.func_184176_by(),
                           1.0F,
                           1.0F
                        );
                     event.setAmount((float)(event.getAmount() * multiplier));
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurtTp(LivingHurtEvent event) {
      if (!event.getEntityLiving().field_70170_p.field_72995_K) {
         boolean direct = event.getSource().func_76364_f() == event.getSource().func_76346_g();
         if (direct && event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_CHANCE)) {
            double chance = event.getEntityLiving().func_233637_b_(ModAttributes.TP_CHANCE);
            if (event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_RANGE)) {
               double range = event.getEntityLiving().func_233637_b_(ModAttributes.TP_RANGE);
               if (event.getEntityLiving().field_70170_p.field_73012_v.nextDouble() < chance) {
                  for (int i = 0; i < 64; i++) {
                     if (teleportRandomly(event.getEntityLiving(), range)) {
                        event.getEntityLiving()
                           .field_70170_p
                           .func_184148_a(
                              null,
                              event.getEntityLiving().field_70169_q,
                              event.getEntityLiving().field_70167_r,
                              event.getEntityLiving().field_70166_s,
                              ModSounds.BOSS_TP_SFX,
                              event.getEntityLiving().func_184176_by(),
                              1.0F,
                              1.0F
                           );
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         } else if (!direct && event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_INDIRECT_CHANCE)) {
            double chance = event.getEntityLiving().func_233637_b_(ModAttributes.TP_INDIRECT_CHANCE);
            if (event.getEntityLiving().func_233645_dx_().func_233790_b_(ModAttributes.TP_RANGE)) {
               double range = event.getEntityLiving().func_233637_b_(ModAttributes.TP_RANGE);
               if (event.getEntityLiving().field_70170_p.field_73012_v.nextDouble() < chance) {
                  for (int ix = 0; ix < 64; ix++) {
                     if (teleportRandomly(event.getEntityLiving(), range)) {
                        event.getEntityLiving()
                           .field_70170_p
                           .func_184148_a(
                              null,
                              event.getEntityLiving().field_70169_q,
                              event.getEntityLiving().field_70167_r,
                              event.getEntityLiving().field_70166_s,
                              ModSounds.BOSS_TP_SFX,
                              event.getEntityLiving().func_184176_by(),
                              1.0F,
                              1.0F
                           );
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean teleportRandomly(LivingEntity entity, double range) {
      if (!entity.field_70170_p.func_201670_d() && entity.func_70089_S()) {
         double d0 = entity.func_226277_ct_() + (entity.field_70170_p.field_73012_v.nextDouble() - 0.5) * range * 2.0;
         double d1 = entity.func_226278_cu_() + (entity.field_70170_p.field_73012_v.nextInt((int)(range * 2.0)) - range);
         double d2 = entity.func_226281_cx_() + (entity.field_70170_p.field_73012_v.nextDouble() - 0.5) * range * 2.0;
         return entity.func_213373_a(d0, d1, d2, true);
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onEntityDestroy(LivingDestroyBlockEvent event) {
      if (event.getState().func_177230_c() instanceof VaultDoorBlock) {
         event.setCanceled(true);
      }
   }
}

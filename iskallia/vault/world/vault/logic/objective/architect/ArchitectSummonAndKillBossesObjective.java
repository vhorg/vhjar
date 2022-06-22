package iskallia.vault.world.vault.logic.objective.architect;

import com.google.common.collect.Iterables;
import iskallia.vault.Vault;
import iskallia.vault.config.FinalArchitectEventConfig;
import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.EffectMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.gen.structure.VaultJigsawHelper;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.VaultUtils;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.objective.architect.modifier.RandomVoteModifier;
import iskallia.vault.world.vault.logic.objective.architect.modifier.VoteModifier;
import iskallia.vault.world.vault.logic.objective.architect.processor.VaultPieceProcessor;
import iskallia.vault.world.vault.modifier.VaultModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.STitlePacket.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ArchitectSummonAndKillBossesObjective extends ArchitectObjective {
   private ResourceLocation roomPool = Vault.id("raid/rooms");
   private ResourceLocation tunnelPool = Vault.id("vault/tunnels");
   private int killedBosses = 0;
   private int totalKilledBossesNeeded = 0;
   private int knowledge = 0;
   private int totalKnowledgeNeeded = 0;
   protected UUID currentBossId = null;
   private float combinedMobHealthMultiplier = 0.0F;

   public ArchitectSummonAndKillBossesObjective(ResourceLocation id) {
      super(id);
      this.voteDowntimeTicks = 0;
      this.totalKilledBossesNeeded = ModConfigs.FINAL_ARCHITECT.getBossKillsNeeded();
      this.totalKnowledgeNeeded = ModConfigs.FINAL_ARCHITECT.getTotalKnowledgeNeeded();
   }

   @Override
   public void tick(VaultRaid vault, PlayerFilter filter, ServerWorld world) {
      if (!this.isCompleted()) {
         vault.getPlayers().forEach(vPlayer -> {
            if (filter.test(vPlayer.getPlayerId())) {
               this.onTick.execute(vault, vPlayer, world);
            }
         });
      }

      MinecraftServer srv = world.func_73046_m();
      vault.getPlayers()
         .stream()
         .filter(vPlayer -> filter.test(vPlayer.getPlayerId()))
         .forEach(
            vPlayer -> vPlayer.runIfPresent(
               srv,
               playerEntity -> {
                  VaultGoalMessage pkt = VaultGoalMessage.architectFinalEvent(
                     this.killedBosses, this.totalKilledBossesNeeded, this.knowledge, this.totalKnowledgeNeeded, this.activeSession, this.currentBossId != null
                  );
                  ModNetwork.CHANNEL.sendTo(pkt, playerEntity.field_71135_a.field_147371_a, NetworkDirection.PLAY_TO_CLIENT);
               }
            )
         );
      if (!this.isCompleted()) {
         if (this.activeSession != null) {
            this.activeSession.tick(world);
            if (this.activeSession.isFinished()) {
               this.finishVote(vault, this.activeSession, world);
               this.completedSessions.add(this.activeSession);
               this.activeSession = null;
            }
         }

         if (this.hasFulfilledObjective()) {
            this.setCompleted();
         }
      }
   }

   private boolean hasFulfilledObjective() {
      return this.killedBosses >= this.totalKilledBossesNeeded;
   }

   @Override
   public boolean createVotingSession(ServerWorld world, BlockPos origin) {
      if (this.getActiveSession() == null && this.currentBossId == null && !this.hasFulfilledObjective()) {
         VaultRaid vault = VaultRaidData.get(world).getAt(world, origin);
         if (vault == null) {
            return false;
         } else {
            VaultRoom room = (VaultRoom)Iterables.getFirst(vault.getGenerator().getPiecesAt(origin, VaultRoom.class), null);
            if (room == null) {
               return false;
            } else {
               List<Direction> availableDirections = new ArrayList<>();

               for (Direction dir : Direction.values()) {
                  if (dir.func_176740_k().func_176722_c() && VaultJigsawHelper.canExpand(vault, room, dir)) {
                     availableDirections.add(dir);
                  }
               }

               if (availableDirections.size() <= 1) {
                  return false;
               } else {
                  List<DirectionChoice> choices = new ArrayList<>();

                  for (Direction dirx : availableDirections) {
                     DirectionChoice choice = new DirectionChoice(dirx);
                     FinalArchitectEventConfig.ModifierPair pair = ModConfigs.FINAL_ARCHITECT.getRandomPair();
                     if (pair != null) {
                        VoteModifier modifier = ModConfigs.FINAL_ARCHITECT.getModifier(pair.getPositive());
                        if (modifier != null) {
                           choice.addModifier(modifier);
                        }

                        modifier = ModConfigs.FINAL_ARCHITECT.getModifier(pair.getNegative());
                        if (modifier != null) {
                           choice.addModifier(modifier);
                        }
                     }

                     choices.add(choice);
                  }

                  this.activeSession = new SummonAndKillBossesVotingSession(origin, choices);
                  EffectMessage msg = EffectMessage.playSound(SoundEvents.field_193807_ew, SoundCategory.PLAYERS, 0.6F, 1.0F);
                  vault.getPlayers()
                     .forEach(
                        vPlayer -> vPlayer.runIfPresent(
                           world.func_73046_m(), sPlayer -> ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sPlayer), msg)
                        )
                     );
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   @Override
   protected void finishVote(VaultRaid vault, VotingSession session, ServerWorld world) {
      vault.getGenerator()
         .getPiecesAt(session.getStabilizerPos(), VaultRoom.class)
         .stream()
         .findFirst()
         .ifPresent(
            room -> {
               DirectionChoice choice = session.getVotedDirection();
               List<VoteModifier> modifiers = new ArrayList<>();
               choice.getFinalArchitectModifiers().forEach(modifier -> {
                  if (modifier instanceof RandomVoteModifier) {
                     modifiers.add(((RandomVoteModifier)modifier).rollModifier());
                  } else {
                     modifiers.add(modifier);
                  }
               });
               EffectMessage msg = EffectMessage.playSound(SoundEvents.field_193807_ew, SoundCategory.PLAYERS, 0.6F, 1.0F);
               vault.getPlayers()
                  .forEach(
                     vPlayer -> vPlayer.runIfPresent(
                        world.func_73046_m(), sPlayer -> ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sPlayer), msg)
                     )
                  );
               JigsawPiece roomPiece = modifiers.stream()
                  .map(modifier -> modifier.getSpecialRoom(this, vault))
                  .filter(Objects::nonNull)
                  .findFirst()
                  .orElse(null);
               IFormattableTextComponent txt = new StringTextComponent("").func_230529_a_(choice.getDirectionDisplay()).func_240702_b_(": ");

               for (int i = 0; i < modifiers.size(); i++) {
                  VoteModifier modifier = modifiers.get(i);
                  if (i != 0) {
                     txt.func_240702_b_(", ");
                  }

                  txt.func_230529_a_(modifier.getDescription());
               }

               vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(txt, Util.field_240973_b_)));
               modifiers.forEach(modifier -> modifier.onApply(this, vault, world));
               List<VaultPiece> generatedPieces = this.expandVault(vault, world, room, session, choice.getDirection(), roomPiece, null);
               List<VaultPieceProcessor> postProcessors = modifiers.stream()
                  .map(modifier -> modifier.getPostProcessor(this, vault))
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
               generatedPieces.forEach(piece -> postProcessors.forEach(processor -> processor.postProcess(vault, world, piece, choice.getDirection())));
               STitlePacket titlePacket = new STitlePacket(Type.TITLE, choice.getDirectionDisplay());
               vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.field_71135_a.func_147359_a(titlePacket)));
            }
         );
   }

   @Override
   protected List<VaultPiece> expandVault(
      VaultRaid vault,
      ServerWorld world,
      VaultRoom room,
      VotingSession session,
      Direction direction,
      @Nullable JigsawPiece roomToGenerate,
      @Nullable JigsawPiece tunnelToGenerate
   ) {
      JigsawPiece roomPiece = this.roomPool == null ? null : VaultJigsawHelper.getRandomPiece(this.roomPool);
      JigsawPiece tunnelPiece = this.tunnelPool == null ? null : VaultJigsawHelper.getRandomPiece(this.tunnelPool);
      boolean generateObelisk = false;
      if (this.knowledge >= this.totalKnowledgeNeeded) {
         generateObelisk = true;
         this.knowledge = 0;
         EffectMessage msg = EffectMessage.playSound(SoundEvents.field_187802_ec, SoundCategory.NEUTRAL, 0.8F, 0.4F);
         vault.getPlayers()
            .forEach(
               vPlayer -> vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sPlayer), msg))
            );
      }

      return VaultJigsawHelper.expandTenosFinalVault(vault, world, room, direction, roomPiece, tunnelPiece, generateObelisk);
   }

   public void setBoss(LivingEntity boss) {
      this.currentBossId = boss.func_110124_au();
   }

   public void setRoomPool(ResourceLocation roomPool) {
      this.roomPool = roomPool;
   }

   public void setTunnelPool(ResourceLocation tunnelPool) {
      this.tunnelPool = tunnelPool;
   }

   public void addKnowledge(int knowledge) {
      this.knowledge = Math.max(0, this.knowledge + knowledge);
   }

   public void addMobHealthMultiplier(float combinedMobHealthMultiplier) {
      this.combinedMobHealthMultiplier = Math.max(0.0F, this.combinedMobHealthMultiplier + combinedMobHealthMultiplier);
   }

   public float getCombinedMobHealthMultiplier() {
      return this.combinedMobHealthMultiplier;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onBossDeath(LivingDeathEvent event) {
      if (!event.getEntity().field_70170_p.func_201670_d()) {
         ServerWorld world = (ServerWorld)event.getEntity().field_70170_p;
         VaultRaid vault = VaultRaidData.get(world).getAt(world, event.getEntity().func_233580_cy_());
         if (VaultUtils.inVault(vault, event.getEntity())) {
            List<ArchitectSummonAndKillBossesObjective> matchingObjectives = vault.getPlayers()
               .stream()
               .map(player -> player.getActiveObjective(ArchitectSummonAndKillBossesObjective.class))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(o -> !o.isCompleted())
               .filter(o -> o.currentBossId != null)
               .filter(o -> o.currentBossId.equals(event.getEntity().func_110124_au()))
               .collect(Collectors.toList());
            if (matchingObjectives.isEmpty()) {
               vault.getActiveObjective(ArchitectSummonAndKillBossesObjective.class).ifPresent(objective -> objective.onBossDeath(event, vault, world));
            } else {
               matchingObjectives.forEach(objective -> objective.onBossDeath(event, vault, world));
            }
         }
      }
   }

   protected void onBossDeath(LivingDeathEvent event, VaultRaid vault, ServerWorld world) {
      LivingEntity boss = event.getEntityLiving();
      if (boss.func_110124_au().equals(this.currentBossId)) {
         Optional<UUID> source = Optional.ofNullable(event.getSource().func_76346_g()).map(Entity::func_110124_au);
         Optional<VaultPlayer> killer = source.flatMap(vault::getPlayer);
         killer.ifPresent(
            kPlayer -> kPlayer.runIfPresent(
               world.func_73046_m(),
               playerEntity -> vault.getPlayers()
                  .forEach(
                     vPlayer -> vPlayer.runIfPresent(
                        world.func_73046_m(), recipient -> recipient.func_145747_a(this.getBossKillMessage(playerEntity), Util.field_240973_b_)
                     )
                  )
            )
         );
         this.currentBossId = null;
         this.killedBosses++;
         if (!this.hasFulfilledObjective()) {
            this.addModifier(vault, world);
         }
      }
   }

   private void addModifier(VaultRaid vault, ServerWorld world) {
      int level = vault.getProperties().getValue(VaultRaid.LEVEL);
      Set<VaultModifier> modifiers = ModConfigs.VAULT_MODIFIERS.getRandom(rand, level, VaultModifiersConfig.ModifierPoolType.FINAL_TENOS_ADDS, this.getId());
      List<VaultModifier> modifierList = new ArrayList<>(modifiers);
      Collections.shuffle(modifierList);
      VaultModifier modifier = MiscUtils.getRandomEntry(modifierList, rand);
      if (modifier != null) {
         ITextComponent ct = new StringTextComponent("Added ").func_240699_a_(TextFormatting.GRAY).func_230529_a_(modifier.getNameComponent());
         vault.getModifiers().addPermanentModifier(modifier);
         vault.getPlayers().forEach(vPlayer -> {
            modifier.apply(vault, vPlayer, world, world.func_201674_k());
            vPlayer.runIfPresent(world.func_73046_m(), sPlayer -> sPlayer.func_145747_a(ct, Util.field_240973_b_));
         });
      }
   }

   private ITextComponent getBossKillMessage(PlayerEntity player) {
      IFormattableTextComponent msgContainer = new StringTextComponent("").func_240699_a_(TextFormatting.WHITE);
      IFormattableTextComponent playerName = player.func_145748_c_().func_230532_e_();
      playerName.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(9974168)));
      return msgContainer.func_230529_a_(playerName).func_240702_b_(" defeated a Boss!");
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74768_a("killedBosses", this.killedBosses);
      nbt.func_74768_a("totalKilledBossesNeeded", this.totalKilledBossesNeeded);
      nbt.func_74768_a("knowledge", this.knowledge);
      nbt.func_74768_a("totalKnowledgeNeeded", this.totalKnowledgeNeeded);
      nbt.func_74776_a("combinedMobHealthMultiplier", this.combinedMobHealthMultiplier);
      if (this.currentBossId != null) {
         nbt.func_186854_a("currentBossId", this.currentBossId);
      }

      nbt.func_74778_a("roomPool", this.roomPool.toString());
      nbt.func_74778_a("tunnelPool", this.tunnelPool.toString());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.killedBosses = nbt.func_74762_e("killedBosses");
      this.totalKilledBossesNeeded = nbt.func_74762_e("totalKilledBossesNeeded");
      this.knowledge = nbt.func_74762_e("knowledge");
      this.totalKnowledgeNeeded = nbt.func_74762_e("totalKnowledgeNeeded");
      this.combinedMobHealthMultiplier = nbt.func_74760_g("combinedMobHealthMultiplier");
      if (nbt.func_186855_b("currentBossId")) {
         this.currentBossId = nbt.func_186857_a("currentBossId");
      }

      if (nbt.func_150297_b("roomPool", 8)) {
         this.roomPool = new ResourceLocation(nbt.func_74779_i("roomPool"));
      }

      if (nbt.func_150297_b("tunnelPool", 8)) {
         this.tunnelPool = new ResourceLocation(nbt.func_74779_i("tunnelPool"));
      }
   }
}

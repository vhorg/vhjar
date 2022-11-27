package iskallia.vault.world.legacy.raid;

import iskallia.vault.entity.entity.ArenaTrackerEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.StreamData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.common.util.INBTSerializable;

public class ArenaSpawner implements INBTSerializable<CompoundTag> {
   private static final String[] DEV_NICKS = new String[]{
      "KaptainWutax",
      "iGoodie",
      "jmilthedude",
      "Scalda",
      "Kumara22",
      "Goktwo",
      "Aolsen96",
      "Winter_Grave",
      "kimandjax",
      "Monni_21",
      "Starmute",
      "MukiTanuki",
      "RowanArtifex",
      "HellFirePvP",
      "Pau1_",
      "Douwsky",
      "neentarts",
      "Reed22"
   };
   private final ArenaRaid raid;
   public final List<UUID> fighters = new ArrayList<>();
   public final List<UUID> bosses = new ArrayList<>();
   private final int bossCount;
   public List<StreamData.Subscribers.Instance> subscribers = new ArrayList<>();
   private boolean started;

   public ArenaSpawner(ArenaRaid raid, int bossCount) {
      this.raid = raid;
      this.bossCount = bossCount;
   }

   public boolean hasStarted() {
      return this.started;
   }

   public void addFighters(List<StreamData.Subscribers.Instance> fighterNames) {
      this.subscribers = new ArrayList<>(fighterNames);
   }

   public void start(ServerLevel world) {
      this.fighters.clear();
      this.bosses.clear();
      int maxMonths = 0;
      float fightersHealth = 0.0F;

      for (int i = 0; i < this.subscribers.size(); i++) {
         double radius = 40.0;
         double a = (double)i / this.subscribers.size() * 2.0 * Math.PI;
         double s = Math.sin(a);
         double c = Math.cos(a);
         double r = Math.sqrt(radius * radius / (s * s + c * c));
         BlockPos var15 = this.raid.getCenter().offset(s * r, 256 - this.raid.getCenter().getY(), c * r);
      }

      int i = 0;

      while (i < this.bossCount) {
         i++;
      }

      ArenaTrackerEntity tracker = (ArenaTrackerEntity)ModEntities.ARENA_TRACKER.create(world);
      tracker.getAttribute(Attributes.MAX_HEALTH).setBaseValue(fightersHealth);
      tracker.moveTo(this.raid.getCenter().getX(), 128.0, this.raid.getCenter().getZ(), 0.0F, 0.0F);
      tracker.setCustomName(new TextComponent("Subscribers"));
      world.addFreshEntity(tracker);
      this.started = true;
   }

   public int getFighterCount() {
      return this.subscribers.size();
   }

   public BlockPos toTop(ServerLevel world, BlockPos pos) {
      return pos.above(world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, true).getHeight(Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) + 1);
   }

   public static String getRandomDevName(Random rand) {
      return DEV_NICKS[rand.nextInt(DEV_NICKS.length)];
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag bossList = new ListTag();
      ListTag fighterList = new ListTag();
      ListTag subscriberList = new ListTag();
      this.bosses.forEach(uuid -> bossList.add(StringTag.valueOf(uuid.toString())));
      this.fighters.forEach(uuid -> fighterList.add(StringTag.valueOf(uuid.toString())));
      this.subscribers.forEach(sub -> subscriberList.add(sub.serializeNBT()));
      nbt.put("BossList", bossList);
      nbt.put("FighterList", fighterList);
      nbt.put("SubscriberList", subscriberList);
      nbt.putBoolean("Started", this.started);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.bosses.clear();
      this.fighters.clear();
      ListTag bossList = nbt.getList("BossList", 8);
      ListTag fighterList = nbt.getList("FighterList", 8);
      ListTag subscriberList = nbt.getList("SubscriberList", 10);
      IntStream.range(0, bossList.size()).mapToObj(i -> UUID.fromString(bossList.getString(i))).forEach(this.bosses::add);
      IntStream.range(0, fighterList.size()).mapToObj(i -> UUID.fromString(fighterList.getString(i))).forEach(this.fighters::add);
      IntStream.range(0, subscriberList.size()).forEach(i -> {
         StreamData.Subscribers.Instance sub = new StreamData.Subscribers.Instance();
         sub.deserializeNBT(subscriberList.getCompound(i));
         this.subscribers.add(sub);
      });
      this.started = nbt.getBoolean("Started");
   }
}

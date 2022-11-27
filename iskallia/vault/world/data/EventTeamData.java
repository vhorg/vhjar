package iskallia.vault.world.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class EventTeamData extends SavedData {
   protected static final String DATA_NAME = "the_vault_EventTeams";
   private final List<EventTeamData.Team> teams = new ArrayList<>();

   private void initialize() {
      EventTeamData.Team team1 = new EventTeamData.Team("Team Green", ChatFormatting.GREEN);
      team1.addMember("PeteZahHutt");
      team1.addMember("Stressmonster101");
      team1.addMember("ItsFundy");
      team1.addMember("5uppps");
      this.teams.add(team1);
      EventTeamData.Team team2 = new EventTeamData.Team("Team Yellow", ChatFormatting.GOLD);
      team2.addMember("Iskall85");
      team2.addMember("CaptainPuffy");
      team2.addMember("X33N");
      team2.addMember("CaptainSparklez");
      this.teams.add(team2);
      EventTeamData.Team team3 = new EventTeamData.Team("Team Aqua", ChatFormatting.AQUA);
      team3.addMember("HBomb94");
      team3.addMember("AntonioAsh");
      team3.addMember("falsesymmetry");
      team3.addMember("Tubbo_");
      this.teams.add(team3);
      this.setDirty();
   }

   public void modifyScore(String name, int modify) {
      EventTeamData.Team team = this.getTeam(name);
      if (team != null) {
         team.setScore(team.getScore() + modify);
         this.setDirty();
      }
   }

   public List<EventTeamData.Team> getTeams() {
      if (this.teams.isEmpty()) {
         this.initialize();
      }

      return this.teams;
   }

   @Nullable
   public EventTeamData.Team getTeam(String name) {
      return this.getTeams().stream().filter(team -> team.getName().equals(name)).findFirst().orElse(null);
   }

   private static EventTeamData create(CompoundTag tag) {
      EventTeamData data = new EventTeamData();
      data.load(tag);
      return data;
   }

   public void load(CompoundTag tag) {
      this.teams.clear();
      ListTag teams = tag.getList("teams", 10);

      for (int i = 0; i < teams.size(); i++) {
         this.teams.add(new EventTeamData.Team(teams.getCompound(i)));
      }
   }

   public CompoundTag save(CompoundTag tag) {
      ListTag teams = new ListTag();
      this.teams.forEach(team -> teams.add(team.serialize()));
      tag.put("teams", teams);
      return tag;
   }

   public static EventTeamData get(ServerLevel world) {
      return (EventTeamData)world.getServer().overworld().getDataStorage().computeIfAbsent(EventTeamData::create, EventTeamData::new, "the_vault_EventTeams");
   }

   public static class Team {
      private final String name;
      private final ChatFormatting color;
      private final List<String> members = new ArrayList<>();
      private int score = 0;

      public Team(String name, ChatFormatting color) {
         this.name = name;
         this.color = color;
      }

      public Team(CompoundTag tag) {
         this.name = tag.getString("name");
         this.color = ChatFormatting.values()[tag.getInt("color")];
         this.score = tag.getInt("score");
         ListTag players = tag.getList("members", 8);

         for (int i = 0; i < players.size(); i++) {
            this.members.add(players.getString(i));
         }
      }

      public String getName() {
         return this.name;
      }

      public ChatFormatting getColor() {
         return this.color;
      }

      public int getScore() {
         return this.score;
      }

      public void setScore(int score) {
         this.score = score;
      }

      public List<String> getMembers() {
         return this.members;
      }

      public boolean addMember(String member) {
         return this.members.contains(member) ? false : this.members.add(member);
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putString("name", this.name);
         tag.putInt("color", this.color.ordinal());
         tag.putInt("score", this.score);
         ListTag players = new ListTag();
         this.members.forEach(member -> players.add(StringTag.valueOf(member)));
         tag.put("members", players);
         return tag;
      }
   }
}

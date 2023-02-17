package com.gmail.chickenpowerrr.ranksync.discord.player;

import com.gmail.chickenpowerrr.ranksync.api.bot.Bot;
import com.gmail.chickenpowerrr.ranksync.api.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

/**
 * This class contains the functionalities to get the representation of a synchronized player
 *
 * @author Chickenpowerrr
 * @since 1.0.0
 */
public class PlayerFactory implements
    com.gmail.chickenpowerrr.ranksync.api.player.PlayerFactory<Member> {

  private static final Map<Guild, PlayerFactory> instances = new HashMap<>();

  private final Map<UUID, Player> players = new HashMap<>();
  private final Guild guild;

  @Getter
  private final Bot<Member, ?> bot;

  /**
   * @param bot the Bot that is running
   * @param guild the Guild the Bot is running on
   */
  private PlayerFactory(Bot<Member, ?> bot, Guild guild) {
    this.bot = bot;
    this.guild = guild;
  }

  /**
   * Lazily returns an instance based on the Guild the Bot is running on
   *
   * @param bot the Bot that is running
   * @param guild the Guild the Bot is running on
   * @return an instance based on the Guild the Bot is running on
   */
  public static PlayerFactory getInstance(Bot<Member, ?> bot, Guild guild) {
    if (!instances.containsKey(guild)) {
      instances.put(guild, new PlayerFactory(bot, guild));
    }
    return instances.get(guild);
  }

  /**
   * Lazily returns an instance based on the Guild the Bot is running on
   *
   * @param guild the Guild the Bot is running on
   * @return an instance based on the Guild the Bot is running on
   */
  static PlayerFactory getInstance(Guild guild) {
    return getInstance(null, guild);
  }

  /**
   * Updates the link for a given Discord user
   *
   * @param playerId the id that represents the Discord user
   * @param uuid the uuid that represents the player on the other service
   * @return a CompletableFuture that will be completed whenever the process is done
   */
  @Override
  public CompletableFuture<Void> setUuid(String playerId, UUID uuid) {
    CompletableFuture<Void> result = new CompletableFuture<>();

    this.guild.retrieveMemberById(playerId).submit().thenAccept(member -> {
      getPlayer(member).thenAccept(player -> {
        if (uuid == null) {
          this.players.remove(player.getUuid());
          player.setUuid(null);
        } else {
          this.players.remove(player.getUuid());
          this.players.put(uuid, player);
          player.setUuid(uuid);
        }
        result.complete(null);
      }).exceptionally(throwable -> {
        throwable.printStackTrace();
        result.completeExceptionally(throwable);
        return null;
      });
    }).exceptionally(throwable -> {
      throwable.printStackTrace();
      result.completeExceptionally(throwable);
      return null;
    });

    return result;
  }

  /**
   * Turns a JDA member instance into a player that is supported by the RankSync API
   *
   * @param member the member object used by JDA, not the RankSync API
   * @return the RankSync representation of the player
   */
  @Override
  public CompletableFuture<Player> getPlayer(Member member) {
    return this.bot.getEffectiveDatabase().getPlayer(member.getId(),
        (uuid, identifier, timesSynced, timesUnsynced) -> {
          Player player;
          if (this.players.containsKey(uuid)) {
            player = this.players.get(uuid);
          } else {
            player = new com.gmail.chickenpowerrr.ranksync.discord.player.Player(uuid, member,
                this.bot, timesSynced, timesUnsynced);
            if (uuid != null) {
              this.players.put(uuid, player);
            }
          }
          return player;
        }).exceptionally(throwable -> {
          throwable.printStackTrace();
          return null;
    });
  }

  /**
   * Returns a Player object based on its UUID that came from the other service
   *
   * @param uuid the UUID that represents this player on another service
   * @return a CompletableFuture that will be completed whenever the player has been found
   */
  @Override
  public CompletableFuture<Player> getPlayer(UUID uuid) {
    return this.bot.getEffectiveDatabase()
        .getPlayer(uuid, (uuid1, identifier, timesSynced, timesUnsynced) -> {
          if (identifier != null) {
            Member member = this.guild.retrieveMemberById(identifier)
                .onErrorMap(throwable -> null).complete();
            if (member != null) {
              return new com.gmail.chickenpowerrr.ranksync.discord.player.Player(uuid,
                  member, this.bot, timesSynced, timesUnsynced);
            } else {
              return null;
            }
          } else {
            return null;
          }
        });
  }
}

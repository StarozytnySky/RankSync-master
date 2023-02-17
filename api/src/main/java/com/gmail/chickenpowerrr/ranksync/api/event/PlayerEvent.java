package com.gmail.chickenpowerrr.ranksync.api.event;

import com.gmail.chickenpowerrr.ranksync.api.player.Player;

/**
 * This interface will be implemented by events that are invoked because the Player is doing things
 *
 * @author Chickenpowerrr
 * @since 1.0.0
 */
public interface PlayerEvent extends Event {

  /**
   * Returns the player that did something
   */
  Player getPlayer();
}

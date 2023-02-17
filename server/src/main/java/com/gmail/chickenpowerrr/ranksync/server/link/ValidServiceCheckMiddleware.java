package com.gmail.chickenpowerrr.ranksync.server.link;

import com.gmail.chickenpowerrr.ranksync.api.player.LinkInfo;
import com.gmail.chickenpowerrr.ranksync.server.language.Translation;
import com.gmail.chickenpowerrr.ranksync.api.user.User;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class checks if the service used to link is valid
 *
 * @author Chickenpowerrr
 * @since 1.0.0
 */
public class ValidServiceCheckMiddleware extends AbstractMiddleware {

  private final String services;

  /**
   * @param linkHelper the helper to create a complete check
   */
  ValidServiceCheckMiddleware(LinkHelper linkHelper) {
    super(linkHelper);
    this.services = super.linkHelper.getLinkInfos().stream().sorted().map(LinkInfo::getName)
        .collect(Collectors.joining("/", "<", ">"));
  }

  /**
   * Returns if the used services is valid
   *
   * @param user the player that invokes the middleware
   * @param uuid the UUID of the player that invokes the middleware
   * @param service the service the middleware has been invoked from
   * @param key the key used to link the player
   * @return if the used services is valid
   */
  @Override
  protected boolean check(User user, UUID uuid, String service, String key) {
    if (super.linkHelper.getLinkInfo(service) == null) {
      user.sendMessage(
          Translation.RANKSYNC_COMMAND_USAGE.getTranslation("services", this.services));
      return false;
    }
    return true;
  }
}

package org.jdk.workmate.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {
  public static long id() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof LoginUser lu)) {
      throw new IllegalStateException("No authenticated user");
    }
    return lu.id();
  }

  public static String userName() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof LoginUser lu)) {
      throw new IllegalStateException("No authenticated user");
    }
    return lu.userName();
  }
}


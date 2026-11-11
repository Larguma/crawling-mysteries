package dev.larguma.crawlingmysteries.client;

/**
 * Implemented by block entities (and entities) that can display googly eyes,
 * allowing {@link dev.larguma.crawlingmysteries.client.item.GooglyEyesLayer} to check their state.
 */
public interface GooglyEyesHolder {
  boolean hasGooglyEyes();
}

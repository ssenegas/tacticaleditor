package org.senegas.tacticeditor.utils;

public final class PitchConstants {

  // All original game coordinates are based upon the top left corner flag being
  // 0,0 and the bottom right flag

  public static final int MAP_WIDTH_IN_TILE = 80;
  public static final int MAP_HEIGHT_IN_TILE = 96;
  public static final int TILE_WIDTH_IN_PIXEL = 16;
  public static final int TILE_HEIGHT_IN_PIXEL = 16;

  public static final int MAP_WIDTH_IN_PIXEL = MAP_WIDTH_IN_TILE * TILE_WIDTH_IN_PIXEL; // 1280
  public static final int MAP_HEIGHT_IN_PIXEL = MAP_HEIGHT_IN_TILE * TILE_HEIGHT_IN_PIXEL; // 1536

  public static final int ONE_YARD_IN_PIXEL = 12;
  public static final int PITCH_WIDTH_IN_YARD = 76;
  public static final int PITCH_HEIGHT_IN_YARD = 116;

  // size of the pitch, starting from middle of the side lines
  public static final int PITCH_WIDTH_IN_PIXEL = PITCH_WIDTH_IN_YARD * ONE_YARD_IN_PIXEL; // 912
  public static final int PITCH_HEIGHT_IN_PIXEL = PITCH_HEIGHT_IN_YARD * ONE_YARD_IN_PIXEL; // 1392

  // technically, the offset origin of the pitch
  public static final int OUTER_TOP_EDGE_X = 176;
  public static final int OUTER_TOP_EDGE_Y = 64;

  public static final int OUTER_BOTTOM_EDGE_X = 176;
  public static final int OUTER_BOTTOM_EDGE_Y = 80;
  
  private PitchConstants() {
		// restrict instantiation
  }

}

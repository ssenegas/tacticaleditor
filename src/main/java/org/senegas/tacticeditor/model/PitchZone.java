package org.senegas.tacticeditor.model;

import java.util.stream.Stream;

public enum PitchZone {
  AREA1(0, "area1"), AREA2(1, "area2"), AREA3(2, "area3"), AREA4(3, "area4"),
  AREA5(4, "area5"), AREA6(5, "area6"), AREA7(6, "area7"), AREA8(7, "area8"),
  AREA9(8, "area9"), AREA10(9, "area10"), AREA11(10, "area11"), AREA12(11, "area12"),
  KICKOFF_OWN(12, "kickoff_own"), KICKOFF_DEF(13, "kickoff_def"),
  GOALKICK_DEF(14, "goalkick_def"), GOALKICK_OWN(15, "goalkick_own"),
  CORNER1(16, "corner 1"), CORNER2(17, "corner 2"),
  CORNER3(18, "corner 3"), CORNER4(19, "corner 4");

  private final int index;
  private final String name;

  PitchZone(int index, String name) {
	this.index = index;
	this.name = name;
  }

  public int getIndex() {
	return this.index;
  }

  public String getName() {
	return this.name;
  }

  public static PitchZone of(int index) {
	return Stream.of(PitchZone.values())
			.filter(pz -> pz.getIndex() == index)
			.findFirst().orElseThrow();
  }
}

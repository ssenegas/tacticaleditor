package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TestPlayer {
  private String name;
  private List<Point> positions = new ArrayList<>();

  public TestPlayer() {
  }

  public TestPlayer(String name) {
	this(name, new ArrayList<>());
  }

  public TestPlayer(String name, List<Point> positions) {
	this.name = name;
	this.positions = positions;
  }

  public String getName() {
	return this.name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public List<Point> getPositions() {
	return Collections.unmodifiableList(this.positions);
  }

  public boolean addPosition(Point p) {
	return this.positions.add(p);
  }

  @Override
  public String toString() {
	return "Player [name=" + this.name + ", positions=" + this.positions + "]";
  }

  @Override
  public int hashCode() {
	return Objects.hash(this.name, this.positions);
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj) {
	  return true;
	}
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final TestPlayer other = (TestPlayer) obj;
	return Objects.equals(this.name, other.name) && Objects.equals(this.positions, other.positions);
  }
}

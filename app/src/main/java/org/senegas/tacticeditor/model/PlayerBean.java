package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlayerBean {

	private String name;
	private List<Point> positions = new ArrayList<>();
	
	public PlayerBean() {
	}
	
	public PlayerBean(String name) {
		this(name, new ArrayList<>());
	}
	
	public PlayerBean(String name, List<Point> positions) {
		this.name = name;
		this.positions = positions;
	}
	
	public String getName() {
		return name;
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
		return "Player [name=" + name + ", positions=" + positions + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, positions);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerBean other = (PlayerBean) obj;
		return Objects.equals(name, other.name) && Objects.equals(positions, other.positions);
	}
}

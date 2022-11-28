package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tactic implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6255623013679331171L;

	private Map<Integer, List<Point>> positions;

	/**
	 * @param positions
	 */
	public Tactic() {
		this(new HashMap<>());
	}

	/**
	 * @param positions
	 */
	public Tactic(Map<Integer, List<Point>> positions) {
		this.positions = positions;
	}

	public Map<Integer, List<Point>> getPositions() {
		return this.positions;
	}

	public void setPositions(Map<Integer, List<Point>> positions) {
		this.positions = positions;
	}

	@Override
	public String toString() {
		return "Tactic [positions=" + this.positions + "]";
	}

}

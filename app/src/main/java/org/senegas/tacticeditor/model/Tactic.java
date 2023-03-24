package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class Tactic implements Serializable {
	
	public static final int NUMBER_OF_PLAYERS = 10;

	/**
	 *
	 */
	private static final long serialVersionUID = -6255623013679331171L;

	private final Map<PitchZone, Map<Integer, Point>> teamPositionPerZone;

	/**
	 * @param positions
	 */
	public Tactic() {
		this(new EnumMap<>(PitchZone.class));
	}

	/**
	 * @param positions
	 */
	public Tactic(Map<PitchZone, Map<Integer, Point>> positions) {
		this.teamPositionPerZone = positions;
	}

	public Map<PitchZone, Map<Integer, Point>> getPositions() {
		return this.teamPositionPerZone;
	}
	
	public Map<Integer, Point> getPositionsFor(PitchZone z) {
		return this.teamPositionPerZone.get(z);
	}

	@Override
	public String toString() {
		return "Tactic [positions=" + this.teamPositionPerZone + "]";
	}
}

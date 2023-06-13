package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Tactic implements Serializable {
	
	public static final int NUMBER_OF_PLAYERS = 10;
	public static final List<Integer> SHIRTS = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

	/**
	 *
	 */
	private static final long serialVersionUID = -6255623013679331171L;

	private final Map<PitchZone, Map<Integer, Point>> teamPositionPerZone;

	/**
	 * Constructor
	 */
	public Tactic() {
		this(new EnumMap<>(PitchZone.class));
	}

	/**
	 * Constructor
	 * @param positions
	 */
	public Tactic(Map<PitchZone, Map<Integer, Point>> positions) {
		this.teamPositionPerZone = positions;
	}

	public Map<PitchZone, Map<Integer, Point>> getPositions() {
		return this.teamPositionPerZone;
	}
	
	public Map<Integer, Point> getPositionsFor(PitchZone zone) {
		return this.teamPositionPerZone.get(zone);
	}
	
	public Point getPositionFor(PitchZone zone, Integer shirt) {
		if (! SHIRTS.contains(shirt)) {
			throw new InvalidParameterException();
		}
		return this.teamPositionPerZone.get(zone).get(shirt);
	}

	@Override
	public String toString() {
		return "Tactic [positions=" + this.teamPositionPerZone + "]";
	}
}

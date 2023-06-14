package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Tactic implements Serializable {
	
	public static final int NUMBER_OF_PLAYERS = 10;
	public static final List<Integer> SHIRTS = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

	/**
	 *
	 */
	private static final long serialVersionUID = -6255623013679331171L;

	private final Map<PitchZone, Map<Integer, Point>> positions;

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
		this.positions = positions;
	}

	public Map<PitchZone, Map<Integer, Point>> getPositions() {
		return this.positions;
	}
	
	public Map<Integer, Point> getPositionsFor(PitchZone zone) {
		return Optional.ofNullable(this.positions.get(zone))
				.orElseThrow(() -> new NoSuchElementException("Zone not found"));
	}
	
	public Point getPositionFor(PitchZone zone, Integer shirt) {
		if (! SHIRTS.contains(shirt)) {
			throw new InvalidParameterException();
		}
		return Optional.ofNullable(getPositionsFor(zone).get(shirt))
				.orElseThrow(() -> new NoSuchElementException("Position not found"));
	}

	@Override
	public String toString() {
		return "Tactic [positions=" + this.positions + "]";
	}
}

package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.Map;

public class TacticModel extends AbstractModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8048437731715191967L;

	private Tactic tactic;
	private Integer previousZoneSelection;
	private Integer selectedZone;

	public TacticModel() {
		super();
		this.previousZoneSelection = -1;
		this.selectedZone = -1;
	}

	public Tactic getTatic() {
		return this.tactic;
	}

	public void setTatic(Tactic tactic) {
		Tactic oldTactic = this.tactic;
		this.tactic = tactic;
		
		firePropertyChange("tactic", oldTactic, tactic);
	}

	public Integer getSelectedZone() {
		return this.selectedZone;
	}

	public void setSelectedZone(Integer zone) {
		Integer oldSelectedZone = this.selectedZone;
		this.previousZoneSelection = this.selectedZone;
		this.selectedZone = zone;
		
		firePropertyChange("zone", oldSelectedZone, zone);
		
		Map<Integer, Point> positions = this.tactic.getPositionsFor(PitchZone.of(this.selectedZone));
		
		System.out.println("Zone: " + PitchZone.of(this.selectedZone) + " [positions=" + positions + "]");
	}

	public Integer getPreviousZoneSelection() {
		return this.previousZoneSelection;
	}

	public void resetPreviousZoneSelection() {
		this.previousZoneSelection = -1;
	}
}

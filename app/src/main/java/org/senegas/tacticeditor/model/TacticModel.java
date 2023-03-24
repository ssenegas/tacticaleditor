package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Map;

public class TacticModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8048437731715191967L;

	private Tactic tatic;
	private Integer previousZoneSelection;
	private Integer selectedZone;
	private final PropertyChangeSupport support;

	public TacticModel() {
		this.previousZoneSelection = -1;
		this.selectedZone = -1;
		this.support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	public Tactic getTatic() {
		return this.tatic;
	}

	public void setTatic(Tactic value) {
		this.support.firePropertyChange("tactic", this.tatic, value);
		this.tatic = value;
	}

	public Integer getSelectedZone() {
		return this.selectedZone;
	}

	public void setSelectedZone(Integer value) {
		this.support.firePropertyChange("zone", this.selectedZone, value);
		this.previousZoneSelection = this.selectedZone;
		this.selectedZone = value;
		
		Map<Integer, Point> positions = this.tatic.getPositionsFor(PitchZone.getPitchZoneByIndex(this.selectedZone));
		
		System.out.println("Zone: " + this.selectedZone + " [positions=" + positions + "]");
	}

	public Integer getPreviousZoneSelection() {
		return this.previousZoneSelection;
	}

	public void resetPreviousZoneSelection() {
		this.previousZoneSelection = -1;
	}
}

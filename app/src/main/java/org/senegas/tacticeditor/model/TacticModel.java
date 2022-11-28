package org.senegas.tacticeditor.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TacticModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8048437731715191967L;

	private Tactic tatic;
	private Integer previousSector;
	private Integer selectedSector;
	private final PropertyChangeSupport support;

	public TacticModel() {
		this.previousSector = -1;
		this.selectedSector = -1;
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

	public Integer getSelectedSector() {
		return this.selectedSector;
	}

	public void setSelectedSector(Integer value) {
		this.support.firePropertyChange("sector", this.selectedSector, value);
		this.previousSector = this.selectedSector;
		this.selectedSector = value;

		final List<Point> result = new ArrayList<>();
		for (final Integer key : this.tatic.getPositions().keySet()) {
			final List<Point> points = this.tatic.getPositions().get(key);
			result.add(points.get(this.selectedSector));
		}

		System.out.println("Region: " + this.selectedSector + " [sector=" + result + "]");
	}

	public Integer getPreviousSector() {
		return this.previousSector;
	}

	public void resetPreviousSector() {
		this.previousSector = -1;
	}
}

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
  private Integer previousSelectedZone;
  private Integer selectedZone;

  public TacticModel() {
	super();
	this.previousSelectedZone = -1;
	this.selectedZone = -1;
  }

  public Tactic getTatic() {
	return this.tactic;
  }

  public void setTatic(Tactic tactic) {
	final Tactic oldTactic = this.tactic;
	this.tactic = tactic;

	firePropertyChange("tactic", oldTactic, tactic);
  }

  /**
   * @return the selected zone index or -1 if not set
   */
  public Integer getSelectedZone() {
	return this.selectedZone;
  }

  public void setSelectedZone(Integer zone) {
	this.previousSelectedZone = this.selectedZone;
	this.selectedZone = zone;

	firePropertyChange("zone", this.previousSelectedZone, this.selectedZone);

	final Map<Integer, Point> positions = this.tactic.getPositions(PitchZone.of(this.selectedZone));

	System.out.println("Zone: " + PitchZone.of(this.selectedZone) + " [positions=" + positions + "]");
  }

  /**
   * @return the previous selected zone index or -1 if not set
   */
  public Integer getPreviousSelectedZone() {
	return this.previousSelectedZone;
  }

  public void resetPreviousSelectedZone() {
	this.previousSelectedZone = -1;
  }
}

package org.senegas.tacticeditor.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.senegas.tacticeditor.model.TacticModel;

public class MainPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  public MainPanel() {
	super(new BorderLayout());

	final TacticModel model = new TacticModel();
	final TacticView view = new TacticView(model);
	final TacticController controller = new TacticController(model, view);

	model.addPropertyChangeListener(view);
	// register controller as the listener to components in the view
	view.registerListener(controller);

	this.add(view, BorderLayout.CENTER);
	this.add(controller, BorderLayout.EAST);
  }
}

package org.senegas.tacticeditor.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.senegas.tacticeditor.model.TacticModel;

public class TacticEditorPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  public TacticEditorPanel() {
	super(new BorderLayout());

	final TacticModel model = new TacticModel();
	final TacticEditorView view = new TacticEditorView(model);
	final TacticEditorController controller = new TacticEditorController(model, view);

	model.addPropertyChangeListener(view);
	// register controller as the listener to components in the view
	view.registerListener(controller);

	this.add(view, BorderLayout.CENTER);
	this.add(controller, BorderLayout.EAST);
  }
}

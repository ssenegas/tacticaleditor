package org.senegas.tacticeditor.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.senegas.tacticeditor.model.TacticModel;

public class MainPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MainPanel() {
        super(new BorderLayout());

        final TacticModel model = new TacticModel();
        final TaticView view = new TaticView(model);
        final TacticController  controller = new TacticController(model, view);

        model.addPropertyChangeListener(view);

        this.add(view, BorderLayout.CENTER);
        this.add(controller, BorderLayout.EAST);
    }

}

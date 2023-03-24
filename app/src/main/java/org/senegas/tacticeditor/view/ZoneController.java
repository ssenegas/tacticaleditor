package org.senegas.tacticeditor.view;

import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.senegas.tacticeditor.model.TacticModel;

public class ZoneController extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final TacticModel model;
	EnumMap<ButtonModel, JToggleButton> buttons = new EnumMap<> (ButtonModel.class);

	private enum ButtonModel {
		AREA1("1", 8, false),
		AREA2("2", 9, false),
		AREA3("3", 10, false),
		AREA4("4", 11, false),
		AREA5("5", 4, false),
		AREA6("6", 5, false),
		AREA7("7", 6, false),
		AREA8("8", 7, false),
		AREA9("9", 0, false),
		AREA10("10", 1, false),
		AREA11("11", 2, false),
		AREA12("12", 3, false),
		KICKOFF_OWN("Kickoff own", 12, true),
		KICKOFF_DEF("Kickoff def", 13, true),
		GOALKICK_OWN("Goalkick own", 15, true),
		GOALKICK_DEF("Goalkick def", 14, true),
		CORNER1("Corner1", 18, true),
		CORNER2("Corner2", 19, true),
		CORNER3("Corner3", 16, true),
		CORNER4("Corner4", 17, true);

		private final String text;
		private final Integer zone;
		private final boolean isResumeGame;

		private ButtonModel(String text, Integer zone, boolean b) {
			this.text = text;
			this.zone = zone;
			this.isResumeGame = b;
		}

		public String getText() {
		    return this.text;
		}

		public Integer getZone() {
		    return this.zone;
		}

		public boolean isResumeGame() {
		    return this.isResumeGame;
		}

		public static Stream<ButtonModel> stream() {
	        return Stream.of(ButtonModel.values());
	    }
	}

	public ZoneController(TacticModel model) {
		setLayout(new GridLayout(3,4));

		this.model = model;

		final ButtonGroup group = new ButtonGroup();
		ButtonModel.stream()
			.forEach(bm -> {
				final JToggleButton tb = new JToggleButton(bm.getText());
	        	this.buttons.put(bm, tb);
	        	group.add(tb);
	        	tb.addActionListener(e -> {
	            	this.model.setSelectedZone(bm.getZone());
	            	if (bm.isResumeGame())
	            		this.model.resetPreviousZoneSelection();
	            });
			});

        add(buildZoneSelectorPanel());
        add(buildResumeGamePanel());

        enableDisableButtons();
    }

	/**
	 * @return
	 */
	private JPanel buildResumeGamePanel() {
		final JPanel grid = new JPanel(new GridLayout(4,2));
		ButtonModel.stream()
			.filter(ButtonModel::isResumeGame)
			.forEach(bm -> grid.add(this.buttons.get(bm)));
		return grid;
	}

	/**
	 * @return
	 */
	private JPanel buildZoneSelectorPanel() {
		final JPanel grid = new JPanel(new GridLayout(3,4));
		ButtonModel.stream()
			.filter(Predicate.not(ButtonModel::isResumeGame))
			.forEach(bm -> grid.add(this.buttons.get(bm)));
		return grid;
	}

	public void enableDisableButtons() {
		final boolean b = this.model.getTatic() != null;
		this.buttons.values().stream()
			.forEach(tb -> tb.setEnabled(b));
	}

	public void toggleGoalkickOwn() {
		this.buttons.get(ButtonModel.KICKOFF_OWN).doClick();
	}
}

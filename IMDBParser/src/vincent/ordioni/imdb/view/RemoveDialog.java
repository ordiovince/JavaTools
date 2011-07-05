package vincent.ordioni.imdb.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import vincent.ordioni.imdb.parser.IMDBParser;
import vincent.ordioni.imdb.parser.model.Serie;
import vincent.ordioni.imdb.parser.model.Videotheque;

public class RemoveDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -4781912055934407796L;

    private final JPanel contentPanel = new JPanel();

    private JList list;

    private JLabel nbSeriesLabel;

    private boolean isModified;

    /**
     * Create the dialog.
     */
    public RemoveDialog() {
        isModified = false;
        setAlwaysOnTop(true);
        setResizable(false);
        setTitle("IMDBParser - Remove a Serie");
        setBounds(100, 100, 375, 500);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            JLabel lblSelectASerie = new JLabel("Select a serie to remove and press \"Remove\".");
            lblSelectASerie.setBounds(10, 11, 349, 14);
            contentPanel.add(lblSelectASerie);
        }
        {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(10, 36, 349, 386);
            contentPanel.add(scrollPane);
            {
                list = new JList();
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                scrollPane.setViewportView(list);
            }
        }
        {
            JButton closeButton = new JButton("Close");
            closeButton.setMnemonic('c');
            closeButton.setBounds(259, 433, 100, 23);
            contentPanel.add(closeButton);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            closeButton.setActionCommand("Cancel");
        }
        JButton removeButton = new JButton("Remove");
        removeButton.setMnemonic('r');
        removeButton.setBounds(149, 433, 100, 23);
        contentPanel.add(removeButton);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Serie serie = (Serie) list.getSelectedValue();

                    int choice = JOptionPane.showConfirmDialog(RemoveDialog.this, "Do you really want to remove this serie?", serie.getName(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice == JOptionPane.NO_OPTION) {
                        return;
                    }

                    IMDBParser.removeSerie(serie);

                    initData();

                    isModified = true;
                } catch (Exception e1) {

                }
            }
        });
        removeButton.setActionCommand("OK");
        getRootPane().setDefaultButton(removeButton);
        {
            nbSeriesLabel = new JLabel("0 series");
            nbSeriesLabel.setBounds(10, 437, 100, 14);
            contentPanel.add(nbSeriesLabel);
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                buttonPane.setLayout(null);
            }
        }

        initData();
    }

    public static boolean display() {
        RemoveDialog removeDialog = new RemoveDialog();
        removeDialog.setVisible(true);

        while (removeDialog.isVisible()) {

        }

        return removeDialog.isModified;
    }

    private void initData() {
        Vector<Object> data = new Vector<Object>();
        try {
            Videotheque source = Videotheque.loadFromXml(IMDBParser.SOURCE_PATH, false);

            if (source.size() == 0) {
                data.add("No data found");
                nbSeriesLabel.setText("0 series");
            } else {
                for (Serie serie : source.getSeries()) {
                    data.add(serie);
                }
                nbSeriesLabel.setText(data.size() + " series");
            }
        } catch (Exception e) {
            data.add("No data found");
            nbSeriesLabel.setText("0 serie");
        }
        list.setListData(data);
        list.setSelectedIndex(0);
    }
}

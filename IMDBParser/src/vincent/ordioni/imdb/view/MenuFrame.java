package vincent.ordioni.imdb.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.MaskFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import vincent.ordioni.imdb.exception.AlreadyAddedException;
import vincent.ordioni.imdb.parser.IMDBParser;
import vincent.ordioni.imdb.parser.model.Serie;
import vincent.ordioni.imdb.util.Utils;

public class MenuFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -9015647408508331207L;

    private final JPanel contentPanel = new JPanel();

    private JTextField nameTextField;

    private JTextField dispNameTextField;

    private JFormattedTextField yearTextField;

    private JCheckBox nameCheckBox;

    private JCheckBox dispNameCheckBox;

    private JCheckBox yearCheckBox;

    private JTextField searchTextField;

    private JList list;

    private JButton goButton;

    private JToggleButton updateButton;

    private JButton addButton;

    public JButton removeButton;

    public JButton cancelButton;

    private JProgressBar progressBar;

    private Thread updateThread;

    public boolean isModified;

    private Thread progressThread;

    private ServerSocket serverSocket;

    public boolean hasChanged;

    /**
     * Create the dialog.
     */
    public MenuFrame() {

        try {
            SAXBuilder builder = new SAXBuilder();
            DateFormat dateFormat = new SimpleDateFormat();

            InputStream remoteInputStream = (new URL("http://vincent-ordioni.fr/applications/CheckListWPF/source.xml")).openStream();
            Document remoteDocument = builder.build(remoteInputStream);
            Date remoteDate = dateFormat.parse(remoteDocument.getRootElement().getAttributeValue("date"));

            Document document = remoteDocument;

            try {
                InputStream localInputStream = new FileInputStream(IMDBParser.SOURCE_PATH);
                Document localDocument = builder.build(localInputStream);
                Date localDate = dateFormat.parse(localDocument.getRootElement().getAttributeValue("date"));
                if (remoteDate.compareTo(localDate) < 0) {
                    document = localDocument;
                }
            } catch (Exception e1) {
            }

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, new FileOutputStream(IMDBParser.SOURCE_PATH));

        } catch (Exception e3) {
            e3.printStackTrace();
        }
        hasChanged = false;
        isModified = false;
        setIconImage(Toolkit.getDefaultToolkit().getImage(MenuFrame.class.getResource("/images/icon.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("IMDBParser - Add a Serie or Load Data");
        setResizable(false);
        setBounds(100, 100, 550, 450);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblName = new JLabel("Name :");
        lblName.setBounds(10, 11, 100, 14);
        contentPanel.add(lblName);

        JLabel lblDisplayName = new JLabel("Display name :");
        lblDisplayName.setBounds(10, 36, 100, 14);
        contentPanel.add(lblDisplayName);

        JLabel lblYear = new JLabel("Year :");
        lblYear.setBounds(10, 61, 100, 14);
        contentPanel.add(lblYear);

        nameTextField = new JTextField();
        nameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (nameTextField.getText().trim().length() == 0) {
                    nameCheckBox.setSelected(false);
                } else {
                    nameCheckBox.setSelected(true);
                }
            }
        });
        nameTextField.setBounds(120, 8, 387, 20);
        contentPanel.add(nameTextField);
        nameTextField.setColumns(100);

        dispNameTextField = new JTextField();
        dispNameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (dispNameTextField.getText().trim().length() == 0) {
                    dispNameCheckBox.setSelected(false);
                } else {
                    dispNameCheckBox.setSelected(true);
                }
            }
        });
        dispNameTextField.setBounds(120, 33, 120, 20);
        contentPanel.add(dispNameTextField);
        dispNameTextField.setColumns(10);

        try {
            yearTextField = new JFormattedTextField(new MaskFormatter("####"));
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
        yearTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    if (Integer.valueOf(yearTextField.getText()).toString().length() != 4) {
                        yearCheckBox.setSelected(false);
                    } else {
                        yearCheckBox.setSelected(true);
                    }
                } catch (NumberFormatException ee) {
                    yearCheckBox.setSelected(false);
                }
            }
        });
        yearTextField.setBounds(120, 58, 60, 20);
        contentPanel.add(yearTextField);
        yearTextField.setColumns(4);

        nameCheckBox = new JCheckBox("");
        nameCheckBox.setToolTipText("Enter a valid name");
        nameCheckBox.setBounds(515, 5, 21, 23);
        nameCheckBox.setEnabled(false);
        nameCheckBox.setIcon(new ImageIcon(MenuFrame.class.getResource("/images/sign_cacel.png")));
        nameCheckBox.setSelectedIcon(new ImageIcon(MenuFrame.class.getResource("/com/sun/java/swing/plaf/windows/icons/image-delayed.png")));
        contentPanel.add(nameCheckBox);

        dispNameCheckBox = new JCheckBox("");
        dispNameCheckBox.setToolTipText("Enter a valid display name");
        dispNameCheckBox.setBounds(515, 30, 21, 23);
        dispNameCheckBox.setEnabled(false);
        dispNameCheckBox.setIcon(new ImageIcon(MenuFrame.class.getResource("/images/sign_cacel.png")));
        dispNameCheckBox.setSelectedIcon(new ImageIcon(MenuFrame.class.getResource("/com/sun/java/swing/plaf/windows/icons/image-delayed.png")));
        contentPanel.add(dispNameCheckBox);

        yearCheckBox = new JCheckBox("");
        yearCheckBox.setToolTipText("Enter a valid year");
        yearCheckBox.setBounds(515, 55, 21, 23);
        yearCheckBox.setEnabled(false);
        yearCheckBox.setIcon(new ImageIcon(MenuFrame.class.getResource("/images/sign_cacel.png")));
        yearCheckBox.setSelectedIcon(new ImageIcon(MenuFrame.class.getResource("/com/sun/java/swing/plaf/windows/icons/image-delayed.png")));
        contentPanel.add(yearCheckBox);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 89, 544, 2);
        contentPanel.add(separator);

        JLabel lblSearch = new JLabel("Search :");
        lblSearch.setBounds(10, 102, 100, 14);
        contentPanel.add(lblSearch);

        searchTextField = new JTextField();
        searchTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getRootPane().setDefaultButton(goButton);
            }

            @Override
            public void focusLost(FocusEvent e) {
                getRootPane().setDefaultButton(addButton);
            }
        });
        searchTextField.setToolTipText("A+Serie+Name&year=2002");
        searchTextField.setBounds(120, 99, 351, 20);
        contentPanel.add(searchTextField);
        searchTextField.setColumns(10);

        goButton = new JButton("Go");
        goButton.setMnemonic('g');
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String search = searchTextField.getText().replaceAll(" ", IMDBParser.JOCKER).replaceAll("[*]", IMDBParser.JOCKER);

                    Set<Serie> series = null;

                    if (search.contains("&year=")) {
                        series = IMDBParser.search(IMDBParser.JOCKER + "+" + search);
                    } else {
                        series = new TreeSet<Serie>();
                        for (String aSearch : Utils.getSearchPermutations(search, IMDBParser.JOCKER)) {
                            if (!aSearch.startsWith(IMDBParser.JOCKER)) {
                                Set<Serie> aSeries = IMDBParser.search(IMDBParser.JOCKER + "+" + aSearch);
                                if (aSeries != null) {
                                    series.addAll(aSeries);
                                }
                            }
                            if (!aSearch.endsWith(IMDBParser.JOCKER)) {
                                Set<Serie> aSeries = IMDBParser.search(aSearch + "+" + IMDBParser.JOCKER);
                                if (aSeries != null) {
                                    series.addAll(aSeries);
                                }
                            }
                            if (!aSearch.startsWith(IMDBParser.JOCKER) && !aSearch.endsWith(IMDBParser.JOCKER)) {
                                Set<Serie> aSeries = IMDBParser.search(IMDBParser.JOCKER + "+" + aSearch + "+" + IMDBParser.JOCKER);
                                if (aSeries != null) {
                                    series.addAll(aSeries);
                                }
                            }
                            Set<Serie> aSeries = IMDBParser.search(aSearch);
                            if (aSeries != null) {
                                series.addAll(aSeries);
                            }
                        }
                    }

                    Vector<Object> data = new Vector<Object>();
                    if (series == null || series.isEmpty()) {
                        data.add("No results found");
                    } else {
                        for (Serie serie : series) {
                            data.add(serie);
                        }
                    }
                    list.setListData(data);
                } catch (Exception e1) {
                    Vector<Object> data = new Vector<Object>();
                    data.add("No results found");
                    list.setListData(data);
                }
            }
        });
        goButton.setBounds(483, 98, 49, 23);
        contentPanel.add(goButton);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 132, 526, 238);
        contentPanel.add(scrollPane);

        list = new JList();
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    Serie serie = (Serie) list.getSelectedValue();

                    nameTextField.setText(serie.getName());
                    dispNameTextField.setText(Utils.format(serie.getName()));
                    yearTextField.setText(serie.getYear().toString());

                    nameCheckBox.setSelected(true);
                    dispNameCheckBox.setSelected(true);
                    yearCheckBox.setSelected(true);
                } catch (Exception e1) {
                }
            }
        });
        scrollPane.setViewportView(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                updateButton = new JToggleButton("Update Data");
                updateButton.setPreferredSize(new Dimension(105, 26));
                updateButton.setMaximumSize(new Dimension(105, 26));
                updateButton.setMinimumSize(new Dimension(105, 26));
                updateButton.addActionListener(new ActionListener() {
                    @SuppressWarnings("deprecation")
                    public void actionPerformed(ActionEvent e) {
                        if (updateButton.isSelected()) {
                            updateButton.setText("Stop");

                            if (!isModified && updateThread != null && updateThread.isAlive()) {
                                updateThread.resume();
                                disableAll();
                            } else {
                                if (updateThread != null && updateThread.isAlive()) {
                                    updateThread.stop();
                                }
                                if (progressThread != null && progressThread.isAlive()) {
                                    progressThread.stop();
                                }

                                progressThread = new Thread(new ProgressBarWork());
                                progressThread.start();

                                updateThread = new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            disableAll();

                                            IMDBParser.loadData();

                                            FTPClient client = new FTPClient();
                                            client.connect("ftp.vincent-ordioni.fr");
                                            client.login("vincento", "tNd2iLuu");
                                            client.changeWorkingDirectory("/www/applications/CheckListWPF");

                                            File dataFile = new File(IMDBParser.DATA_PATH);
                                            File oldDataFile = new File(IMDBParser.DATA_PATH + ".old");

                                            client.retrieveFile(IMDBParser.DATA_PATH, new FileOutputStream(oldDataFile));

                                            String strData = FileUtils.readFileToString(dataFile);
                                            String strOldData = FileUtils.readFileToString(oldDataFile);

                                            if (!strData.equals(strOldData)) {
                                                client.storeFile(IMDBParser.DATA_PATH, new FileInputStream(dataFile));
                                            }

                                            client.disconnect();

                                            FileUtils.deleteQuietly(oldDataFile);

                                            progressBar.setValue(progressBar.getMaximum());
                                        } catch (Exception e1) {
                                            JOptionPane.showMessageDialog(MenuFrame.this, "Data not updated.", "Error", JOptionPane.ERROR_MESSAGE);
                                            enableAll();
                                            updateButton.setText("Update Data");
                                            updateButton.setSelected(false);
                                            return;
                                        }
                                        JOptionPane.showMessageDialog(MenuFrame.this, "Data updated.", "Success", JOptionPane.INFORMATION_MESSAGE);

                                        enableAll();
                                        updateButton.setText("Update Data");
                                        updateButton.setSelected(false);
                                        isModified = false;
                                    }
                                });

                                updateThread.start();
                            }
                        } else {
                            updateButton.setText("Update Data");
                            updateThread.suspend();
                            enableAll();
                        }
                    }
                });
                updateButton.setMnemonic('u');

                progressBar = new JProgressBar();
                progressBar.setStringPainted(true);
                buttonPane.add(progressBar);
                updateButton.setActionCommand("OK");
                buttonPane.add(updateButton);
            }
            {
                addButton = new JButton("Add Serie");
                addButton.setMnemonic('a');
                addButton.addActionListener(new ActionListener() {
                    @SuppressWarnings("deprecation")
                    public void actionPerformed(ActionEvent e) {

                        try {
                            boolean error = false;

                            String name = nameTextField.getText().trim();
                            String dispName = dispNameTextField.getText().trim();
                            Integer year = null;
                            try {
                                year = Integer.valueOf(yearTextField.getText());
                            } catch (NumberFormatException e1) {
                                error = true;
                                yearCheckBox.setSelected(false);
                            }
                            if (year != null) {
                                yearCheckBox.setSelected(true);
                            }

                            if (name.equals("")) {
                                error = true;
                                nameCheckBox.setSelected(false);
                            } else {
                                nameCheckBox.setSelected(true);
                            }

                            if (dispName.equals("")) {
                                error = true;
                                dispNameCheckBox.setSelected(false);
                            } else {
                                dispNameCheckBox.setSelected(true);
                            }

                            if (error) {
                                JOptionPane.showMessageDialog(MenuFrame.this, "Serie not added.\nWrong values.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            } else {
                                int choice = JOptionPane.showConfirmDialog(MenuFrame.this, "Do you really want to add this serie?", name, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (choice == JOptionPane.NO_OPTION) {
                                    return;
                                }
                                IMDBParser.addSerie(new Serie(name, dispName, year));
                                if (updateThread != null && updateThread.isAlive()) {
                                    updateThread.stop();
                                }
                                hasChanged = true;
                                isModified = true;
                            }
                        } catch (AlreadyAddedException e1) {
                            JOptionPane.showMessageDialog(MenuFrame.this, "Serie not added.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(MenuFrame.this, "Serie not added.\nValues are not good.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(MenuFrame.this, "Serie added.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    }
                });
                buttonPane.add(addButton);
                getRootPane().setDefaultButton(addButton);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.setMnemonic('c');

                removeButton = new JButton("Remove ...");
                removeButton.setMnemonic('r');
                buttonPane.add(removeButton);
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private void disableAll() {
        goButton.setEnabled(false);
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }

    private void enableAll() {
        goButton.setEnabled(true);
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    class ProgressBarWork implements Runnable {

        public ProgressBarWork() {
            if (serverSocket == null) {
                try {
                    serverSocket = new ServerSocket(IMDBParser.SOCKET_PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            try {
                if (serverSocket != null) {
                    Socket socket = serverSocket.accept();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null && !line.equals("")) {
                        StringTokenizer tokenizer = new StringTokenizer(line, "-");
                        progressBar.setMaximum(Integer.parseInt(tokenizer.nextToken()) + 1);
                        progressBar.setValue(Integer.parseInt(tokenizer.nextToken()));
                    }

                    reader.close();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

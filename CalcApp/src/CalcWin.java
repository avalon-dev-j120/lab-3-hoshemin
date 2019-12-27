import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static java.awt.Cursor.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CalcWin implements ActionListener, KeyListener {
    private JTextField io;
    private ArrayList<JButton> controls;
    private JPanel ui;

    private boolean startNumber = true;
    private String previousOp = "=";

    private final CalcLog logic;

    private CalcWin() {
        logic = new CalcLog();
        initUI();
    }

    private void initUI() {
        ui = new JPanel(new BorderLayout(2, 2));
        controls = new ArrayList<>();
        //  создаем поле ввода
        JPanel text = new JPanel(new GridLayout(0, 1, 3, 3));
        ui.add(text, BorderLayout.PAGE_START);
        io = new JTextField(15);
        io.setText("0");
        Font font = io.getFont();
        font = font.deriveFont(font.getSize() * 1.6f);
        io.setFont(font);
        io.setHorizontalAlignment(SwingConstants.RIGHT);
        io.setFocusable(false);
        text.add(io);


        JPanel buttons = new JPanel(new GridLayout(4, 4, 10, 10));
        buttons.setBorder(new EmptyBorder(10, 0, 10, 0));
        buttons.setCursor(new Cursor(HAND_CURSOR));
        ui.add(buttons, BorderLayout.CENTER);

        String[] keyValues = {
                "1", "2", "3", "+",
                "4", "5", "6", "-",
                "7", "8", "9", "*",
                "CE", "0", ".", "/"
        };

        for (String keyValue : keyValues) {
            addButton(buttons, keyValue);
        }

        JButton equals = new JButton("=");
        configureButton(equals);
        equals.setBorder(new EmptyBorder(10, 0, 10, 0));
        equals.setCursor(new Cursor(HAND_CURSOR));
        ui.add(equals, BorderLayout.AFTER_LAST_LINE);
        ui.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private JComponent getUI() {
        return ui;
    }

    private void addButton(Container container, String text) {
        JButton button = new JButton(text);
        configureButton(button);
        container.add(button);
    }

    private void configureButton(JButton button) {
        Font font = button.getFont();
        button.setFont(font.deriveFont(font.getSize() * 1.5f));
        button.addActionListener(this);
        button.addKeyListener(this);
        controls.add(button);
    }

    private void actionClear() {
        startNumber = true;
        io.setText("0");
        previousOp = "=";
        logic.setTotal("0");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals("CE")) {
            actionClear();
        } else if (command.equals("=")
                || command.equals("+")
                || command.equals("-")
                || command.equals("*")
                || command.equals("/")) {

            if (startNumber) {
                actionClear();
                io.setText("Ошибка ввода");
            } else {
                startNumber = true;
                try {



                    if (previousOp.equals("=")) {
                        logic.setTotal(io.getText());
                    } else if (previousOp.equals("+")) {
                        logic.add(io.getText());
                    } else if (previousOp.equals("-")) {
                        logic.subtract(io.getText());
                    } else if (previousOp.equals("*")) {
                        logic.multiply(io.getText());
                    } else if (previousOp.equals("/")) {
                        logic.divide(io.getText());
                    }

                    io.setText(logic.getTotalString());

                } catch (NumberFormatException ex) {
                    actionClear();
                    io.setText("Error");
                }

                previousOp = ae.getActionCommand();
            }


        } else if (command.equals(".")) {
            if (!io.getText().contains(".")) {
                io.setText(io.getText() + ".");
            }
        } else {
            if (startNumber) {

                io.setText(command);
                startNumber = false;
            } else {
                io.setText(io.getText() + command);
            }
        }
    }

    private JButton getButton(String text) {
        for (JButton button : controls) {
            String s = button.getText();
            if (text.endsWith(s)
                    || (s.equals("=")
                    && (text.equals("Equals") || text.equals("Enter")))) {

                return button;
            }
        }
        return null;
    }


    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        String keyText = KeyEvent.getKeyText(ke.getKeyCode());
        JButton button = getButton(keyText);
        if (button != null) {
            button.requestFocusInWindow();
            button.doClick();
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalcWin calc = new CalcWin();
            JFrame jFrame = new JFrame("CalcApp") {
                @Override
                public void paint(Graphics graphics) {
                    Dimension d = getSize();
                    Dimension m = getMaximumSize();
                    boolean resize = d.width > m.width || d.height > m.height;
                    d.width = Math.min(m.width, d.width);
                    d.height = Math.min(m.height, d.height);
                    if (resize) {
                        Point point = getLocation();
                        setVisible(false);
                        setSize(d);
                        setLocation(point);
                        setVisible(true);
                    }
                    super.paint(graphics);
                }
            };
            jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jFrame.setContentPane(calc.getUI());
            
            jFrame.pack();
            jFrame.setMinimumSize(jFrame.getSize());
            jFrame.setMaximumSize(new Dimension((int) (jFrame.getSize().width * 1.3), (int) (jFrame.getSize().height * 1.3)));
            
            jFrame.setLocationByPlatform(true);
            jFrame.setVisible(true);
        });
    }
}


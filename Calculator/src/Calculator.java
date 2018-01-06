import annotations.Operation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Calculator extends JFrame{
    final String OPS_FOLDER = "operations";
    private JTextField inputField;
    private JLabel resultField;
    private JPanel ops;

    public static void main(String[] args) {
        new Calculator();
    }

    public Calculator() {
        inputField = new JTextField();
        resultField = new JLabel();
        ops = new JPanel();
        Container c = this.getContentPane();
        JPanel fields = new JPanel();
        fields.setLayout(new GridLayout(2, 1));
        fields.add(inputField);
        fields.add(resultField);
        c.add(fields, BorderLayout.NORTH);

        c.add(ops, BorderLayout.CENTER);
        loadOps();
        setSize(300, 300);
        setVisible(true);
    }

    private void loadOps() {
        File folder = new File(OPS_FOLDER);
        File[] classes = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });

        if (classes == null){
            System.err.println("Load plugins into \"operations\" subdirectory!");
            return;
        }
        for (final File fileEntry : classes) {

            if (!fileEntry.isDirectory()) {
                tryLoadOperation(fileEntry);

            }
        }
    }
    private boolean tryLoadOperation(File file)  {

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class<?> clazz = null;
        try {
             clazz = cl.loadClass(OPS_FOLDER + "." + file.getName().replaceFirst("[.][^.]+$", ""));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        Annotation oper = clazz.getAnnotation(Operation.class);
        if (oper != null){
            addButton(clazz);
        }
        else return false;

        return true;
    }

    private boolean addButton(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        if (methods.length > 0) {
            JButton button = new JButton();
            button.setAction(new AbstractAction(methods[0].getName()) {
                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        String res = (String) methods[0].invoke(null, inputField.getText());
                        resultField.setText(res);
                    } catch (Exception e) {
                    }
                }
            });
            ops.add(button);
            return true;
        }
        return false;
    }

}
